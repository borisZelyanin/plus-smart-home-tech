package ru.practicum.yandex.commerce.delivery.model;


import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;
}
