package ru.practicum.yandex.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseAddress;

public interface WarehouseAddressRepository extends JpaRepository<WarehouseAddress, Integer> {
}