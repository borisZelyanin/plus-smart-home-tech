package ru.practicum.collector.kafka;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class GeneralKafkaDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private Class<T> targetType;

    public GeneralKafkaDeserializer(Class<T> targetType) {
        this.targetType = targetType;
    }

    // Для Kafka (через newInstance)
    public GeneralKafkaDeserializer() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> configs, boolean isKey) {
        Object type = configs.get("avro.deserializer.target.class");
        if (type instanceof Class<?>) {
            this.targetType = (Class<T>) type;
        } else if (type instanceof String) {
            try {
                this.targetType = (Class<T>) Class.forName((String) type);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Не удалось загрузить класс: " + type, e);
            }
        }
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null || data.length == 0 || targetType == null) return null;
        try {
            DatumReader<T> reader = new SpecificDatumReader<>(targetType);
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка десериализации Avro", e);
        }
    }

    @Override
    public void close() {
    }
}
