package ru.practicum.yandex.commerce.delivery.interfaceapi.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "order", url = "http://order")
public interface PaymentOrderClient {

    @PostMapping("/api/v1/order/payment/success")
    void markOrderPaid(@RequestBody UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    void markOrderPaymentFailed(@RequestBody UUID orderId);
}