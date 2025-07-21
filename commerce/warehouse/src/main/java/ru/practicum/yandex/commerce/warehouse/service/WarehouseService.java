package ru.practicum.yandex.commerce.warehouse.service;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.NewProductInWarehouseRequest;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.WarehouseAddressDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.warehose.WarehouseStockDto;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseService {

    WarehouseAddressDto getAddress();

    void addNewProduct(NewProductInWarehouseRequest request);

    boolean isProductInStock(UUID productId);

    Optional<WarehouseStockDto> getStockByProductId(UUID productId);

    WarehouseStockDto addStock(WarehouseStockDto request);

    Map<UUID, Boolean> checkProductsAvailability(Map<UUID, Integer> productList);

    /**
     * Собрать товары для заказа (проверка остатков, резервирование и уменьшение количества).
     */
    void assembleOrder(UUID orderId);

    /**
     * Передать заказ в доставку (связать с deliveryId и обновить статус).
     */
    void sendToDelivery(UUID orderId);

    /**
     * Вернуть товары на склад (увеличить остаток по переданным productId и quantity).
     */
    void returnOrder(UUID orderId);
}