package ru.practicum.yandex.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.AddressDto;
import ru.practicum.yandex.commerce.delivery.interfaceapi.dto.delivery.DeliveryDto;
import ru.practicum.yandex.commerce.delivery.model.*;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {
    DeliveryDto toDto(Delivery entity);
    Delivery toEntity(DeliveryDto dto);
    AddressDto toDto(Address address);
    Address toEntity(AddressDto dto);
}