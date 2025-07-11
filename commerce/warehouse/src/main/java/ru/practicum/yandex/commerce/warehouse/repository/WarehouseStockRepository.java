package ru.practicum.yandex.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;

import java.util.UUID;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, UUID> {
}