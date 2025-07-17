package ru.practicum.yandex.commerce.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products", schema = "warehouse")
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    private double width;
    private double height;
    private double depth;
    private double weight;
    private boolean fragile;
}