package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.order;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotEnoughInfoInOrderToCalculateExceptionDto {
    private String message;
    private String userMessage;
    private String httpStatus;
    private List<StackTraceElementDto> stackTrace;
    private ThrowableDto cause;
    private List<ThrowableDto> suppressed;
    private String localizedMessage;
}