package ru.practicum.yandex.commerce.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.practicum.yandex.commerce.interfaceapi.client")
public class ShoppingDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingDeliveryApplication.class, args);
    }
}
