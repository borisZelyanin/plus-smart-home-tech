package ru.practicum.yandex.commerce.shopping.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.ChangeProductQuantityRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.ShoppingCartDto;
import ru.practicum.yandex.commerce.shopping.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    // Получить корзину пользователя
    @GetMapping
    public ResponseEntity<ShoppingCartDto> getShoppingCart(@RequestParam String username) {
        return ResponseEntity.ok(shoppingCartService.getCart(username));
    }

    // Добавить товары в корзину
    @PutMapping
    public ResponseEntity<ShoppingCartDto> addProductsToCart(
            @RequestParam String username,
            @RequestBody Map<String, Long> productIdToQuantity
    ) {
        return ResponseEntity.ok(shoppingCartService.addProducts(username, productIdToQuantity));
    }

    // Изменить количество товара
    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(
            @RequestParam String username,
            @RequestBody ChangeProductQuantityRequest request
    ) {
        return ResponseEntity.ok(shoppingCartService.changeQuantity(username, request));
    }

    // Удалить товары из корзины
    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeProductsFromCart(
            @RequestParam String username,
            @RequestBody List<String> productIds
    ) {
        return ResponseEntity.ok(shoppingCartService.removeProducts(username, productIds));
    }

    // Деактивировать корзину
    @DeleteMapping
    public ResponseEntity<Void> deactivateShoppingCart(@RequestParam String username) {
        shoppingCartService.deactivateCart(username);
        return ResponseEntity.ok().build();
    }
}