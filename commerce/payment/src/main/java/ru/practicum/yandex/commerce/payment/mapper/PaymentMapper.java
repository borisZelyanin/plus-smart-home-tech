package ru.practicum.yandex.commerce.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.order.PaymentDto;
import ru.practicum.yandex.commerce.payment.model.Payment;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    PaymentDto toDto(Payment payment);
    Payment toEntity(PaymentDto dto);
}