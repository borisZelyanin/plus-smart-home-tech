package ru.practicum.yandex.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.interfaceapi.dto.WarehouseStockDto;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;

@Component
public class WarehouseStockMapper {
    public WarehouseStockDto toDto(WarehouseStock entity) {
        WarehouseStockDto dto = new WarehouseStockDto();
        dto.setProductId(entity.getProduct().getProductId());
        dto.setQuantity(entity.getQuantity());
        return dto;
    }
}