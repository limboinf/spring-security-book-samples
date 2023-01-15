package org.javaboy.auth_server_jwt;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 提供一个公钥接口，资源服务器将从该接口中获取到公钥，进而完成对JWT的校验
 */
@RestController
public class HelloController {
    @Autowired
    JWKSet jwkSet;

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping(value = "/oauth2/keys")
    public String keys() {
        return jwkSet.toString();
    }
}