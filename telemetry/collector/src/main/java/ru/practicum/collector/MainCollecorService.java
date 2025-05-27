package ru.practicum.collector;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.practicum.collector.util.GrpcRequestLog;


@SpringBootApplication(scanBasePackages = "ru.practicum")
public class MainCollecorService {

    private final GrpcRequestLog requestLog;

    public MainCollecorService(GrpcRequestLog requestLog) {
        this.requestLog = requestLog;
    }


    public static void main(String[] args) {
        SpringApplication.run(MainCollecorService.class,args);
    }

    @PreDestroy
    public void onShutdown() {
        requestLog.printSummary(); // ✅ печатаем перед завершением
    }
}