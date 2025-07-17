package ru.practicum.yandex.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseProduct;

import java.util.UUID;

public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, UUID> {
}