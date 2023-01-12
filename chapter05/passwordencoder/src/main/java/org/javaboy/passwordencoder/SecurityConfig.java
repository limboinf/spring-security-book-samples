package org.javaboy.passwordencoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 密码加密
 * <br>
 * test case 见 PasswordencoderApplicationTests
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 如果将一个BCryptPasswordEncoder实例注册到Spring容器中，这将代替默认的DelegatingPasswordEncoder
     * <br>
     * 则在 configure 可以 .password("$2a$10$XtBXprcqjT/sGPEOY5y1eurS.V.9U7/M5RD1i32k1uAhXQHK4//U6") 配置一个加密后的密码
     * <br>
     * 由于默认使用的是DelegatingPasswordEncoder，所以也可以不配置PasswordEncoder实例，只在密码前加上前缀, 如 .password("{bcrypt}xxx")
     *
     * @return {@link PasswordEncoder}
     */
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 默认使用的是DelegatingPasswordEncoder（默认用BCryptPasswordEncoder) 则可创建一个局部用户
        auth.inMemoryAuthentication()
                .withUser("javaboy")
                // BCryptPasswordEncoder 使用 bcrypt算法加密, BCryptPasswordEncoder是自动带盐的加密方式
                .password("{bcrypt}$2a$10$XtBXprcqjT/sGPEOY5y1eurS.V.9U7/M5RD1i32k1uAhXQHK4//U6")
                .roles("admin")

                .and()

                // 再创建一个局部明文用户
                .withUser("江南一点雨")
                .password("{noop}123")
                .roles("user");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(authentication));
                })
                .and()
                .csrf().disable();
    }
}
