package ru.practicum.yandex.commerce.interfaceapi.dto.warehose;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DimensionDto {
    private double depth;
    private double height;
    private double width;
}