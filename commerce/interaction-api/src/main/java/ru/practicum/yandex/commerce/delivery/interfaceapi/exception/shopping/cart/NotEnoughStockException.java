package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.shopping.cart;

public class NotEnoughStockException extends RuntimeException {

    public NotEnoughStockException(String message) {
        super(message);
    }
}
