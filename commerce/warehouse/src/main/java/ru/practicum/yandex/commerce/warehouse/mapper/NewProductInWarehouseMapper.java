package ru.practicum.yandex.commerce.warehouse.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.NewProductInWarehouseRequest;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseProduct;

@Component
public class NewProductInWarehouseMapper {

    public WarehouseProduct toProduct(NewProductInWarehouseRequest request) {
        return WarehouseProduct.builder()
                .productId(request.getProductId())
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .weight(request.getWeight())
                .fragile(request.isFragile())
                .build();
    }
}