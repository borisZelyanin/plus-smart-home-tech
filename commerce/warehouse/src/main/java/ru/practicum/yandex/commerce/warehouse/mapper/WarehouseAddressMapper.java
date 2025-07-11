package ru.practicum.yandex.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.interfaceapi.dto.WarehouseAddressDto;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseAddress;

@Component
public class WarehouseAddressMapper {
    public WarehouseAddressDto toDto(WarehouseAddress entity) {
        WarehouseAddressDto dto = new WarehouseAddressDto();
        dto.setCountry(entity.getCountry());
        dto.setCity(entity.getCity());
        dto.setStreet(entity.getStreet());
        dto.setHouse(entity.getHouse());
        dto.setFlat(entity.getFlat());
        return dto;
    }
}