package es.redmic.test.vesselscommands.integration.vesseltracking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
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

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.DeleteItemException;
import es.redmic.exception.data.ItemAlreadyExistException;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.test.vesselscommands.integration.vessel.VesselDataUtil;
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.handler.VesselTrackingCommandHandler;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.EnrichUpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingCancelledEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel-tracking}", groupId = "VesselTrackingCommandHandlerTest")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselTrackingCommandHandler",
		"schema.registry.port=18184" })
public class VesselTrackingCommandHandlerTest extends KafkaBaseIntegrationTest {

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private static final Integer mmsi = 6666;

	@Value("${broker.topic.vessel}")
	private String vessel_topic;

	@Value("${broker.topic.vessel-tracking}")
	private String vessel_tracking_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	VesselTrackingCommandHandler vesselTrackingCommandHandler;

	@PostConstruct
	public void VesselTrackingCommandHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Before
	public void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	// Success cases

	// Envía un evento de enriquecimiento de creación y debe provocar un evento
	// Create con el item dentro
	@Test
	public void enrichCreateVesselTrackingEvent_SendCreateVesselTrackingEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> createVesselTrackingEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		// Envía vesselCreated
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi);
		kafkaTemplate.send(vessel_topic, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);

		Thread.sleep(2000);

		// Envía enrichCreateVesselTracking con id del vessel igual al enviado

		EnrichCreateVesselTrackingEvent enrichCreateVesselTrackingEvent = VesselTrackingDataUtil
				.getEnrichCreateVesselTrackingEvent(mmsi, tstamp);
		enrichCreateVesselTrackingEvent.setSessionId(UUID.randomUUID().toString());

		VesselDTO vessel = VesselDataUtil.getVessel(mmsi);
		vessel.setName(null);
		vessel.setType(null);

		enrichCreateVesselTrackingEvent.getVesselTracking().getProperties().setVessel(vessel);

		kafkaTemplate.send(vessel_tracking_topic, enrichCreateVesselTrackingEvent.getAggregateId(),
				enrichCreateVesselTrackingEvent);

		// Comprueba que recibe createVesselTrackingEvent con vessel enriquecido
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.CREATE, confirm.getType());

		assertEquals(vesselCreatedEvent.getVessel(),
				((CreateVesselTrackingEvent) confirm).getVesselTracking().getProperties().getVessel());
	}

	// Envía un evento de confirmación de creación y debe provocar un evento Created
	// con el item dentro
	@Test
	public void createVesselTrackingConfirmedEvent_SendVesselTrackingCreatedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> createVesselTrackingConfirmedEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		// Envía create para meterlo en el stream
		CreateVesselTrackingEvent createVesselTrackingEvent = VesselTrackingDataUtil.getCreateEvent(mmsi + 1, tstamp);
		kafkaTemplate.send(vessel_tracking_topic, createVesselTrackingEvent.getAggregateId(),
				createVesselTrackingEvent);
		Event request = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(request);

		// Envía confirmed y espera un evento de created con el vesselTracking original
		// dentro
		CreateVesselTrackingConfirmedEvent event = VesselTrackingDataUtil
				.getCreateVesselTrackingConfirmedEvent(mmsi + 1, tstamp);
		kafkaTemplate.send(vessel_tracking_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.CREATED, confirm.getType());

		assertEquals(createVesselTrackingEvent.getVesselTracking(),
				((VesselTrackingCreatedEvent) confirm).getVesselTracking());
	}

	// Envía un evento de enriquecimiento de modificación y debe provocar un evento
	// Update con el item dentro
	@Test
	public void enrichUpdateVesselTrackingEvent_SendUpdateVesselTrackingEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> updateVesselTrackingEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		// Envía vesselCreated
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi);
		kafkaTemplate.send(vessel_topic, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);

		Thread.sleep(2000);

		// Envía enrichUpdateVesselTracking con id del vessel igual al enviado

		EnrichUpdateVesselTrackingEvent enrichUpdateVesselTrackingEvent = VesselTrackingDataUtil
				.getEnrichUpdateVesselTrackingEvent(mmsi, tstamp);
		enrichUpdateVesselTrackingEvent.setSessionId(UUID.randomUUID().toString());

		VesselDTO vessel = VesselDataUtil.getVessel(mmsi);
		vessel.setName(null);
		vessel.setType(null);

		enrichUpdateVesselTrackingEvent.getVesselTracking().getProperties().setVessel(vessel);

		kafkaTemplate.send(vessel_tracking_topic, enrichUpdateVesselTrackingEvent.getAggregateId(),
				enrichUpdateVesselTrackingEvent);

		// Comprueba que recibe UpdateVesselTrackingEvent con vessel enriquecido
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.UPDATE, confirm.getType());

		assertEquals(vesselCreatedEvent.getVessel(),
				((UpdateVesselTrackingEvent) confirm).getVesselTracking().getProperties().getVessel());
	}

	// Envía un evento de confirmación de modificación y debe provocar un evento
	// Updated con el item dentro
	@Test
	public void updateVesselTrackingConfirmedEvent_SendVesselTrackingUpdatedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> updateVesselTrackingConfirmedEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		// Envía update para meterlo en el stream
		UpdateVesselTrackingEvent updateVesselTrackingEvent = VesselTrackingDataUtil.getUpdateEvent(mmsi + 2, tstamp);
		kafkaTemplate.send(vessel_tracking_topic, updateVesselTrackingEvent.getAggregateId(),
				updateVesselTrackingEvent);
		Event request = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);
		assertNotNull(request);

		// Envía confirmed y espera un evento de updated con el vesselTracking original
		// dentro
		UpdateVesselTrackingConfirmedEvent event = VesselTrackingDataUtil
				.getUpdateVesselTrackingConfirmedEvent(mmsi + 2, tstamp);
		kafkaTemplate.send(vessel_tracking_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.UPDATED, confirm.getType());
		assertEquals(updateVesselTrackingEvent.getVesselTracking(),
				((VesselTrackingUpdatedEvent) confirm).getVesselTracking());
	}

	// Envía un evento de confirmación de borrado y debe provocar un evento Deleted
	@Test
	public void deleteVesselTrackingConfirmedEvent_SendVesselTrackingDeletedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> DeleteVesselTrackingConfirmedEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		DeleteVesselTrackingConfirmedEvent event = VesselTrackingDataUtil
				.getDeleteVesselTrackingConfirmedEvent(mmsi + 3, tstamp);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(vessel_tracking_topic,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.DELETED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Fail cases

	// Envía un evento de error de creación y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = ItemAlreadyExistException.class)
	public void createVesselTrackingFailedEvent_SendVesselTrackingCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createVesselTrackingFailedEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		CreateVesselTrackingFailedEvent event = VesselTrackingDataUtil.getCreateVesselTrackingFailedEvent(mmsi + 4,
				tstamp);

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselTrackingDTO> completableFuture = Whitebox.invokeMethod(vesselTrackingCommandHandler,
				"getCompletableFeature", event.getSessionId(),
				VesselTrackingDataUtil.getVesselTracking(mmsi + 4, tstamp));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(vessel_tracking_topic,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselTrackingCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.CREATE_CANCELLED, confirm.getType());
	}

	// Envía un evento de error de modificación y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void updateVesselTrackingFailedEvent_SendVesselTrackingCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> updateVesselTrackingFailedEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		// Envía created para meterlo en el stream y lo saca de la cola
		VesselTrackingCreatedEvent vesselTrackingCreatedEvent = VesselTrackingDataUtil
				.getVesselTrackingCreatedEvent(mmsi + 5, tstamp);
		vesselTrackingCreatedEvent.setSessionId(UUID.randomUUID().toString());
		vesselTrackingCreatedEvent.getVesselTracking().getProperties().getVessel().setName("Nombre erroneo al crearlo");
		kafkaTemplate.send(vessel_tracking_topic, vesselTrackingCreatedEvent.getAggregateId(),
				vesselTrackingCreatedEvent);
		Event created = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);
		assertNotNull(created);

		// Envía updated para meterlo en el stream y lo saca de la cola
		VesselTrackingUpdatedEvent vesselTrackingUpdatedEvent = VesselTrackingDataUtil
				.getVesselTrackingUpdatedEvent(mmsi + 5, tstamp);
		vesselTrackingUpdatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(vessel_tracking_topic, vesselTrackingUpdatedEvent.getAggregateId(),
				vesselTrackingUpdatedEvent);
		Event updated = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);
		assertNotNull(updated);

		// Envía failed y espera un evento de cancelled con el vesselTracking original
		// dentro
		UpdateVesselTrackingFailedEvent event = VesselTrackingDataUtil.getUpdateVesselTrackingFailedEvent(mmsi + 5,
				tstamp);

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselTrackingDTO> completableFuture = Whitebox.invokeMethod(vesselTrackingCommandHandler,
				"getCompletableFeature", event.getSessionId(), vesselTrackingUpdatedEvent.getVesselTracking());

		kafkaTemplate.send(vessel_tracking_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselTrackingCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.UPDATE_CANCELLED, confirm.getType());
		assertEquals(vesselTrackingUpdatedEvent.getVesselTracking(),
				((UpdateVesselTrackingCancelledEvent) confirm).getVesselTracking());
	}

	// Envía un evento de error de borrado y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = DeleteItemException.class)
	public void deleteVesselTrackingFailedEvent_SendVesselTrackingCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> deleteVesselTrackingFailedEvent");

		String tstamp = String.valueOf(new DateTime().getMillis());

		// Envía created para meterlo en el stream y lo saca de la cola
		VesselTrackingCreatedEvent vesselTrackingCreatedEvent = VesselTrackingDataUtil
				.getVesselTrackingCreatedEvent(mmsi + 6, tstamp);
		vesselTrackingCreatedEvent.setSessionId(UUID.randomUUID().toString());
		vesselTrackingCreatedEvent.getVesselTracking().getProperties().getVessel().setName("Nombre erroneo al crearlo");
		kafkaTemplate.send(vessel_tracking_topic, vesselTrackingCreatedEvent.getAggregateId(),
				vesselTrackingCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		VesselTrackingUpdatedEvent vesselTrackingUpdateEvent = VesselTrackingDataUtil
				.getVesselTrackingUpdatedEvent(mmsi + 6, tstamp);
		vesselTrackingUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(vessel_tracking_topic, vesselTrackingUpdateEvent.getAggregateId(),
				vesselTrackingUpdateEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía failed y espera un evento de cancelled con el vesselTracking original
		// dentro
		DeleteVesselTrackingFailedEvent event = VesselTrackingDataUtil.getDeleteVesselTrackingFailedEvent(mmsi + 6,
				tstamp);

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselTrackingDTO> completableFuture = Whitebox.invokeMethod(vesselTrackingCommandHandler,
				"getCompletableFeature", event.getSessionId(), vesselTrackingUpdateEvent.getVesselTracking());

		kafkaTemplate.send(vessel_tracking_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselTrackingCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.DELETE_CANCELLED, confirm.getType());
		assertEquals(vesselTrackingUpdateEvent.getVesselTracking(),
				((DeleteVesselTrackingCancelledEvent) confirm).getVesselTracking());
	}

	@KafkaHandler
	public void createVesselTrackingEvent(CreateVesselTrackingEvent createdVesselTrackingEvent) {

		blockingQueue.offer(createdVesselTrackingEvent);
	}

	@KafkaHandler
	public void vesselTrackingCreatedEvent(VesselTrackingCreatedEvent vesselTrackingCreatedEvent) {

		blockingQueue.offer(vesselTrackingCreatedEvent);
	}

	@KafkaHandler
	public void createVesselTrackingCancelledEvent(
			CreateVesselTrackingCancelledEvent createVesselTrackingCancelledEvent) {

		blockingQueue.offer(createVesselTrackingCancelledEvent);
	}

	@KafkaHandler
	public void updatedVesselTrackingEvent(UpdateVesselTrackingEvent updatedVesselTrackingEvent) {

		blockingQueue.offer(updatedVesselTrackingEvent);
	}

	@KafkaHandler
	public void vesselTrackingUpdatedEvent(VesselTrackingUpdatedEvent vesselTrackingUpdatedEvent) {

		blockingQueue.offer(vesselTrackingUpdatedEvent);
	}

	@KafkaHandler
	public void updateVesselTrackingCancelledEvent(
			UpdateVesselTrackingCancelledEvent updateVesselTrackingCancelledEvent) {

		blockingQueue.offer(updateVesselTrackingCancelledEvent);
	}

	@KafkaHandler
	public void vesselTrackingDeletedEvent(VesselTrackingDeletedEvent vesselTrackingDeletedEvent) {

		blockingQueue.offer(vesselTrackingDeletedEvent);
	}

	@KafkaHandler
	public void deleteVesselTrackingCancelledEvent(
			DeleteVesselTrackingCancelledEvent deleteVesselTrackingCancelledEvent) {

		blockingQueue.offer(deleteVesselTrackingCancelledEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
