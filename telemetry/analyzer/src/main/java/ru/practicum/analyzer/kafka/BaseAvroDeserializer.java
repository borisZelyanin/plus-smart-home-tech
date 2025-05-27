package ru.practicum.analyzer.kafka;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class BaseAvroDeserializer<T extends SpecificRecordBase> implements Deserializer<T> {

    private final Schema schema;

    public BaseAvroDeserializer(Schema schema) {
        this.schema = schema;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            var reader = new SpecificDatumReader<T>(schema);
            var decoder = DecoderFactory.get().binaryDecoder(data, null);
            return reader.read(null, decoder);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации Avro", e);
        }
    }

    @Override
    public void close() {}
}