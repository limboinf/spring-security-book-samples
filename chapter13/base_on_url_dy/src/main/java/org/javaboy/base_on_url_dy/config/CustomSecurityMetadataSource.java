package org.javaboy.base_on_url_dy.config;

import org.javaboy.base_on_url_dy.model.Menu;
import org.javaboy.base_on_url_dy.model.Role;
import org.javaboy.base_on_url_dy.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SecurityMetadataSource接口负责提供受保护对象所需要的权限。
 * 在本案例中，受保护对象所需要的权限保存在数据库中，
 * 所以我们可以通过自定义类继承自FilterInvocationSecurityMetadataSource，并重写getAttributes方法来提供受保护对象所需要的权限
 */
@Component
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Autowired
    MenuService menuService;
    AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 得到受保护对象所需要的权限
     *
     * @param object 所保护的对象, 基于URL地址的权限控制中，受保护对象就是FilterInvocation
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        // 从受保护的对象FilterInvocation 中获取当前请求URL地址
        String requestURI = ((FilterInvocation) object).getRequest().getRequestURI();

        // 从数据库中查询所有菜单数据（每条包含访问所需权限）
        List<Menu> allMenu = menuService.getAllMenu();
        for (Menu menu : allMenu) {
            if (antPathMatcher.match(menu.getPattern(), requestURI)) {
                String[] roles = menu.getRoles().stream().map(Role::getName).toArray(String[]::new);
                return SecurityConfig.createList(roles);
            }
        }

        // 返回null时，允许访问受保护对象
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
