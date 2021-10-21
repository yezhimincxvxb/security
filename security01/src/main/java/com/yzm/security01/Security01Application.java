package com.yzm.security01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Security01Application {

    public static void main(String[] args) {
        SpringApplication.run(Security01Application.class, args);
    }

}
