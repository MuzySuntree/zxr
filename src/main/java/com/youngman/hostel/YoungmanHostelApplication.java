package com.youngman.hostel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 */
@SpringBootApplication
@MapperScan("com.youngman.hostel.mapper")
public class YoungmanHostelApplication {

    public static void main(String[] args) {
        SpringApplication.run(YoungmanHostelApplication.class, args);
    }
}
