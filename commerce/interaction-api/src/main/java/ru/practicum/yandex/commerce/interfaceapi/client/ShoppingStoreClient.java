package ru.practicum.yandex.commerce.interfaceapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    List<ProductDto> getProducts(@RequestParam String category,
                                 @RequestParam int page,
                                 @RequestParam int size);

    @GetMapping("/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @PutMapping
    ProductDto createNewProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setQuantityState(@RequestBody SetProductQuantityStateRequest request);
}