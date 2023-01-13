package org.javaboy.based_on_url;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 基于URL地址的权限管理
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 角色继承
     * 一般通过RoleHierarchyImpl类来定义角色的层级关系
     *
     * @return {@link RoleHierarchy}
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        // A > B > C (A继承B，B继承C， A的权限最大，B次之）
        // hierarchy.setHierarchy("ROLE_A > ROLE_B > ROLE_C");
        return hierarchy;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("javaboy").password("{noop}123").roles("ADMIN")
                .and()
                .withUser("javagirl").password("{noop}123").roles("USER")
                .and()
                .withUser("itboyhub").password("{noop}123").authorities("READ_INFO");
    }

    /**
     * 配置
     * <ul>
     *     <li>基于内存定义的用户，会自动给角色加ROLE_前缀，而权限则不会加任何前缀</li>
     *     <li>hasRole表达式自动添加上ROLE_前缀</li>
     *     <li>hasAuthority表达式不会添加上任何前缀</li>
     *     <li>access方法使用权限表达式</li>
     *     <li>顺序很关键，请求会依次向下匹配</li>
     * </ul>
     *
     * @param http http
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/user/**").access("hasAnyRole('USER', 'ADMIN')")
                .antMatchers("/user/**").access("hasRole('USER')")  // 由于上面配置了角色继承，ROLE_ADMIN继承自ROLE_USER具备其权限
                .antMatchers("/getinfo").hasAuthority("READ_INFO")
                .anyRequest().access("isAuthenticated()")
                .and()
                .formLogin()
                .and()
                .csrf().disable();

    }
}
