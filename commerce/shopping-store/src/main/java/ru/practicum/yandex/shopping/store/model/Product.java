package ru.practicum.yandex.shopping.store.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductCategory;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductState;
import ru.practicum.yandex.commerce.interfaceapi.dto.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image_src")
    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
