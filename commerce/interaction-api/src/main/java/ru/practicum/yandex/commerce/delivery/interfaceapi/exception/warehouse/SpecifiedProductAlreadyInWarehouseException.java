package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.warehouse;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    public SpecifiedProductAlreadyInWarehouseException(String message) {
        super(message);
    }
}