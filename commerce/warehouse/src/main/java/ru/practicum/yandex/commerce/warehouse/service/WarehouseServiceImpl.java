package ru.practicum.yandex.commerce.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.warehouse.model.Product;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseAddress;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;
import ru.practicum.yandex.commerce.warehouse.repository.ProductRepository;
import ru.practicum.yandex.commerce.warehouse.repository.WarehouseAddressRepository;
import ru.practicum.yandex.commerce.warehouse.repository.WarehouseStockRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final ProductRepository productRepository;
    private final WarehouseStockRepository stockRepository;
    private final WarehouseAddressRepository addressRepository;

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> getProduct(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public WarehouseStock saveStock(WarehouseStock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Optional<WarehouseStock> getStock(UUID productId) {
        return stockRepository.findById(productId);
    }

    @Override
    public WarehouseAddress saveAddress(WarehouseAddress address) {
        return addressRepository.save(address);
    }

    @Override
    public List<WarehouseAddress> getAllAddresses() {
        return addressRepository.findAll();
    }
}