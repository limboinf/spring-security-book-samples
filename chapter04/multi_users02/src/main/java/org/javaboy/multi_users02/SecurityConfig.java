package org.javaboy.multi_users02;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * 用户定义方式2
 * 全局AuthenticationManager 定义用户
 * <p>
 * 同时定义了局部 AuthenticationManager
 * 登录时，先全局后局部，即系统拿着我们输入的用户名／密码，首先和javaboy/123进行匹配，如果匹配不上的话，再去和limbo/123进行匹配
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 由于默认的全局AuthenticationManager在配置时会从Spring容器中查找 UserDetailsService实例
     * <p>
     * 所以针对全局 AuthenticationManager配置用户，只需要在Spring容器中注入一个 UserDetailsService实例即可
     */
    @Bean
    UserDetailsService us() {
        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
        UserDetails customUserDetails = User.withUsername("limbo").password("{noop}123").roles("admin").build();
        users.createUser(customUserDetails);
        return users;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 局部AuthenticationManager定义用户
        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
        users.createUser(User.withUsername("javaboy").password("{noop}123").roles("admin").build());

        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll()
                .and()
                .userDetailsService(users)
                .csrf().disable();
    }
}

