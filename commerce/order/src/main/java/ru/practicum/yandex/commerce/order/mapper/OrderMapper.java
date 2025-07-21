package ru.practicum.yandex.commerce.order.mapper;



import org.springframework.stereotype.Component;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.OrderDto;
import ru.practicum.yandex.commerce.order.model.Order;

@Component
public class OrderMapper {

    public OrderDto toDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .shoppingCartId(order.getShoppingCartId())
                .products(order.getProducts())
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .state(order.getState())
                .deliveryWeight(order.getDeliveryWeight())
                .deliveryVolume(order.getDeliveryVolume())
                .fragile(order.getFragile())
                .totalPrice(order.getTotalPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .productPrice(order.getProductPrice())
                .build();
    }

    public Order toEntity(OrderDto dto) {
        return Order.builder()
                .orderId(dto.getOrderId())
                .shoppingCartId(dto.getShoppingCartId())
                .products(dto.getProducts())
                .paymentId(dto.getPaymentId())
                .deliveryId(dto.getDeliveryId())
                .state(dto.getState())
                .deliveryWeight(dto.getDeliveryWeight())
                .deliveryVolume(dto.getDeliveryVolume())
                .fragile(dto.getFragile())
                .totalPrice(dto.getTotalPrice())
                .deliveryPrice(dto.getDeliveryPrice())
                .productPrice(dto.getProductPrice())
                .build();
    }
}