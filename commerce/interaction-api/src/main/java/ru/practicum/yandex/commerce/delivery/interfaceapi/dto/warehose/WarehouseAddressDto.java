package ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для адреса склада.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseAddressDto {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}