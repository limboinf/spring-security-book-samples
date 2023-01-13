package org.javaboy.based_on_method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.test.context.support.WithMockUser;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BasedOnMethodApplicationTests {

    @Autowired
    HelloService helloService;

    @Test
    @DisplayName("测试@PostFilter")
    @WithMockUser(roles = "ADMIN")
    void postFilterTest01() {

        // 在目标方法执行后对结果进行过滤
        // @PostFilter("filterObject.id%2==0")

        List<User> all = helloService.getAll();
        assertNotNull(all);
        assertEquals(5, all.size());
        assertEquals(2, all.get(1).getId());
    }

    @Test
    @DisplayName("测试@PreAuthorize")
    @WithMockUser(roles = "ADMIN", username = "javaboy")
    void preauthorizeTest01() {

        // 在目标方法执行之前进行权限验证
        // @PreAuthorize("hasRole('ADMIN') and authentication.name=='javaboy'")

        String hello = helloService.hello();
        assertNotNull(hello);
        assertEquals("hello", hello);
    }
    @Test
    @DisplayName("测试@PreAuthorize 2")
    @WithMockUser(username = "javaboy")
    void preauthorizeTest02() {

        // @PreAuthorize("authentication.name == #name")

        String hello = helloService.hello("javaboy");
        assertNotNull(hello);
        assertEquals("hello:javaboy", hello);
    }

    @Test
    @DisplayName("测试@PreFilter")
    @WithMockUser(username = "javaboy")
    void preFilterTest01() {

        // 在目标方法执行之前对方法入参进行过滤
        // @PreFilter(value = "filterObject.id % 2 != 0", filterTarget = "users")

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(i, "javaboy:" + i));
        }
        helloService.addUsers(users, 99);
    }


    @Test
    @DisplayName("测试@PostAuthorize")
    @WithMockUser(username = "javaboy")
    void postAuthorizeTest01() {

        // 在目标方法执行之后进行权限验证
        // @PostAuthorize("returnObject.id == 1")

        User user = helloService.getUserById(1);
        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("javaboy", user.getUsername());
    }


    @Test
    @DisplayName("测试@Secured")
    @WithMockUser(roles = "ADMIN")
    void securedTest01() {

        // 访问目标方法必须具备相应角色
        // @Secured({"ROLE_ADMIN","ROLE_USER"})

        User user = helloService.getUserByUsername("javaboy");
        assertNotNull(user);
        assertEquals(99, user.getId());
        assertEquals("javaboy", user.getUsername());
    }


    @Test
    @DisplayName("测试@DenyAll")
    @WithMockUser(username = "javaboy")
    void denyAllTest01() {

        // 禁止所有访问
        // @DenyAll
        AccessDeniedException thrown = Assertions.assertThrows(AccessDeniedException.class, () -> helloService.denyAll());
        Assertions.assertEquals("不允许访问", thrown.getMessage());
    }

    @Test
    @DisplayName("测试@PermitAll")
    @WithMockUser(username = "javaboy")
    void permitAllTest01() {

        // 允许所有访问
        // @PermitAll

        String s = helloService.permitAll();
        assertNotNull(s);
        assertEquals("PermitAll", s);
    }


    @Test
    @DisplayName("测试@RolesAllowed")
    @WithMockUser(roles = "ADMIN")
    void rolesAllowedTest01() {

        // 访问目标方法必须具备相应角色
        // @RolesAllowed({"ADMIN","USER"})

        String s = helloService.rolesAllowed();
        assertNotNull(s);
        assertEquals("RolesAllowed", s);
    }

}
