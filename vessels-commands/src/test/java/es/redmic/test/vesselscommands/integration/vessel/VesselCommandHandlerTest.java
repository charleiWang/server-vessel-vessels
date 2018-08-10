package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.commands.VesselCommandHandler;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.VesselDeletedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "${random.value}")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselCommandHandlerTest",
		"schema.registry.port=18084" })
public class VesselCommandHandlerTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	// number of brokers.
	private final static Integer numBrokers = 3;
	// partitions per topic.
	private final static Integer partitionsPerTopic = 3;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(numBrokers, true, partitionsPerTopic);

	private static final Integer mmsi = 1234;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	VesselCommandHandler vesselCommandHandler;

	@PostConstruct
	public void VesselCommandHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@Before
	public void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	// Success cases

	// Envía un evento de confirmación de creación y debe provocar un evento Created
	// con el item dentro
	@Test
	public void createVesselConfirmedEvent_SendVesselCreatedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> createVesselConfirmedEvent");
		// Envía create para meterlo en el stream
		CreateVesselEvent createVesselEvent = VesselDataUtil.getCreateEvent(mmsi + 1);
		kafkaTemplate.send(VESSEL_TOPIC, createVesselEvent.getAggregateId(), createVesselEvent);

		// Envía confirmed y espera un evento de created con el vessel original dentro
		CreateVesselConfirmedEvent event = VesselDataUtil.getCreateVesselConfirmedEvent(mmsi + 1);
		kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.CREATED, confirm.getType());

		assertEquals(createVesselEvent.getVessel(), ((VesselCreatedEvent) confirm).getVessel());
	}

	// Envía un evento de confirmación de modificación y debe provocar un evento
	// Updated con el item dentro
	@Test
	public void updateVesselConfirmedEvent_SendVesselUpdatedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> updateVesselConfirmedEvent");
		// Envía update para meterlo en el stream
		UpdateVesselEvent updateVesselEvent = VesselDataUtil.getUpdateEvent(mmsi + 2);
		kafkaTemplate.send(VESSEL_TOPIC, updateVesselEvent.getAggregateId(), updateVesselEvent);

		// Envía confirmed y espera un evento de updated con el vessel original dentro
		UpdateVesselConfirmedEvent event = VesselDataUtil.getUpdateVesselConfirmedEvent(mmsi + 2);
		kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.UPDATED, confirm.getType());
		assertEquals(updateVesselEvent.getVessel(), ((VesselUpdatedEvent) confirm).getVessel());
	}

	// Envía un evento de confirmación de borrado y debe provocar un evento Deleted
	@Test
	public void deleteVesselConfirmedEvent_SendVesselDeletedEvent_IfReceivesSuccess() throws InterruptedException {

		logger.debug("----> DeleteVesselConfirmedEvent");
		DeleteVesselConfirmedEvent event = VesselDataUtil.getDeleteVesselConfirmedEvent(mmsi + 3);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.DELETED, confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Fail cases

	// Envía un evento de error de creación y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = ItemAlreadyExistException.class)
	public void createVesselFailedEvent_SendVesselCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createVesselFailedEvent");
		CreateVesselFailedEvent event = VesselDataUtil.getCreateVesselFailedEvent(mmsi + 4);

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselDTO> completableFuture = Whitebox.invokeMethod(vesselCommandHandler,
				"getCompletableFeature", event.getSessionId(), VesselDataUtil.getVessel(mmsi + 4));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.CREATE_CANCELLED, confirm.getType());
	}

	// Envía un evento de error de modificación y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void updateVesselFailedEvent_SendVesselCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> updateVesselFailedEvent");
		// Envía created para meterlo en el stream y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi + 5);
		vesselCreatedEvent.setSessionId(UUID.randomUUID().toString());
		vesselCreatedEvent.getVessel().setName("Nombre erroneo al crearlo");
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);
		Event created = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);
		assertNotNull(created);

		// Envía updated para meterlo en el stream y lo saca de la cola
		VesselUpdatedEvent vesselUpdatedEvent = VesselDataUtil.getVesselUpdatedEvent(mmsi + 5);
		vesselUpdatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(VESSEL_TOPIC, vesselUpdatedEvent.getAggregateId(), vesselUpdatedEvent);
		Event updated = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);
		assertNotNull(updated);

		// Envía failed y espera un evento de cancelled con el vessel original dentro
		UpdateVesselFailedEvent event = VesselDataUtil.getUpdateVesselFailedEvent(mmsi + 5);

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselDTO> completableFuture = Whitebox.invokeMethod(vesselCommandHandler,
				"getCompletableFeature", event.getSessionId(), vesselUpdatedEvent.getVessel());

		kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.UPDATE_CANCELLED, confirm.getType());
		assertEquals(vesselUpdatedEvent.getVessel(), ((UpdateVesselCancelledEvent) confirm).getVessel());
	}

	// Envía un evento de error de borrado y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = DeleteItemException.class)
	public void deleteVesselFailedEvent_SendVesselCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> deleteVesselFailedEvent");
		// Envía created para meterlo en el stream y lo saca de la cola
		VesselCreatedEvent vesselCreatedEvent = VesselDataUtil.getVesselCreatedEvent(mmsi + 6);
		vesselCreatedEvent.setSessionId(UUID.randomUUID().toString());
		vesselCreatedEvent.getVessel().setName("Nombre erroneo al crearlo");
		kafkaTemplate.send(VESSEL_TOPIC, vesselCreatedEvent.getAggregateId(), vesselCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		VesselUpdatedEvent vesselUpdateEvent = VesselDataUtil.getVesselUpdatedEvent(mmsi + 6);
		vesselUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(VESSEL_TOPIC, vesselUpdateEvent.getAggregateId(), vesselUpdateEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía failed y espera un evento de cancelled con el vessel original dentro
		DeleteVesselFailedEvent event = VesselDataUtil.getDeleteVesselFailedEvent(mmsi + 6);

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselDTO> completableFuture = Whitebox.invokeMethod(vesselCommandHandler,
				"getCompletableFeature", event.getSessionId(), vesselUpdateEvent.getVessel());

		kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(20, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.DELETE_CANCELLED, confirm.getType());
		assertEquals(vesselUpdateEvent.getVessel(), ((DeleteVesselCancelledEvent) confirm).getVessel());
	}

	@KafkaHandler
	public void vesselCreatedEvent(VesselCreatedEvent vesselCreatedEvent) {

		blockingQueue.offer(vesselCreatedEvent);
	}

	@KafkaHandler
	public void createVesselCancelledEvent(CreateVesselCancelledEvent createVesselCancelledEvent) {

		blockingQueue.offer(createVesselCancelledEvent);
	}

	@KafkaHandler
	public void vesselUpdatedEvent(VesselUpdatedEvent vesselUpdatedEvent) {

		blockingQueue.offer(vesselUpdatedEvent);
	}

	@KafkaHandler
	public void updateVesselCancelledEvent(UpdateVesselCancelledEvent updateVesselCancelledEvent) {

		blockingQueue.offer(updateVesselCancelledEvent);
	}

	@KafkaHandler
	public void vesselDeletedEvent(VesselDeletedEvent vesselDeletedEvent) {

		blockingQueue.offer(vesselDeletedEvent);
	}

	@KafkaHandler
	public void deleteVesselCancelledEvent(DeleteVesselCancelledEvent deleteVesselCancelledEvent) {

		blockingQueue.offer(deleteVesselCancelledEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
