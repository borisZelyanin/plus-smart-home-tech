package ru.practicum.yandex.commerce.shopping.cart.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.cart.ShoppingCartDto;
import ru.practicum.yandex.commerce.shopping.cart.model.ShoppingCart;

import java.util.HashMap;

@Component
public class ShoppingCartMapper {

    public ShoppingCartDto toDto(ShoppingCart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getId())
                .products(new HashMap<>(cart.getProducts()))
                .build();
    }

    public ShoppingCart toEntity(ShoppingCartDto dto) {
        return ShoppingCart.builder()
                .id(dto.getShoppingCartId())
                .products(new HashMap<>(dto.getProducts()))
                .build();
    }
}