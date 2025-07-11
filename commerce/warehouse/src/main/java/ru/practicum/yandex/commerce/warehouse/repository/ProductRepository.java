package ru.practicum.yandex.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.yandex.commerce.warehouse.model.Product;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}