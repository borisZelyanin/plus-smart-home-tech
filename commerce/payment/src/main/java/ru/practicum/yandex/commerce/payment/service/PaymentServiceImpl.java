package ru.practicum.yandex.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.PaymentDto;
import ru.practicum.yandex.commerce.payment.mapper.PaymentMapper;
import ru.practicum.yandex.commerce.payment.model.Payment;
import ru.practicum.yandex.commerce.payment.model.PaymentStatus;
import ru.practicum.yandex.commerce.payment.repository.PaymentRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public Double calculateProductCost(OrderDto orderDto) {
        // TODO: Запросить стоимость каждого товара из shopping-store
        return orderDto.getProducts().values().stream()
                .mapToInt(Integer::intValue)
                .sum() * 100.0; // заглушка: цена товара = 100
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        Double productCost = calculateProductCost(orderDto);
        Double fee = productCost * 0.1;
        Double delivery = orderDto.getDeliveryPrice() != null ? orderDto.getDeliveryPrice() : 50.0;
        return productCost + fee + delivery;
    }

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        Double productCost = calculateProductCost(orderDto);
        Double fee = productCost * 0.1;
        Double delivery = orderDto.getDeliveryPrice() != null ? orderDto.getDeliveryPrice() : 50.0;
        Double total = productCost + fee + delivery;

        Payment payment = Payment.builder()
                .totalPayment(total)
                .deliveryTotal(delivery)
                .feeTotal(fee)
                .status(PaymentStatus.PENDING)
                .build();

        return PaymentMapper.INSTANCE.toDto(paymentRepository.save(payment));
    }

    @Override
    public void markSuccess(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    public void markFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
}