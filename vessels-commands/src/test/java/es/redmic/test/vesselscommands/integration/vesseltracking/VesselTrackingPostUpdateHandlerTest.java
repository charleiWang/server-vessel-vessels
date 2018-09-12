package es.redmic.test.vesselscommands.integration.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
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
import es.redmic.test.vesselscommands.integration.vessel.VesselDataUtil;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.common.VesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.partialupdate.vessel.UpdateVesselInVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel-tracking}", groupId = "testPostUpdate")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselTrackingPostUpdateHandlerTest",
		"schema.registry.port=18185" })
public class VesselTrackingPostUpdateHandlerTest extends KafkaBaseIntegrationTest {

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final Integer mmsi = 3333;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Value("${broker.topic.vessel-tracking}")
	private String VESSELTRACKING_TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected BlockingQueue<Object> blockingQueue;

	protected BlockingQueue<Object> blockingQueueForAlerts;

	protected BlockingQueue<Object> blockingQueueForUpdatedEvents;

	protected BlockingQueue<Object> blockingQueueForCancelledEvents;

	@PostConstruct
	public void VesselTrackingPostUpdateHandlerTestPostConstruct() throws Exception {

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

	// Envía un evento de vessel modificado y debe provocar un evento de
	// vesselTracking
	// modificado para cada uno de los vesselTracking que tiene el vessel
	// modificado.
	@Test
	public void vesselUpdatedEvent_SendUpdateVesselTrackingEvent_IfReceivesSuccess() throws Exception {

		// Referencia a modificar
		VesselUpdatedEvent vesselUpdatedEvent = VesselDataUtil.getVesselUpdatedEvent(mmsi);
		vesselUpdatedEvent.getVessel().setName("other");

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselTrackingCreatedEvent vesselTrackingCreatedEvent = VesselTrackingDataUtil
				.getVesselTrackingCreatedEvent(mmsi, String.valueOf(new DateTime().getMillis()));
		kafkaTemplate.send(VESSELTRACKING_TOPIC, vesselTrackingCreatedEvent.getAggregateId(),
				vesselTrackingCreatedEvent);
		Thread.sleep(3000);

		// Envía created para que genere un evento postUpdate y lo saca de la cola
		VesselTrackingCreatedEvent vesselTrackingCreatedEvent2 = VesselTrackingDataUtil
				.getVesselTrackingCreatedEvent(mmsi, String.valueOf(new DateTime().getMillis()));
		kafkaTemplate.send(VESSELTRACKING_TOPIC, vesselTrackingCreatedEvent2.getAggregateId(),
				vesselTrackingCreatedEvent2);
		Thread.sleep(3000);

		// Envía created con vessel actualizado para comprobar que no genera evento
		VesselTrackingCreatedEvent vesselTrackingCreatedEvent3 = VesselTrackingDataUtil
				.getVesselTrackingCreatedEvent(mmsi, String.valueOf(new DateTime().getMillis()));
		vesselTrackingCreatedEvent3.getVesselTracking().getProperties().setVessel(vesselUpdatedEvent.getVessel());
		kafkaTemplate.send(VESSELTRACKING_TOPIC, vesselTrackingCreatedEvent3.getAggregateId(),
				vesselTrackingCreatedEvent3);
		Thread.sleep(3000);

		// Envía create para simular uno a medias en el stream y lo saca de la cola
		CreateVesselTrackingEvent createVesselTrackingEvent = VesselTrackingDataUtil.getCreateEvent(mmsi,
				String.valueOf(new DateTime().getMillis()));
		kafkaTemplate.send(VESSELTRACKING_TOPIC, createVesselTrackingEvent.getAggregateId(), createVesselTrackingEvent);
		Thread.sleep(3000);

		// Envía evento de vessel actualizado para que genere los eventos de
		// postUpdate
		kafkaTemplate.send(VESSEL_TOPIC, vesselUpdatedEvent.getAggregateId(), vesselUpdatedEvent);
		Thread.sleep(2000);

		UpdateVesselInVesselTrackingEvent update = (UpdateVesselInVesselTrackingEvent) blockingQueue.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(update);
		assertEquals(VesselTrackingEventTypes.UPDATE_VESSEL, update.getType());
		assertEquals(vesselUpdatedEvent.getVessel(), update.getVessel());
		assertEquals(vesselTrackingCreatedEvent.getAggregateId(), update.getAggregateId());

		// Envía confirmación para simular que view lo insertó
		kafkaTemplate.send(VESSELTRACKING_TOPIC, update.getAggregateId(),
				new UpdateVesselTrackingConfirmedEvent().buildFrom(update));

		UpdateVesselInVesselTrackingEvent update2 = (UpdateVesselInVesselTrackingEvent) blockingQueue.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(update2);
		assertEquals(VesselTrackingEventTypes.UPDATE_VESSEL, update2.getType());
		assertEquals(vesselUpdatedEvent.getVessel(), update2.getVessel());
		assertEquals(vesselTrackingCreatedEvent2.getAggregateId(), update2.getAggregateId());

		// Envía fallo para simular que view no lo insertó
		UpdateVesselTrackingFailedEvent updateVesselTrackingFailedEvent = new UpdateVesselTrackingFailedEvent()
				.buildFrom(update2);
		updateVesselTrackingFailedEvent.setExceptionType(ExceptionType.ITEM_NOT_FOUND.name());
		Map<String, String> arguments = new HashMap<>();
		arguments.put("A", "B");
		updateVesselTrackingFailedEvent.setArguments(arguments);
		kafkaTemplate.send(VESSELTRACKING_TOPIC, update2.getAggregateId(), updateVesselTrackingFailedEvent);

		Event update3 = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNull(update3);

		// LLegó un mensaje de alerta
		Message message = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message);
		assertEquals(AlertType.ERROR.name(), message.getType());

		// Se modificó bien el primer vesselTracking
		VesselTrackingUpdatedEvent updated = (VesselTrackingUpdatedEvent) blockingQueueForUpdatedEvents.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(updated);
		assertEquals(VesselTrackingEventTypes.UPDATED, updated.getType());
		assertEquals(vesselUpdatedEvent.getVessel(), updated.getVesselTracking().getProperties().getVessel());
		assertEquals(vesselTrackingCreatedEvent.getAggregateId(), updated.getAggregateId());

		// No se modificó bien el segundo vesselTracking
		VesselTrackingCancelledEvent cancelled = (VesselTrackingCancelledEvent) blockingQueueForCancelledEvents.poll(30,
				TimeUnit.SECONDS);
		assertNotNull(cancelled);
		assertEquals(VesselTrackingEventTypes.UPDATE_CANCELLED, cancelled.getType());
		assertEquals(vesselTrackingCreatedEvent2.getVesselTracking().getProperties().getVessel(),
				cancelled.getVesselTracking().getProperties().getVessel());
		assertEquals(vesselTrackingCreatedEvent2.getAggregateId(), cancelled.getAggregateId());

		// LLegó un mensaje de alerta
		Message message2 = (Message) blockingQueueForAlerts.poll(30, TimeUnit.SECONDS);
		assertNotNull(message2);
		assertEquals(AlertType.ERROR.name(), message2.getType());
	}

	@KafkaHandler
	public void updateVesselEventFromPostUpdate(UpdateVesselInVesselTrackingEvent updateVesselTrackingEvent) {

		blockingQueue.offer(updateVesselTrackingEvent);
	}

	@KafkaHandler
	public void vesselTrackingUpdatedEventFromPostUpdate(VesselTrackingUpdatedEvent vesselTrackingUpdatedEvent) {

		blockingQueueForUpdatedEvents.offer(vesselTrackingUpdatedEvent);
	}

	@KafkaHandler
	public void vesselTrackingCancelledEventFromPostUpdate(VesselTrackingCancelledEvent vesselTrackingCancelledEvent) {

		blockingQueueForCancelledEvents.offer(vesselTrackingCancelledEvent);
	}

	@KafkaListener(topics = "${broker.topic.alert}", groupId = "test")
	public void errorAlert(Message message) {
		blockingQueueForAlerts.offer(message);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
