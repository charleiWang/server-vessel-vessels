package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

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
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEnrichedEvent;
import es.redmic.vesselslib.events.vessel.update.EnrichUpdateVesselEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselFromAIS", "schema.registry.port=18082" })
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "CreateVesselFromAISTest")
public class CreateVesselFromAISTest extends KafkaBaseIntegrationTest {

	@Value("${broker.topic.realtime.vessels}")
	String REALTIME_VESSELS_TOPIC;

	@Value("${broker.topic.vessel}")
	String VESSEL_TOPIC;

	private Integer mmsi = 111;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	@Autowired
	private KafkaTemplate<String, VesselDTO> kafkaTemplate;

	@Autowired
	private KafkaTemplate<String, Event> kafkaEventTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@PostConstruct
	public void CreateVesselFromAISTestPostConstruct() throws Exception {
		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void createVessel_SendCreateVesselEvent_IfCommandWasSuccess() throws Exception {

		VesselDTO source = VesselDataUtil.getVessel(mmsi);

		// Envía un vessel simulando que llega desde ais. Al no haber llegado antes debe
		// generar un evento
		kafkaTemplate.send(REALTIME_VESSELS_TOPIC, source.getId(), source);

		VesselDTO vessel = (VesselDTO) blockingQueue.poll(30, TimeUnit.SECONDS);

		assertNotNull(vessel);
		assertEquals(vessel.getMmsi(), source.getMmsi());
		assertEquals(vessel.getName(), source.getName());
		assertEquals(vessel.getLength(), source.getLength());
		assertEquals(vessel.getBeam(), source.getBeam());

		Thread.sleep(3000);

		// Envía un evento de vessel creado para simularlo. Al ya estar creado, solo
		// genera evento si ha cambiado
		kafkaEventTemplate.send(VESSEL_TOPIC, source.getId(), VesselDataUtil.getVesselCreatedEvent(mmsi));

		Thread.sleep(3000);

		// Al estar ya añadido, si
		// llega de nuevo el mismo barco, lo descartará y no llegará nada

		VesselDTO source2 = VesselDataUtil.getVessel(mmsi);

		kafkaTemplate.send(REALTIME_VESSELS_TOPIC, source2.getId(), source2);

		vessel = (VesselDTO) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNull(vessel);

		// En este caso se cambia el nombre y se comprueba que genera un
		// evento para modificarlo
		source2.setName("Otro");

		kafkaTemplate.send(REALTIME_VESSELS_TOPIC, source2.getId(), source2);

		Thread.sleep(1000);

		kafkaTemplate.send(REALTIME_VESSELS_TOPIC, source.getId(), source);

		Thread.sleep(3000);

		vessel = (VesselDTO) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(vessel);
		assertEquals("Otro", vessel.getName());

		vessel = (VesselDTO) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNull(vessel);
	}

	@KafkaHandler
	public void listen(CreateVesselEnrichedEvent createVesselEnrichedEvent) {

		blockingQueue.offer(createVesselEnrichedEvent.getVessel());
	}

	@KafkaHandler
	public void listen(EnrichUpdateVesselEvent enrichUpdateVesselEvent) {

		blockingQueue.offer(enrichUpdateVesselEvent.getVessel());
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}