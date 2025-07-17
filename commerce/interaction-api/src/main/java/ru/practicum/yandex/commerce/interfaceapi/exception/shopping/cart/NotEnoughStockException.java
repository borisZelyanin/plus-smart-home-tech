package ru.practicum.yandex.commerce.interfaceapi.exception.shopping.cart;

public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException(String message) {
        super(message);
    }
}
