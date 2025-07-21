package ru.practicum.yandex.commerce.delivery.interfaceapi.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.store.ProductDto;


import java.util.UUID;

@FeignClient(name = "shopping-store", url = "http://shopping-store")
public interface ProductClient {

    @GetMapping("/api/v1/products/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);
}
