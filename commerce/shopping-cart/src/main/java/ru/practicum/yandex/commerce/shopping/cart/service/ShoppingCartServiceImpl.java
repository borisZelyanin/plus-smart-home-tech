package ru.practicum.yandex.commerce.shopping.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.interfaceapi.client.WarehouseClient;
import ru.practicum.yandex.commerce.interfaceapi.dto.warehose.Feign.ChangeProductQuantityRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.shopping.cart.ShoppingCartDto;
import ru.practicum.yandex.commerce.interfaceapi.exception.shopping.cart.NotEnoughStockException;
import ru.practicum.yandex.commerce.interfaceapi.exception.shopping.cart.ShoppingCartNotFoundException;
import ru.practicum.yandex.commerce.shopping.cart.mapper.ShoppingCartMapper;
import ru.practicum.yandex.commerce.shopping.cart.model.ShoppingCart;
import ru.practicum.yandex.commerce.shopping.cart.repository.ShoppingCartRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;
    private final WarehouseClient warehouseClient;

    @Override
    public ShoppingCartDto getCart(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDto)
                .orElseGet(() -> {
                    ShoppingCart cart = ShoppingCart.builder()
                            .username(username)
                            .products(new HashMap<>())
                            .build();
                    return mapper.toDto(repository.save(cart));
                });
    }

    @Override
    public ShoppingCartDto addProducts(String username, Map<String, Long> productIdToQuantity) {
        // Преобразуем ключи в UUID
        Map<UUID, Integer> productCheckRequest = productIdToQuantity.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> UUID.fromString(e.getKey()),
                        e -> e.getValue().intValue()
                ));

        // Проверяем наличие на складе
        Map<UUID, Boolean> availabilityMap = warehouseClient.checkProducts(productCheckRequest);

        // Получаем или создаём корзину
        ShoppingCart cart = repository.findByUsername(username)
                .orElseGet(() -> repository.save(
                        ShoppingCart.builder()
                                .username(username)
                                .products(new HashMap<>())
                                .build()
                ));

        Map<UUID, Long> cartProducts = cart.getProducts();

        // Добавляем только доступные товары
        for (Map.Entry<String, Long> entry : productIdToQuantity.entrySet()) {
            UUID productId = UUID.fromString(entry.getKey());
            Long qty = entry.getValue();

            if (availabilityMap.getOrDefault(productId, false)) {
                cartProducts.put(productId, cartProducts.getOrDefault(productId, 0L) + qty);
            }
        }

        // Сохраняем и возвращаем
        return mapper.toDto(repository.save(cart));
    }

    @Override
    public ShoppingCartDto changeQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = repository.findByUsername(username).orElseThrow();
        UUID productId = request.getProductId();
        long newQty = request.getNewQuantity();
        cart.getProducts().put(productId, newQty);
        return mapper.toDto(repository.save(cart));
    }

    @Override
    public ShoppingCartDto removeProducts(String username, List<String> productIds) {
        ShoppingCart cart = repository.findByUsername(username).orElseThrow();
        List<UUID> toRemove = productIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        toRemove.forEach(cart.getProducts()::remove);
        return mapper.toDto(repository.save(cart));
    }

    @Override
    public void deactivateCart(String username) {
        repository.deleteByUsername(username);
    }
}