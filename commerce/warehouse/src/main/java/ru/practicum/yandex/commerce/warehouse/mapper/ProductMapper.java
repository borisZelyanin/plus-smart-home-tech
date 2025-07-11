package ru.practicum.yandex.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductDto;
import ru.practicum.yandex.commerce.warehouse.model.Product;

@Component
public class ProductMapper {

    public ProductDto toDto(Product entity) {
        return ProductDto.builder()
                .productId(entity.getProductId())
                .productName(entity.getProductName())
                .description(entity.getDescription())
                .imageSrc(entity.getImageSrc())
                .price(entity.getPrice())
                .productCategory(entity.getProductCategory())
                .productState(entity.getProductState())
                .quantityState(entity.getQuantityState())
                .build();
    }

    public Product toEntity(ProductDto dto) {
        return Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .imageSrc(dto.getImageSrc())
                .price(dto.getPrice())
                .productCategory(dto.getProductCategory())
                .productState(dto.getProductState())
                .quantityState(dto.getQuantityState())
                .build();
    }
}