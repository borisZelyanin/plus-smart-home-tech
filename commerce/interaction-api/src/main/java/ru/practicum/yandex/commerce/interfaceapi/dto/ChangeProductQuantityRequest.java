package ru.practicum.yandex.commerce.interfaceapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductQuantityRequest {
    private UUID productId;
    private long newQuantity;
}