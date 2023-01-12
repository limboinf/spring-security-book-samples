package org.javaboy.http_basic;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * HTTP Basic authentication && HTTP Digest authentication
 * <br>
 * 这种认证方式通过HTTP请求头来提供认证信息，而不是通过表单登录
 *
 * 请求后Response WWW-Authenticate: Basic realm="Realm"  --> 登陆成功 --> Authorization: Basic amF2YWJveToxMjM=
 *
 * 认证方式有Basic、Bearer（OAuth2.0认证）、Digest（HTTP摘要认证）等取值。
 *
 * <ul>
 *     <li>HTTP Basic authentication 将用户的登录用户名／密码经过Base64编码之后，放在请求头的Authorization字段中，从而完成用户身份的认证</li>
 *     <li>HTTP Digest authentication 摘要认证</li>
 * </ul>
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy")
                .password("{noop}123")
                .roles("admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin().disable()
                .csrf().disable();
    }
}
