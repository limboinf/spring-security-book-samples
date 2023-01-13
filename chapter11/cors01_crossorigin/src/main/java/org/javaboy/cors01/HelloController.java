package org.javaboy.cors01;

import org.springframework.web.bind.annotation.*;

/**
 * @CrossOrigin注解来标识支持跨域
 */
@RestController
@CrossOrigin(
        origins = "http://localhost:8081",      // 允许的域，* 表全部
        allowCredentials = "",                  // 浏览器是否应当发送凭证信息，如Cookie
        allowedHeaders = "*",                   // 被允许的请求头字段, * 表全部
        methods = {RequestMethod.POST},         // 允许的请求方法, * 表全部
        maxAge = 60 * 30,                       // 预检请求的有效性，默认1800秒，有效期内不会再次发送Option 预检请求
        exposedHeaders = {"Access-Control-Request-Headers", "Access-Control-Request-Method"}    // 响应头可暴露字段
)
public class HelloController {
    @PostMapping("/post")
    public String post() {
        return "hello post";
    }

    @CrossOrigin(
            origins = "http://localhost:8081",
            allowedHeaders = "*",
            methods = {RequestMethod.PUT, RequestMethod.OPTIONS},
            maxAge = 60 * 30,
            exposedHeaders = {"Access-Control-Max-Age"})
    @PutMapping("/put")
    public String put() {
        return "hello put";
    }

    @GetMapping("/get")
    public String get() {
        return "hello get";
    }
}
