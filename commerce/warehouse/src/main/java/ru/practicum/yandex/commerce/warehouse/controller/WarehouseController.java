package ru.practicum.yandex.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.warehouse.model.Product;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseAddress;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;
import ru.practicum.yandex.commerce.warehouse.service.WarehouseService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    // ==== Products ====

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(service.saveProduct(product));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable UUID id) {
        Optional<Product> product = service.getProduct(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    // ==== Warehouse Stock ====

    @PostMapping("/stock")
    public ResponseEntity<WarehouseStock> createOrUpdateStock(@RequestBody WarehouseStock stock) {
        return ResponseEntity.ok(service.saveStock(stock));
    }

    @GetMapping("/stock/{productId}")
    public ResponseEntity<WarehouseStock> getStock(@PathVariable UUID productId) {
        Optional<WarehouseStock> stock = service.getStock(productId);
        return stock.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ==== Warehouse Address ====

    @PostMapping("/address")
    public ResponseEntity<WarehouseAddress> createAddress(@RequestBody WarehouseAddress address) {
        return ResponseEntity.ok(service.saveAddress(address));
    }

    @GetMapping("/address")
    public ResponseEntity<List<WarehouseAddress>> getAllAddresses() {
        return ResponseEntity.ok(service.getAllAddresses());
    }
}