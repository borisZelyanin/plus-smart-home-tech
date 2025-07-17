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
import java.util.HashMap;
import java.util.Map;
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
    public WarehouseStockDto addStock(WarehouseStockDto request) {
        UUID productId = request.getProductId();

        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Товар с ID " + productId + " не найден в таблице products");
        }

        WarehouseStock stock = warehouseStockRepository.findByProduct_ProductId(productId).orElse(null);

        if (stock == null) {
            // Проверка: есть ли такой продукт
            WarehouseProduct product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Продукт не найден: " + productId));
            WarehouseStock newStock = new WarehouseStock();
            newStock.setProduct(product);
            newStock.setQuantity(request.getQuantity());
            warehouseStockRepository.save(newStock);
            return WarehouseStockDto.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .isNew(false)
                    .message("Количество товара на складе")
                    .build();
        } else {
            long newQuantity = stock.getQuantity() + request.getQuantity();
            stock.setQuantity(newQuantity);
            warehouseStockRepository.save(stock);
            return WarehouseStockDto.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .isNew(false)
                    .message("Количество товара на складе")
                    .build();
        }
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
    public Map<UUID, Boolean> checkProductsAvailability(Map<UUID, Integer> productList) {
        Map<UUID, Boolean> result = new HashMap<>();

        for (Map.Entry<UUID, Integer> entry : productList.entrySet()) {
            UUID productId = entry.getKey();
            int requiredQuantity = entry.getValue();

            Optional<WarehouseStock> stockOpt = warehouseStockRepository.findByProduct_ProductId(productId);
            boolean isAvailable = stockOpt.map(stock -> stock.getQuantity() >= requiredQuantity).orElse(false);

            result.put(productId, isAvailable);
        }

        return result;
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