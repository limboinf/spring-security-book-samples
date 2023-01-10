package org.javaboy.json_login;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 重写父类configure(AuthenticationManagerBuilder auth) 方法定义一个登录用户【全局】
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy")
                .password("{noop}123")
                .roles("admin");
    }

    /**
     * 获取一个AuthenticationManager实例时，有两种不同的方式
     * <br>
     * 方式1：重写父类authenticationManagerBean方法提供一个 AuthenticationManager 实例
     * 这种方式是获取局部的AuthenticationManager实例
     * 在实际应用中，如果需要自己配置一个AuthenticationManager实例，大部分情况下，都是通过重写authenticationManagerBean方法来获取。
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 获取一个AuthenticationManager实例时，有两种不同的方式
     * <br>
     * 方式2：重写父类的authenticationManager方法获取
     * 这种方式是获取全局的AuthenticationManager实例
     *
     * 注意：
     * LoginFilter作为过滤器链中的一环，显然应该配置局部的AuthenticationManager实例，
     * 因为如果将全局的AuthenticationManager实例配置给LoginFilter，则局部Authentication Manager实例所对应的用户就会失效 （javagirl局部用户则无法登录）
     */
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * 自定义登录过滤器
     * <br>
     *
     * example:
     * curl -X POST http://localhost:8080/login  -H "Content-Type: application/json" -d '{"username":"javagirl","password":"123"}'
     *
        {
            "authorities": [{ "authority": "ROLE_admin" } ],
            "details": {
                "remoteAddress": "0:0:0:0:0:0:0:1",
                "sessionId": "90D627C45B47FC19AE96AD2883D8F4EA"
            },
            "authenticated": true,
            "principal": {
                "password": null,
                "username": "javagirl",
                "authorities": [ { "authority": "ROLE_admin" } ],
                "accountNonExpired": true,
                "accountNonLocked": true,
                "credentialsNonExpired": true,
                "enabled": true
            },
            "credentials": null,
            "name": "javagirl"
        }
     */
    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        // 设置AuthenticationManager
         loginFilter.setAuthenticationManager(authenticationManagerBean());
//        loginFilter.setAuthenticationManager(authenticationManager());    // 注意实验下这两种不同的构造AuthenticationManager方式
        // 设置登录成功后回调
        loginFilter.setAuthenticationSuccessHandler((req, resp, auth) -> {
            resp.setContentType("application/json;charset=utf-8");
            PrintWriter out = resp.getWriter();
            out.write(new ObjectMapper().writeValueAsString(auth));
        });
        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 定义局部登录用户
        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
        users.createUser(User.withUsername("javagirl").password("{noop}123").roles("admin").build());

        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .csrf().disable()
                .userDetailsService(users);

        // 调用HttpSecurity addFilterAt方法将 loginFilter过滤器添加到 UsernamePasswordAuthenticationFilter过滤器所在的位置
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
