package com.llyinatech.houserental;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.llyinatech.houserental.mapper")
public class HouseRentalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseRentalBackendApplication.class, args);
    }

}
