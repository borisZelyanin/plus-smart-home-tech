package ru.practicum.yandex.shopping.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductCategory;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.QuantityState;
import ru.practicum.yandex.commerce.interfaceapi.dto.SetProductQuantityStateRequest;
import ru.practicum.yandex.shopping.store.service.ShoppingStoreService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
@Tag(name = "Витрина онлайн-магазина", description = "API для обеспечения работы витрины онлайн магазина")
@Slf4j
public class ShoppingStoreController {

    private final ShoppingStoreService shoppingStoreService;

    @Operation(summary = "Получение списка товаров по типу в пагинированном виде", operationId = "getProducts")
    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam("category") ProductCategory category,
                                        @ParameterObject Pageable pageable) {
        log.info("Получен запрос на получение товаров категории '{}' с пагинацией: page={}, size={}",
                category, pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductDto> result = shoppingStoreService.getProductsPageByCategory(category, pageable);

        log.info("Найдено {} товаров категории '{}'", result.getTotalElements(), category);
        return result;
    }

    @Operation(summary = "Создание нового товара в ассортименте", operationId = "createNewProduct")
    @PutMapping
    public ProductDto createNewProduct(@RequestBody ProductDto productDto) {
        log.info("Создание нового товара: {}", productDto);
        ProductDto result = shoppingStoreService.createNewProduct(productDto);
        log.info("Создан товар: {}", result);
        return result;
    }

    @Operation(summary = "Обновление товара в ассортименте", operationId = "updateProduct")
    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        log.info("Обновление товара: {}", productDto);
        ProductDto result = shoppingStoreService.updateProduct(productDto);
        log.info("Обновлён товар: {}", result);
        return result;
    }

    @Operation(summary = "Удалить товар из ассортимента магазина", operationId = "removeProductFromStore")
    @PostMapping("/removeProductFromStore")
    public boolean removeProductFromStore(@RequestBody UUID productId) {
        log.info("Удаление товара с ID: {}", productId);
        boolean result = shoppingStoreService.removeProductFromStore(productId);
        log.info("Результат удаления товара: {}", result);
        return result;
    }

    @Operation(summary = "Установка статуса по товару", operationId = "setProductQuantityState")
    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestParam UUID productId,
                                           @RequestParam QuantityState quantityState) {
        log.info("Установка статуса товара: {} -> {}", productId, quantityState);
        boolean result = shoppingStoreService.setProductQuantityState(productId, quantityState);
        log.info("Результат установки статуса: {}", result);
        return result;
    }

    @Operation(summary = "Получить сведения по товару", operationId = "getProduct")
    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable UUID productId) {
        log.info("Получение товара с ID: {}", productId);
        ProductDto result = shoppingStoreService.getProductById(productId)
                .orElseThrow(() -> {
                    log.warn("Товар с ID {} не найден", productId);
                    return new RuntimeException("Product not found");
                });
        log.info("Найден товар: {}", result);
        return result;
    }
}