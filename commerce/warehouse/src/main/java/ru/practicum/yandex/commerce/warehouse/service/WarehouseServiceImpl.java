package ru.practicum.yandex.commerce.warehouse.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.NewProductInWarehouseRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseAddressDto;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.WarehouseStockDto;
import ru.practicum.yandex.commerce.interfaceapi.exception.warehouse.ProductNotFoundException;
import ru.practicum.yandex.commerce.interfaceapi.exception.warehouse.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.yandex.commerce.warehouse.mapper.NewProductInWarehouseMapper;
import ru.practicum.yandex.commerce.warehouse.mapper.WarehouseStockMapper;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseProduct;
import ru.practicum.yandex.commerce.warehouse.model.WarehouseStock;
import ru.practicum.yandex.commerce.warehouse.repository.WarehouseProductRepository;
import ru.practicum.yandex.commerce.warehouse.repository.WarehouseAddressRepository;
import ru.practicum.yandex.commerce.warehouse.repository.WarehouseStockRepository;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseProductRepository productRepository;
    private final WarehouseAddressRepository addressRepository;
    private final NewProductInWarehouseMapper mapper;


    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS = ADDRESSES[new SecureRandom().nextInt(2)];
    private final WarehouseStockRepository warehouseStockRepository;
    private final WarehouseStockMapper warehouseStockMapper;

    @Override
    public boolean isProductInStock(UUID productId) {
        return warehouseStockRepository.existsByProduct_ProductId(productId);
    }

    @Override
    public Optional<WarehouseStockDto> getStockByProductId(UUID productId) {
        return warehouseStockRepository.findByProduct_ProductId(productId)
                .map(warehouseStockMapper::toDto);
    }

    @Override
    @Transactional
    public boolean addStock(WarehouseStockDto request) {
        UUID productId = request.getProductId();

        // Проверка: есть ли уже остатки
        if (warehouseStockRepository.existsByProduct_ProductId(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException("Остатки уже существуют для товара: " + productId);
        }

        // Проверка: есть ли такой продукт
        WarehouseProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Продукт не найден: " + productId));

        // Создание новой записи об остатках
        WarehouseStock stock = new WarehouseStock();
        stock.setProduct(product);
        stock.setQuantity(request.getQuantity());

        warehouseStockRepository.save(stock);
        return true;
    }

    @Override
    public void addNewProduct(NewProductInWarehouseRequest request) {
        UUID productId = request.getProductId();

        if (productRepository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар уже зарегистрирован на складе");
        }

        WarehouseProduct product = mapper.toProduct(request);


        productRepository.save(product);
    }

    @Override
    public WarehouseAddressDto getAddress() {
        return new WarehouseAddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS
        );
    }
}