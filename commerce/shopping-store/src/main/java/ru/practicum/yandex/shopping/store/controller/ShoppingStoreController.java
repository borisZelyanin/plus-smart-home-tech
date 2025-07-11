package ru.practicum.yandex.shopping.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductCategory;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.SetProductQuantityStateRequest;
import ru.practicum.yandex.shopping.store.service.ShoppingStoreService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Tag(name = "Витрина онлайн-магазина", description = "API для обеспечения работы витрины онлайн магазина")
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @Operation(
            summary = "Получение списка товаров по типу в пагинированном виде",
            operationId = "getProducts"
    )
    @GetMapping
    public List<ProductDto> getProducts(
            @RequestParam("category") ProductCategory category,
            @ParameterObject Pageable pageable
    ) {
        return shoppingStoreService.getProductsByCategory(category.name(), pageable.getPageNumber(), pageable.getPageSize());
    }

    @Operation(
            summary = "Создание нового товара в ассортименте",
            operationId = "createNewProduct"
    )
    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.createNewProduct(productDto);
    }

    @Operation(
            summary = "Обновление товара в ассортименте",
            operationId = "updateProduct"
    )
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return shoppingStoreService.updateProduct(productDto);
    }

    @Operation(
            summary = "Удалить товар из ассортимента магазина",
            operationId = "removeProductFromStore"
    )
    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        return shoppingStoreService.removeProductFromStore(productId);
    }

    @Operation(
            summary = "Установка статуса по товару",
            operationId = "setProductQuantityState"
    )
    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request) {
        return shoppingStoreService.setProductQuantityState(request);
    }

    @Operation(
            summary = "Получить сведения по товару",
            operationId = "getProduct"
    )
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        return shoppingStoreService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}