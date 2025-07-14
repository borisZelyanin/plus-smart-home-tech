package ru.practicum.yandex.commerce.warehouse.service;

import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.NewProductInWarehouseRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseAddressDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseStockDto;

import java.util.Optional;
import java.util.UUID;

public interface WarehouseService {

    WarehouseAddressDto getAddress();

    void addNewProduct(NewProductInWarehouseRequest request);

    boolean isProductInStock(UUID productId);

    Optional<WarehouseStockDto> getStockByProductId(UUID productId);

    WarehouseStockDto addStock(WarehouseStockDto request);

}