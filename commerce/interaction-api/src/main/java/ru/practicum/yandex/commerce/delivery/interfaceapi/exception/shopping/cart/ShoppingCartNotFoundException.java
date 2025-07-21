package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.shopping.cart;

public class ShoppingCartNotFoundException extends RuntimeException {

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
