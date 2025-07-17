package ru.practicum.yandex.commerce.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_address", schema = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}