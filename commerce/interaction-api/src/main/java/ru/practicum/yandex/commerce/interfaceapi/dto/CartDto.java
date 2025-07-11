package ru.practicum.yandex.commerce.interfaceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO корзины пользователя.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private UUID cartId;
    private UUID userId;
    private List<ProductInCartDto> products;
    private boolean active;
}