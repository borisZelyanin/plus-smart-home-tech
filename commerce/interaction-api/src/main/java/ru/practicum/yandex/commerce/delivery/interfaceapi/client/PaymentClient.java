package ru.practicum.yandex.commerce.delivery.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.payment.TotalCostRequest;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.payment.TotalCostResponse;

@FeignClient(name = "payment", url = "http://payment")
public interface PaymentClient {

    @PostMapping("/api/v1/payment/productCost")
    Double getProductCost(@RequestBody TotalCostRequest request);

    @PostMapping("/api/v1/payment/totalCost")
    TotalCostResponse getTotalCost(@RequestBody TotalCostRequest request);

    @PostMapping("/api/v1/payment")
    void initiatePayment(@RequestBody TotalCostRequest request);
}
