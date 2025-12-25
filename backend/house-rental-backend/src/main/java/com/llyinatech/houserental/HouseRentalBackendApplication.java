package com.llyinatech.houserental;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.llyinatech.houserental.mapper")
public class HouseRentalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseRentalBackendApplication.class, args);
    }

}
