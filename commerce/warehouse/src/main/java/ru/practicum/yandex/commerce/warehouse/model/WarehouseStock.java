package ru.practicum.yandex.commerce.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "warehouse_stock", schema = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseStock {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private long quantity;
}