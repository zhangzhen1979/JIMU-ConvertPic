package com.thinkdifferent.convertpic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@EnableDiscoveryClient
@EnableOpenApi
public class ConvertpicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvertpicApplication.class, args);
    }

}
