package com.thrblock.sweeper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan(value = {"com.thrblock.sweeper"})
public class SweeperApplication implements CommandLineRunner {
    
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        new SpringApplicationBuilder(SweeperApplication.class)
            .web(WebApplicationType.NONE)
            .headless(false)
            .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        
    }
}
