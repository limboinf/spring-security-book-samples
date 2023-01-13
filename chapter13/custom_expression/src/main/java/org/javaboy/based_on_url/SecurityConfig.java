package org.javaboy.based_on_url;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 自定义权限验证表达式
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy") .password("{noop}123") .roles("ADMIN")
                .and()
                .withUser("javagirl") .password("{noop}123") .roles("USER");
    }

    /**
     * 自定义 PermissionExpression 类并注入到Spring容器中，
     * 可以在 `access` 方法中通过`@` 符号引用一个Bean并调用其方法
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()    // 创建 ExpressionUrlAuthorizationConfigurer ，必须至少有一个 antMatchers 否则启动报错
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").access("hasRole('USER')")
                .antMatchers("/hello/{userId}").access("@permissionExpression.checkId(authentication, #userId)")
                .antMatchers("/hi").access("isAuthenticated() and @permissionExpression.check(request)")
                .anyRequest().access("isAuthenticated()")
                .and()
                .formLogin()
                .and()
                .csrf().disable();
    }
}
