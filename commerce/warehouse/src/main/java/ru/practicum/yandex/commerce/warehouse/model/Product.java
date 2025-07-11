package ru.practicum.yandex.commerce.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "products", schema = "warehouse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @Column(name = "product_id")
    private UUID id;

    @Column(nullable = false)
    private double width;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private double depth;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private boolean fragile;
}