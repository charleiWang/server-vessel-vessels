package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.EnrichCreateVesselEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselFromAIS", "schema.registry.port=18082" })
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "CreateVesselFromAISTest")
public class CreateVesselFromAISTest extends KafkaBaseIntegrationTest {

	@Value("${broker.topic.realtime.tracking.vessels}")
	String REALTIME_TRACKING_VESSELS_TOPIC;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	@Autowired
	private KafkaTemplate<String, AISTrackingDTO> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@PostConstruct
	public void CreateVesselFromAISTestPostConstruct() throws Exception {
		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void createVessel_SendCreateVesselEvent_IfCommandWasSuccess() throws Exception {

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(2);
		dto.setImo(1);
		dto.setName("Avatar");
		dto.setType(70);
		dto.setCallSign("1");
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
		dto.setA(2.9);
		dto.setB(3.5);
		dto.setC(1.4);
		dto.setD(1.4);

		ListenableFuture<SendResult<String, AISTrackingDTO>> future = kafkaTemplate
				.send(REALTIME_TRACKING_VESSELS_TOPIC, "vessel-mmsi-" + dto.getMmsi(), dto);
		future.addCallback(new SendListener());

		VesselDTO vessel = (VesselDTO) blockingQueue.poll(120, TimeUnit.SECONDS);
		assertNotNull(vessel);
		assertEquals(vessel.getMmsi(), dto.getMmsi());
		assertEquals(vessel.getName(), dto.getName());
		Double length = dto.getA() + dto.getB();
		assertEquals(vessel.getLength(), length);

		Double beam = dto.getC() + dto.getD();
		assertEquals(vessel.getBeam(), beam);
	}

	@KafkaHandler
	public void listen(EnrichCreateVesselEvent createEvent) {

		blockingQueue.offer(createEvent.getVessel());
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
