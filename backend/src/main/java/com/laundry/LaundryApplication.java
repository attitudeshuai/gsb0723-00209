package com.laundry;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 洗衣店管理系统启动类
 */
@SpringBootApplication
@MapperScan("com.laundry.mapper")
public class LaundryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LaundryApplication.class, args);
    }
}
