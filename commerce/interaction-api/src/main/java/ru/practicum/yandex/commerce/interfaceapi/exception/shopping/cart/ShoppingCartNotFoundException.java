package ru.practicum.yandex.commerce.interfaceapi.exception.shopping.cart;

public class ShoppingCartNotFoundException extends RuntimeException {

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
