package ru.practicum.yandex.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.PaymentDto;
import ru.practicum.yandex.commerce.payment.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/productCost")
    public Double calculateProductCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @PostMapping("/totalCost")
    public Double calculateTotalCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto orderDto) {
        return paymentService.createPayment(orderDto);
    }

    @PostMapping("/refund")
    public void markSuccess(@RequestBody UUID paymentId) {
        paymentService.markSuccess(paymentId);
    }

    @PostMapping("/failed")
    public void markFailed(@RequestBody UUID paymentId) {
        paymentService.markFailed(paymentId);
    }
}