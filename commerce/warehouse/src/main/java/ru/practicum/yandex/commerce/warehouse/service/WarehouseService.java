package ru.practicum.yandex.commerce.warehouse.service;

import ru.practicum.yandex.commerce.warehouse.model.Product;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseAddress;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseService {
    Product saveProduct(Product product);
    Optional<Product> getProduct(UUID productId);
    List<Product> getAllProducts();

    WarehouseStock saveStock(WarehouseStock stock);
    Optional<WarehouseStock> getStock(UUID productId);

    WarehouseAddress saveAddress(WarehouseAddress address);
    List<WarehouseAddress> getAllAddresses();
}