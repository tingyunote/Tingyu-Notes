package org.bifu.distributed.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证模块启动程序
 * 
 */
@SpringBootApplication
@MapperScan("org.bifu.distributed.auth.dao")
public class AuthApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}
	
}
