package ru.practicum.yandex.commerce.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.CartDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductInCartDto;

import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @PostMapping("/add")
    CartDto addProductToCart(@RequestParam UUID userId, @RequestBody ProductInCartDto product);

    @PostMapping("/remove")
    CartDto removeProductFromCart(@RequestParam UUID userId, @RequestParam UUID productId);

    @GetMapping("/{userId}")
    CartDto getCart(@PathVariable UUID userId);
}
