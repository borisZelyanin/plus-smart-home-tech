package ru.practicum.yandex.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, UUID> {

    // Проверка — есть ли товар по productId
    boolean existsByProduct_ProductId(UUID productId);

    // Получение записи склада по productId
    Optional<WarehouseStock> findByProduct_ProductId(UUID productId);
}