package org.javaboy.httpfirewall;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * HttpFirewall
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    HttpFirewall httpFirewall() {
        // StrictHttpFirewall 严格防火墙模式（默认）
        StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowedHostnames((hostname) -> hostname.equalsIgnoreCase("local.javaboy.org"));
        return strictHttpFirewall;
    }

//    @Bean
//    HttpFirewall httpFirewall() {
//        // DefaultHttpFirewall: 宽松防火墙模式
//        return new DefaultHttpFirewall();
//    }

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
                .successHandler((req,resp,auth)->{
                    resp.getWriter().write("success");
                })
                .and()
                .csrf()
                .disable();
    }
}
