package ru.practicum.yandex.commerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.yandex.commerce.order.model.Order;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findAllByShoppingCartId(UUID cartId);
    List<Order> findAllByUsername(String username); // если у тебя есть поле username
}