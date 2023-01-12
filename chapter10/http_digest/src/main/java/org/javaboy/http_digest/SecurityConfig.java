package org.javaboy.http_digest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

/**
 * HTTP Digest authentication HTTP 摘要认证
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(digestAuthenticationEntryPoint())
                .and()
                .addFilter(digestAuthenticationFilter());
    }

    /**
     * 摘要式身份验证入口点
     * <br>
     * 当用户发起一个没有认证的请求时，由该实例进行处理
     */
    DigestAuthenticationEntryPoint digestAuthenticationEntryPoint() {
        DigestAuthenticationEntryPoint entryPoint = new DigestAuthenticationEntryPoint();
        // nonce：服务端生成的一个随机字符串，在客户端生成摘要信息时会用到该随机字符串
        entryPoint.setNonceValiditySeconds(3600);
        // Realm：服务端返回的标识访问资源的安全域
        entryPoint.setRealmName("myrealm");
        entryPoint.setKey("javaboy");
        return entryPoint;
    }
    DigestAuthenticationFilter digestAuthenticationFilter() throws Exception {
        DigestAuthenticationFilter filter = new DigestAuthenticationFilter();
        filter.setAuthenticationEntryPoint(digestAuthenticationEntryPoint());
        filter.setUserDetailsService(userDetailsServiceBean());
        // 进行密码加密：将username + ":" + realm + ":" +password使用MD5算法计算其消息摘要，将计算结果作为用户密码
        // --> e7ecfd3f08e6960f154e1ff29079fbd3
        filter.setPasswordAlreadyEncoded(true);
        return filter;
    }
    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // 用户名是javaboy，realm是myrealm，用户密码是123，计算的消息摘要 见 HttpDigestApplicationTests
        manager.createUser(User.withUsername("javaboy").password("e7ecfd3f08e6960f154e1ff29079fbd3").roles("admin").build());
        return manager;
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
