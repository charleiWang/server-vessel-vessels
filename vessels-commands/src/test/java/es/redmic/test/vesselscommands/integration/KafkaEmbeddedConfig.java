package es.redmic.test.vesselscommands.integration;

public class KafkaEmbeddedConfig {

	// number of brokers.
	public final static Integer NUM_BROKERS = 3;
	// partitions per topic.
	public final static Integer PARTITIONS_PER_TOPIC = 3;

	// @formatter:off
 
	public final static String[] TOPICS_NAME = new String[] { 
		"vessels-agg-by-vessel-type",
		"vessel-type",
		"vessel",
		"realtime.tracking.vessels",
		"realtime.vessels",
		"vessel-type-updated",
		"tracking-agg-by-vessel",
		"vessel-updated",
		"vessel-tracking",
		"vessel-type-snapshot",
		"vessel-snapshot",
		"vessel-tracking-snapshot"
	};
	
	// @formatter:on
}
