package org.javaboy.formlogin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Slf4j
@RestController
public class UserController {
    @GetMapping("/user")
    public void userInfo() {
        printAuthenticationInfo();
    }

    private static void printAuthenticationInfo() {
        // 从 SecurityContextHolder 获取 Authentication对象
        // 这里由于是表单登录，是UsernamePasswordAuthenticationToken实现类
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 获取 principal name
        String name = authentication.getName();
        // 获取权限列表
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info("get Authentication: {}, name: {}, authorities: {}, className: {}",
                authentication, name, authorities, authentication.getClass());
    }
}
