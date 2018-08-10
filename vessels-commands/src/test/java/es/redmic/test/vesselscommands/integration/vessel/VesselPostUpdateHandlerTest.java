package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import es.redmic.brokerlib.alert.AlertType;
import es.redmic.brokerlib.alert.Message;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "testPostUpdate")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselPostUpdateHandlerTest",
		"schema.registry.port=18085" })
public class VesselPostUpdateHandlerTest extends KafkaBaseIntegrationTest {

	// number of brokers.
	private final static Integer numBrokers = 3;
	// partitions per topic.
	private final static Integer partitionsPerTopic = 3;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(numBrokers, true, partitionsPerTopic);

	private static final Integer mmsi = 3333;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Value("${broker.topic.vessel-type}")
	private String VESSELTYPE_TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected BlockingQueue<Object> blockingQueue;

	protected BlockingQueue<Object> blockingQueueForAlerts;

	@PostConstruct
	public void VesselPostUpdateHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Before
	public void before() {

		blockingQueue = new LinkedBlockingDeque<>();

		blockingQueueForAlerts = new LinkedBlockingDeque<>();
	}

	// PostUpdate

	// Envía un evento de vesseltype modificado y debe provocar un evento de vessel
	// modificado para cada uno de los vessel que tiene el vesseltype modificado.
	@Test
	public void vesselTypeUpdatedEvent_SendUpdateVesselEvent_IfReceivesSuccess() throws Exception {

		// Referencia a modificar
		VesselTypeUpdatedEvent vesselTypeUpdatedEvent = VesselTypeDataUtil.getVesselTypeUpdatedEvent("70");
		vesselTypeUpdatedEvent.getVesselType().setName("other");

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi + 7);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);

		Thread.sleep(2000);

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent2 = VesselDataUtil.getVesselCreatedEvent(mmsi + 8);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent2.getAggregateId(), vesselCreatedEvent2);

		Thread.sleep(2000);

		// Envía created con vesselType actualizado para comprobar que no genera evento
		VesselCreatedEvent vesselCreatedEvent3 = VesselDataUtil.getVesselCreatedEvent(mmsi + 9);
		vesselCreatedEvent3.getVessel().setType(vesselTypeUpdatedEvent.getVesselType());
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent3.getAggregateId(), vesselCreatedEvent3);

		Thread.sleep(2000);

		// Envía create para simular uno a medias en el stream y lo saca de la cola
		CreateVesselEvent createVesselEvent = VesselDataUtil.getCreateEvent(mmsi + 10);
		kafkaTemplate.send(VESSEL_TOPIC, createVesselEvent.getAggregateId(), createVesselEvent);

		Thread.sleep(2000);

		// Envía evento de vesselType actualizado para que genere los eventos de
		// postUpdate
		kafkaTemplate.send(VESSELTYPE_TOPIC, vesselTypeUpdatedEvent.getAggregateId(), vesselTypeUpdatedEvent);

		Event update = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(VesselEventTypes.UPDATE, update.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), ((UpdateVesselEvent) update).getVessel().getType());
		assertEquals(vesselCreatedEvent.getAggregateId(), ((UpdateVesselEvent) update).getAggregateId());

		Event update2 = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(update2);
		assertEquals(VesselEventTypes.UPDATE, update2.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), ((UpdateVesselEvent) update2).getVessel().getType());
		assertEquals(vesselCreatedEvent2.getAggregateId(), ((UpdateVesselEvent) update2).getAggregateId());

		Event update3 = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);
		assertNull(update3);

		Message message = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());
	}

	@KafkaHandler
	public void updateVesselEventFromPostUpdate(UpdateVesselEvent updateVesselEvent) {

		blockingQueue.offer(updateVesselEvent);
	}

	@KafkaListener(topics = "${broker.topic.alert}", groupId = "test")
	public void errorAlert(Message message) {
		blockingQueueForAlerts.offer(message);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
