package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
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
import es.redmic.exception.common.ExceptionType;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.common.VesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "testPostUpdate")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselPostUpdateHandlerTest",
		"schema.registry.port=18085" })
public class VesselPostUpdateHandlerTest extends KafkaBaseIntegrationTest {

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final Integer mmsi = 3333;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Value("${broker.topic.vessel-type}")
	private String VESSELTYPE_TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected BlockingQueue<Object> blockingQueue;

	protected BlockingQueue<Object> blockingQueueForAlerts;

	protected BlockingQueue<Object> blockingQueueForUpdatedEvents;

	protected BlockingQueue<Object> blockingQueueForCancelledEvents;

	@PostConstruct
	public void VesselPostUpdateHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Before
	public void before() {

		blockingQueue = new LinkedBlockingDeque<>();

		blockingQueueForAlerts = new LinkedBlockingDeque<>();

		blockingQueueForUpdatedEvents = new LinkedBlockingDeque<>();

		blockingQueueForCancelledEvents = new LinkedBlockingDeque<>();
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
		Thread.sleep(3000);

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent2 = VesselDataUtil.getVesselCreatedEvent(mmsi + 8);
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent2.getAggregateId(), vesselCreatedEvent2);
		Thread.sleep(3000);

		// Envía created con vesselType actualizado para comprobar que no genera evento
		VesselCreatedEvent vesselCreatedEvent3 = VesselDataUtil.getVesselCreatedEvent(mmsi + 9);
		vesselCreatedEvent3.getVessel().setType(vesselTypeUpdatedEvent.getVesselType());
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent3.getAggregateId(), vesselCreatedEvent3);
		Thread.sleep(3000);

		// Envía create para simular uno a medias en el stream y lo saca de la cola
		CreateVesselEvent createVesselEvent = VesselDataUtil.getCreateEvent(mmsi + 10);
		kafkaTemplate.send(VESSEL_TOPIC, createVesselEvent.getAggregateId(), createVesselEvent);
		Thread.sleep(3000);

		// Envía evento de vesselType actualizado para que genere los eventos de
		// postUpdate
		kafkaTemplate.send(VESSELTYPE_TOPIC, vesselTypeUpdatedEvent.getAggregateId(), vesselTypeUpdatedEvent);
		Thread.sleep(2000);

		UpdateVesselTypeInVesselEvent update = (UpdateVesselTypeInVesselEvent) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(VesselEventTypes.UPDATE_VESSELTYPE, update.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), update.getVesselType());
		assertEquals(vesselCreatedEvent.getAggregateId(), update.getAggregateId());

		// Envía confirmación para simular que view lo insertó
		kafkaTemplate.send(VESSEL_TOPIC, update.getAggregateId(), new UpdateVesselConfirmedEvent().buildFrom(update));

		UpdateVesselTypeInVesselEvent update2 = (UpdateVesselTypeInVesselEvent) blockingQueue.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(update2);
		assertEquals(VesselEventTypes.UPDATE_VESSELTYPE, update2.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), update2.getVesselType());
		assertEquals(vesselCreatedEvent2.getAggregateId(), update2.getAggregateId());

		// Envía fallo para simular que view no lo insertó
		UpdateVesselFailedEvent updateVesselFailedEvent = new UpdateVesselFailedEvent().buildFrom(update2);
		updateVesselFailedEvent.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		updateVesselFailedEvent.setArguments(arguments);
		kafkaTemplate.send(VESSEL_TOPIC, update2.getAggregateId(), updateVesselFailedEvent);

		Event update3 = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNull(update3);

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());

		// Se modificó bien el primer vessel
		VesselUpdatedEvent updated = (VesselUpdatedEvent) blockingQueueForUpdatedEvents.poll(30, TimeUnit.SECONDS);
		assertNotNull(updated);
		assertEquals(VesselEventTypes.UPDATED, updated.getType());
		assertEquals(vesselTypeUpdatedEvent.getVesselType(), updated.getVessel().getType());
		assertEquals(vesselCreatedEvent.getAggregateId(), updated.getAggregateId());

		// No se modificó bien el segundo vessel
		VesselCancelledEvent cancelled = (VesselCancelledEvent) blockingQueueForCancelledEvents.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(cancelled);
		assertEquals(VesselEventTypes.UPDATE_CANCELLED, cancelled.getType());
		assertEquals(vesselCreatedEvent2.getVessel().getType(), cancelled.getVessel().getType());
		assertEquals(vesselCreatedEvent2.getAggregateId(), cancelled.getAggregateId());

		// LLegó un mensaje de alerta
		Message message2 = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message2);
		assertEquals(AlertType.ERROR.name(), message2.getType());
	}

	@KafkaHandler
	public void updateVesselEventFromPostUpdate(UpdateVesselTypeInVesselEvent updateVesselEvent) {

		blockingQueue.offer(updateVesselEvent);
	}

	@KafkaHandler
	public void vesselUpdatedEventFromPostUpdate(VesselUpdatedEvent vesselUpdatedEvent) {

		blockingQueueForUpdatedEvents.offer(vesselUpdatedEvent);
	}

	@KafkaHandler
	public void vesselCancelledEventFromPostUpdate(VesselCancelledEvent vesselCancelledEvent) {

		blockingQueueForCancelledEvents.offer(vesselCancelledEvent);
	}

	@KafkaListener(topics = "${broker.topic.alert}", groupId = "test")
	public void errorAlert(Message message) {
		blockingQueueForAlerts.offer(message);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
