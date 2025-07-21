package ru.practicum.yandex.commerce.shopping.cart.service;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.Feign.ChangeProductQuantityRequest;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {
    ShoppingCartDto getCart(String username);

    ShoppingCartDto addProducts(String username, Map<String, Long> productIdToQuantity); // <- String, not UUID

    ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request);

    ShoppingCartDto removeProducts(String username, List<String> productIds); // <- String, not UUID

    void deactivateCart(String username);
}