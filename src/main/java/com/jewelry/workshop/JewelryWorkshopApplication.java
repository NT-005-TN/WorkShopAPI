package com.jewelry.workshop;

import com.fasterxml.jackson.databind.deser.std.StringArrayDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JewelryWorkshopApplication {
    public static void main(String[] args) {
        SpringApplication.run(JewelryWorkshopApplication.class, args);
        System.out.println("✅ API для ювелирной мастерской запущено!");
    }
}
