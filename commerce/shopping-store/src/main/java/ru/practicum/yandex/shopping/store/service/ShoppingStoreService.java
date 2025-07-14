package ru.practicum.yandex.shopping.store.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.ProductCategory;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.QuantityState;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingStoreService {

    List<ProductDto> getProductsByCategory(String category, int page, int size);

    Optional<ProductDto> getProductById(UUID productId);

    ProductDto createNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    boolean removeProductFromStore(UUID productId);

    boolean setProductQuantityState(UUID productId, QuantityState quantityState);

    Page<ProductDto> getProductsPageByCategory(ProductCategory category, Pageable pageable);
}
