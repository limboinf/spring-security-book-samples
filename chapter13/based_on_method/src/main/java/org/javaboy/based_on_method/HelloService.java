package org.javaboy.based_on_method;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于方法的权限管理
 */
@Service
public class HelloService {

    // 在目标方法执行之前进行权限验证
    @PreAuthorize("hasRole('ADMIN') and authentication.name == 'javaboy'")
    public String hello() {
        return "hello";
    }

    @PreAuthorize("authentication.name == #name")
    public String hello(String name) {
        return "hello:" + name;
    }

    // 在目标方法执行之前对方法入参进行过滤
    @PreFilter(value = "filterObject.id % 2 != 0", filterTarget = "users")
    public void addUsers(List<User> users, Integer other) {
        System.out.println("users = " + users);
    }

    // 在目标方法执行之后进行权限验证
    @PostAuthorize("returnObject.id == 1")
    public User getUserById(Integer id) {
        return new User(id, "javaboy");
    }

    // 在目标方法执行后对结果进行过滤
    @PostFilter("filterObject.id%2==0")
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(i, "javaboy:" + i));
        }
        return users;
    }

    // 访问目标方法必须具备相应角色
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    public User getUserByUsername(String username) {
        return new User(99, username);
    }

    @Secured({"READ_USER"})
    public User getUserByUsername2(String username) {
        return new User(99, username);
    }

    // 允许所有访问
    @PermitAll
    public String permitAll() {
        return "PermitAll";
    }

    // 禁止所有访问
    @DenyAll
    public String denyAll() {
        return "DenyAll";
    }

    // 访问目标方法必须具备相应角色
    @RolesAllowed({"ADMIN","USER"})
    public String rolesAllowed() {
        return "RolesAllowed";
    }
}
