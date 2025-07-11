package ru.practicum.yandex.commerce.interfaceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;

/**
 * DTO товара в корзине.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInCartDto {
    private UUID productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
}