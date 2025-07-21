package ru.practicum.yandex.commerce.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.yandex.commerce.payment.model.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}