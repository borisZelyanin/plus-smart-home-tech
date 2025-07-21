package ru.practicum.yandex.shopping.store.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.store.ProductCategory;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.store.ProductState;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.shopping.store.QuantityState;
import ru.practicum.yandex.shopping.store.model.Product;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Получить страницу товаров по категории
    Page<Product> findByProductCategoryOrderByProductName(ProductCategory category, Pageable pageable);

    // Обновить состояние товара (например, DEACTIVATE)
    @Modifying
    @Query("UPDATE Product p SET p.productState = :state WHERE p.productId = :productId")
    int updateProductState(@Param("productId") UUID productId, @Param("state") ProductState state);

    // Обновить количество товара
    @Modifying
    @Query("UPDATE Product p SET p.quantityState = :quantityState WHERE p.productId = :productId")
    int updateQuantityState(@Param("productId") UUID productId, @Param("quantityState") QuantityState quantityState);
}