package ru.practicum.yandex.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseStockDto;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseProduct;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;

@Component
public class WarehouseStockMapper {

    public WarehouseStockDto toDto(WarehouseStock entity) {
        return new WarehouseStockDto(false, null, entity.getProductId(), entity.getQuantity());
    }

    public WarehouseStock toEntity(WarehouseStockDto dto, WarehouseProduct product) {
        return WarehouseStock.builder()
                .productId(dto.getProductId())
                .product(product)
                .quantity(dto.getQuantity())
                .build();
    }
}