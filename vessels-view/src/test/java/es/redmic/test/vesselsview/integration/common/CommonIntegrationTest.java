package es.redmic.test.vesselsview.integration.common;

import org.junit.ClassRule;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;

import es.redmic.testutils.documentation.DocumentationViewBaseTest;

@DirtiesContext
public abstract class CommonIntegrationTest extends DocumentationViewBaseTest {

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1);
}
