package ru.practicum.yandex.shopping.store.service;

import ru.practicum.yandex.commerce.interfaceapi.dto.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.SetProductQuantityStateRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingStoreService {

    List<ProductDto> getProductsByCategory(String category, int page, int size);

    Optional<ProductDto> getProductById(UUID productId);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProductFromStore(UUID productId);

    boolean setProductQuantityState(SetProductQuantityStateRequest request);
}
