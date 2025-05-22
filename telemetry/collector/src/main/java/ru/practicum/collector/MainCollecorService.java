package ru.practicum.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "ru.practicum")
public class MainCollecorService {
    public static void main(String[] args) {
        SpringApplication.run(MainCollecorService.class,args);
    }
}