package ru.practicum.yandex.commerce.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.Feign.CheckProductAvailabilityRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.Feign.CheckProductAvailabilityResponse;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/checkAvailability")
    CheckProductAvailabilityResponse checkAvailability(@RequestBody CheckProductAvailabilityRequest request);
}
