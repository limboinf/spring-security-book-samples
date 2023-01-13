package org.javaboy.based_on_method;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * 基于方法的权限管理
 * @EnableGlobalMethodSecurity 开启权限注解
 * 具体case 见 BasedOnMethodApplicationTests
 */
@EnableGlobalMethodSecurity(
        // 开启Spring Security提供的四个权限注解，@PostAuthorize、@PostFilter、@PreAuthorize以及@PreFilter，这四个注解支持权限表达式
        prePostEnabled = true,
        // 开启Spring Security提供的@Secured注解，不支持权限表达式
        securedEnabled = true,
        // 开启JSR-250提供的注解，@DenyAll, @PermitAll, @RolesAllowed 注解，不支持权限表达式
        jsr250Enabled = true
)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

}
