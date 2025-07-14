package ru.practicum.yandex.shopping.store.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.ProductCategory;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.ProductState;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.store.QuantityState;
import ru.practicum.yandex.shopping.store.mapper.ProductMapper;
import ru.practicum.yandex.shopping.store.model.Product;
import ru.practicum.yandex.shopping.store.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductDto> getProductsPageByCategory(ProductCategory category, Pageable pageable) {
        Page<Product> page = productRepository.findByProductCategoryOrderByProductName(category, pageable);
        return page.map(productMapper::toDto);
    }

    @Override
    public List<ProductDto> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return productRepository.findByProductCategoryOrderByProductName(ProductCategory.valueOf(category), pageable)
                .stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductDto> getProductById(UUID productId) {
        return productRepository.findById(productId)
                .map(productMapper::toDto);
    }

    @Override
    public ProductDto createNewProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        product.setProductId(UUID.randomUUID());
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product updated = productMapper.toEntity(productDto);
        return productMapper.toDto(productRepository.save(updated)); // save() работает и для обновления
    }

    @Override
    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        return productRepository.updateProductState(productId, ProductState.DEACTIVATE) > 0;
    }

    @Override
    @Transactional
    public boolean setProductQuantityState(UUID productId, QuantityState quantityState) {
        return productRepository.updateQuantityState(productId, quantityState) > 0;
    }
}