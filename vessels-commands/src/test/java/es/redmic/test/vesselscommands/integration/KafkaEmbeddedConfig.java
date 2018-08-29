package es.redmic.test.vesselscommands.integration;

public class KafkaEmbeddedConfig {

	// number of brokers.
	public final static Integer NUM_BROKERS = 3;
	// partitions per topic.
	public final static Integer PARTITIONS_PER_TOPIC = 3;

	public final static String[] TOPICS_NAME = new String[] { "vessels-agg-by-vessel-type", "vessel-type", "vessel",
			"realtime.tracking.vessels" };
}
