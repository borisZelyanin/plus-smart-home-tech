package ru.practicum.yandex.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.NewProductInWarehouseRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseAddressDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseStockDto;
import ru.practicum.yandex.commerce.warehouse.service.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @GetMapping("/address")
    public WarehouseAddressDto getAllAddresses() {
        return service.getAddress();
    }

    @PutMapping
    public ResponseEntity<Void> addNewProduct(@RequestBody NewProductInWarehouseRequest request) {
        service.addNewProduct(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add")
    public ResponseEntity<WarehouseStockDto> addStock(@RequestBody WarehouseStockDto request) {
        return ResponseEntity.ok(service.addStock(request));
    }

}