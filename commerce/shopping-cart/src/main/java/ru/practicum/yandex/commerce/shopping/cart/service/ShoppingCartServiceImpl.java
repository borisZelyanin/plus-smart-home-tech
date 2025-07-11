package ru.practicum.yandex.commerce.shopping.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.yandex.commerce.interfaceapi.dto.ChangeProductQuantityRequest;
import ru.practicum.yandex.commerce.interfaceapi.dto.ShoppingCartDto;
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
        ShoppingCart cart = repository.findByUsername(username)
                .orElseGet(() -> repository.save(ShoppingCart.builder()
                        .username(username)
                        .products(new HashMap<>())
                        .build()));

        Map<UUID, Long> products = cart.getProducts();
        productIdToQuantity.forEach((id, qty) -> {
            UUID uuid = UUID.fromString(id);
            products.put(uuid, products.getOrDefault(uuid, 0L) + qty);
        });

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