package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.commands.VesselCommandHandler;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=GetVesselStateFromStoreTest",
		"schema.registry.port=18083" })
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
public class GetVesselStateFromStoreTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	// number of brokers.
	private final static Integer numBrokers = 3;
	// partitions per topic.
	private final static Integer partitionsPerTopic = 3;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(numBrokers, true, partitionsPerTopic);

	// @formatter:off
	
	private static final Integer mmsi = 2222,
			mmsiOther = 5678;

	private static final String aggregateId = VesselDataUtil.PREFIX + mmsi,
			aggregateIdOther = VesselDataUtil.PREFIX + mmsiOther;

	// @formatter:on

	@Value("${broker.topic.vessel}")
	private String TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Autowired
	VesselCommandHandler vesselCommandHandler;

	@PostConstruct
	public void GetVesselStateFromStoreTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Test
	public void createVessel_getVesselState_IfConfigIsCorrect() throws Exception {

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(TOPIC, aggregateId,
				VesselDataUtil.getCreateEvent(mmsi));
		future.addCallback(new SendListener());

		UpdateVesselEvent updateEvent = VesselDataUtil.getUpdateEvent(mmsi);

		updateEvent.getVessel().setName("updated");

		ListenableFuture<SendResult<String, Event>> future2 = kafkaTemplate.send(TOPIC, aggregateId, updateEvent);
		future2.addCallback(new SendListener());

		ListenableFuture<SendResult<String, Event>> future3 = kafkaTemplate.send(TOPIC, aggregateIdOther,
				VesselDataUtil.getCreateEvent(mmsiOther));
		future3.addCallback(new SendListener());

		VesselDTO vessel = null;

		while (!(future.isDone() && future2.isDone() && future3.isDone())) {
			logger.info("Waitting for msg send confirmation");
		}

		Thread.sleep(4000);
		for (int i = 0; i < 10; i++) {
			try {
				logger.info("try " + i);
				vessel = vesselCommandHandler.getVessel(VesselDataUtil.getVessel(mmsi));
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e1) {
				}
			}
		}

		assertNotNull(vessel);
		assertEquals(updateEvent.getAggregateId(), vessel.getId());
		assertEquals(updateEvent.getVessel().getId(), vessel.getId());
		assertEquals(updateEvent.getVessel().getMmsi(), vessel.getMmsi());
		assertEquals(updateEvent.getVessel().getName(), vessel.getName());
	}
}
