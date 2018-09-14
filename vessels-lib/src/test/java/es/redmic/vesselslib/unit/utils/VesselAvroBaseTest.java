package es.redmic.vesselslib.unit.utils;

import java.util.HashMap;
import java.util.Properties;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

public abstract class VesselAvroBaseTest {

	private final SchemaRegistryClient schemaRegistry;

	protected KafkaAvroSerializer avroSerializer;

	protected KafkaAvroDeserializer avroDeserializer;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public VesselAvroBaseTest() {

		Properties defaultConfig = new Properties();
		defaultConfig.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "bogus");
		defaultConfig.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

		schemaRegistry = new MockSchemaRegistryClient();

		avroSerializer = new KafkaAvroSerializer(schemaRegistry, new HashMap(defaultConfig));

		avroDeserializer = new KafkaAvroDeserializer(schemaRegistry, new HashMap(defaultConfig));
	}

	@SuppressWarnings("unchecked")
	protected <T> T serializerAndDeserializer(T dto) {

		byte[] dtoBytes = avroSerializer.serialize("msg.t", dto);

		T result = (T) avroDeserializer.deserialize("msg.t", dtoBytes);

		avroSerializer.close();
		avroDeserializer.close();

		return result;
	}
}
