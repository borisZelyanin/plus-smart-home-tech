package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.order;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThrowableDto {
    private String message;
    private String localizedMessage;
    private List<StackTraceElementDto> stackTrace;
}