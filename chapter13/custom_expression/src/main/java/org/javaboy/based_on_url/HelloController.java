package org.javaboy.based_on_url;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
public class HelloController {
    @GetMapping("/hello/{userId}")
    public String hello(@PathVariable Integer userId) {
        return "hello " + userId;
    }

    @GetMapping("/hi")
    public String hello2User(String username) {
        return "hello " + username;
    }

    @GetMapping("/admin/hello")
    @PreAuthorize("@has")
    public String admin() {
        return "hello admin";
    }

    @GetMapping("/user/hello")
    public String user() {
        return "hello user";
    }

}
