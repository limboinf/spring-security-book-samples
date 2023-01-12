package org.javaboy.sessionshare;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

/**
 * 集群 session 方案(不适用与无状态登录)
 * <br>
 * <ul>
 *     <li>Session粘滞（会话保持）通过nginx 一致性hash，将hash结果相同的请求落到相同服务器上</li>
 *     <li>Session共享：如Redis存储session做共享 (利用spring-session可以方便地实现Session的管理)</li>
 * </ul>
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 会话存储和加载的Repository, 实现类有 RedisIndexedSessionRepository
     */
    @Autowired
    FindByIndexNameSessionRepository sessionRepository;

    /**
     * 会话注册表
     */
    @Bean
    SpringSessionBackedSessionRegistry sessionRegistry() {
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }

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
                .formLogin()
                .and()
                .csrf()
                .disable()
                .sessionManagement()
                .maximumSessions(1)
                .sessionRegistry(sessionRegistry());
    }

}
