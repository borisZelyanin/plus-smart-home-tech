package ru.practicum.yandex.shopping.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.interfaceapi.dto.ProductDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.SetProductQuantityStateRequest;
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

    @Override
    public List<ProductDto> getProductsByCategory(String category, int page, int size) {
        return productRepository.findByProductCategory(category, page, size).stream()
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
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product updated = productRepository.update(productMapper.toEntity(productDto));
        return productMapper.toDto(updated);
    }

    @Override
    public boolean removeProductFromStore(UUID productId) {
        return productRepository.remove(productId);
    }

    @Override
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return productRepository.updateQuantityState(request.getProductId(), request.getQuantityState().name());
    }
}