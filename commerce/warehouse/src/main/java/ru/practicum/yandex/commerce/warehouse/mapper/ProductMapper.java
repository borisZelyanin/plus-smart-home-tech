package ru.practicum.yandex.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.interfaceapi.dto.WarehouseProductDto;
import ru.practicum.yandex.commerce.warehouse.model.Product;

@Component
public class ProductMapper {

    public WarehouseProductDto toDto(Product entity) {
        return WarehouseProductDto.builder()
                .productId(entity.getProductId())
                .width(entity.getWidth())
                .height(entity.getHeight())
                .depth(entity.getDepth())
                .weight(entity.getWeight())
                .fragile(entity.isFragile())
                .build();
    }

    public Product toEntity(WarehouseProductDto dto) {
        return Product.builder()
                .productId(dto.getProductId())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .depth(dto.getDepth())
                .weight(dto.getWeight())
                .fragile(dto.isFragile())
                .build();
    }
}