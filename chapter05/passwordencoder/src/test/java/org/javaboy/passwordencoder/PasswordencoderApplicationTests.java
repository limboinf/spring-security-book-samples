package org.javaboy.passwordencoder;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class PasswordencoderApplicationTests {

    @Test
    void bCryptPasswordEncoderTest() {
        // 使用BCryptPasswordEncoder加密后的字符串就已经“带盐”了，即使相同的明文每次生成的加密字符串都不相同。
        // BCryptPasswordEncoder() 构造函数有一个强度字段，默认是10(可根据服务器性能进行调整)，以确保密码验证时间约为1秒钟
        // （官方建议密码验证时间为1秒钟，这样既可以提高系统安全性，又不会过多影响系统运行性能）。
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 加密
        String e1 = encoder.encode("123");
        String e2 = encoder.encode("123");
        System.out.println(e1);     // $2a$10$xUwVqn6fokEOr./rhWOMpOCz93Ei5MiHQEw3PS2s47lMTCRCUYm/q
        System.out.println(e2);     // $2a$10$cprfzvq7Je.tbL18NqlOGeGuFFp23y6O8i/f8UGTWzJ3WslUnYhuO
        System.out.println(e1.equals(e2));  // false

        // 匹配验证
        System.out.println(encoder.matches("123", e1)); // true
        System.out.println(encoder.matches("123", e2)); // true
    }

    /**
     * DelegatingPasswordEncoder主要用来代理上面不同的密码加密方案
     * <br/>
     * {@link org.springframework.security.crypto.factory.PasswordEncoderFactories}
     */
    @Test
    void delegatingPasswordEncoderTest() {
        // 定义一个密码加密方案ID
        String idForEncode = "bcrypt";
        // encoders 存储每种密码加密方案的ID和对应的加密类
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(idForEncode, new BCryptPasswordEncoder());
        encoders.put("noop", NoOpPasswordEncoder.getInstance());

        // 直接构造DelegatingPasswordEncoder实例
        // 默认加密 BCryptPasswordEncoder
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);

        String encode = delegatingPasswordEncoder.encode("123");
        System.out.println(encode);     // {bcrypt}$2a$10$aDNRTpoXHofj4k14gHM/JOTlUkQXzFco1ni0iafuoiPiO58Q1fFjq
        // 其中{id} 来包裹生成的加密方案的id, 如上 bcrypt
        // 常用的还有 {noop}123 表示明文存储

        // or using PasswordEncoderFactories

        PasswordEncoder delegatingPasswordEncoder2 = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encode2 = delegatingPasswordEncoder2.encode("123");
        System.out.println(encode2);    // {bcrypt}$2a$10$GpcmBQL7uIaMorlHdthEeO8jGmsDkJf.ZkJq3TWSuxnOZ0UxokJPu
    }

}