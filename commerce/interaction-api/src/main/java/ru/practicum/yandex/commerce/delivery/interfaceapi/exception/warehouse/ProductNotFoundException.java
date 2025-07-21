package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.warehouse;


public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}