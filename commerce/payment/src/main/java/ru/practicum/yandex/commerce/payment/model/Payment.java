package ru.practicum.yandex.commerce.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "payments", schema = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue
    private UUID paymentId;

    private Double totalPayment;
    private Double deliveryTotal;
    private Double feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}