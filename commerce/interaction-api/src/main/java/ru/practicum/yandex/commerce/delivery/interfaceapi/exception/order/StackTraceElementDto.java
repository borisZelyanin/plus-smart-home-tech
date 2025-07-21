package ru.practicum.yandex.commerce.delivery.interfaceapi.exception.order;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StackTraceElementDto {
    private String classLoaderName;
    private String moduleName;
    private String moduleVersion;
    private String className;
    private String methodName;
    private String fileName;
    private Integer lineNumber;
    private Boolean nativeMethod;
}