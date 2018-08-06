package es.redmic.test.vesselscommands.integration.vesseltype;

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
import es.redmic.vesselscommands.commands.VesselTypeCommandHandler;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.create.VesselTypeCreatedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.VesselTypeDeletedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeCancelledEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.VesselTypeUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@KafkaListener(topics = "${broker.topic.vessel-type}", groupId = "${random.value}")
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=VesselTypeCommandHandlerTest" })
public class VesselTypeCommandHandlerTest extends KafkaBaseIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	// number of brokers.
	private final static Integer numBrokers = 3;
	// partitions per topic.
	private final static Integer partitionsPerTopic = 3;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(numBrokers, true, partitionsPerTopic);

	private static final String code = "1234";

	@Value("${broker.topic.vessel-type}")
	private String vessel_type_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	VesselTypeCommandHandler vesselTypeCommandHandler;

	@PostConstruct
	public void VesselTypeCommandHandlerTestPostConstruct() throws Exception {

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
	public void createVesselTypeConfirmedEvent_SendVesselTypeCreatedEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createVesselTypeConfirmedEvent");

		// Envía create para meterlo en el stream
		CreateVesselTypeEvent createVesselTypeEvent = VesselTypeDataUtil.getCreateEvent(code + "1");
		kafkaTemplate.send(vessel_type_topic, createVesselTypeEvent.getAggregateId(), createVesselTypeEvent);

		// Envía confirmed y espera un evento de created con el vessel original dentro
		CreateVesselTypeConfirmedEvent event = VesselTypeDataUtil.getCreateVesselTypeConfirmedEvent(code + "1");

		kafkaTemplate.send(vessel_type_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(120, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.VESSELTYPE_CREATED.toString(), confirm.getType());

		assertEquals(createVesselTypeEvent.getVesselType(), ((VesselTypeCreatedEvent) confirm).getVesselType());
	}

	// Envía un evento de confirmación de modificación y debe provocar un evento
	// Updated con el item dentro
	@Test
	public void updateVesselTypeConfirmedEvent_SendVesselTypeUpdatedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> updateVesselTypeConfirmedEvent");

		// Envía update para meterlo en el stream
		UpdateVesselTypeEvent updateVesselTypeEvent = VesselTypeDataUtil.getUpdateEvent(code + "2");
		kafkaTemplate.send(vessel_type_topic, updateVesselTypeEvent.getAggregateId(), updateVesselTypeEvent);

		// Envía confirmed y espera un evento de updated con el vessel original dentro
		UpdateVesselTypeConfirmedEvent event = VesselTypeDataUtil.getUpdateVesselTypeConfirmedEvent(code + "2");
		kafkaTemplate.send(vessel_type_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.VESSELTYPE_UPDATED.toString(), confirm.getType());
		assertEquals(updateVesselTypeEvent.getVesselType(), ((VesselTypeUpdatedEvent) confirm).getVesselType());
	}

	// Envía un evento de confirmación de borrado y debe provocar un evento Deleted
	@Test
	public void deleteVesselTypeConfirmedEvent_SendVesselTypeDeletedEvent_IfReceivesSuccess()
			throws InterruptedException {

		logger.debug("----> deleteVesselTypeConfirmedEvent");

		DeleteVesselTypeConfirmedEvent event = VesselTypeDataUtil.getDeleteVesselTypeConfirmedEvent(code + "3");

		kafkaTemplate.send(vessel_type_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(60, TimeUnit.SECONDS);

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.VESSELTYPE_DELETED.toString(), confirm.getType());
		assertEquals(event.getAggregateId(), confirm.getAggregateId());
		assertEquals(event.getUserId(), confirm.getUserId());
		assertEquals(event.getSessionId(), confirm.getSessionId());
		assertEquals(event.getVersion(), confirm.getVersion());
	}

	// Fail cases

	// Envía un evento de error de creación y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = ItemAlreadyExistException.class)
	public void createVesselTypeFailedEvent_SendVesselTypeCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> createVesselTypeFailedEvent");

		CreateVesselTypeFailedEvent event = VesselTypeDataUtil.getCreateVesselTypeFailedEvent(code + "4");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselTypeDTO> completableFuture = Whitebox.invokeMethod(vesselTypeCommandHandler,
				"getCompletableFeature", event.getSessionId(), VesselTypeDataUtil.getVesselType(code + "4"));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(vessel_type_topic,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(40, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselTypeCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.CREATE_VESSELTYPE_CANCELLED.toString(), confirm.getType());
	}

	// Envía un evento de error de modificación y debe provocar un evento Cancelled
	// con el item dentro
	@Test(expected = ItemNotFoundException.class)
	public void updateVesselTypeFailedEvent_SendVesselTypeCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> updateVesselTypeFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		VesselTypeCreatedEvent vesselTypeCreatedEvent = VesselTypeDataUtil.getVesselTypeCreatedEvent(code + "5");
		vesselTypeCreatedEvent.getVesselType().setName("Nombre erroneo al crearlo");
		vesselTypeCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(vessel_type_topic, vesselTypeCreatedEvent.getAggregateId(), vesselTypeCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		VesselTypeUpdatedEvent vesselTypeUpdateEvent = VesselTypeDataUtil.getVesselTypeUpdatedEvent(code + "5");
		vesselTypeUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(vessel_type_topic, vesselTypeUpdateEvent.getAggregateId(), vesselTypeUpdateEvent);
		blockingQueue.poll(20, TimeUnit.SECONDS);

		// Envía failed y espera un evento de cancelled con el vessel original dentro
		UpdateVesselTypeFailedEvent event = VesselTypeDataUtil.getUpdateVesselTypeFailedEvent(code + "5");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselTypeDTO> completableFuture = Whitebox.invokeMethod(vesselTypeCommandHandler,
				"getCompletableFeature", event.getSessionId(), VesselTypeDataUtil.getVesselType(code + "5"));

		kafkaTemplate.send(vessel_type_topic, event.getAggregateId(), event);
		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselTypeCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.UPDATE_VESSELTYPE_CANCELLED.toString(), confirm.getType());
		assertEquals(vesselTypeUpdateEvent.getVesselType(), ((UpdateVesselTypeCancelledEvent) confirm).getVesselType());
	}

	// Envía un evento de error de borrado y debe provocar un evento Cancelled con
	// el item dentro
	@Test(expected = DeleteItemException.class)
	public void deleteVesselTypeFailedEvent_SendVesselTypeCancelledEvent_IfReceivesSuccess() throws Exception {

		logger.debug("----> deleteVesselTypeFailedEvent");

		// Envía created para meterlo en el stream y lo saca de la cola
		VesselTypeCreatedEvent vesselTypeCreatedEvent = VesselTypeDataUtil.getVesselTypeCreatedEvent(code + "6");
		vesselTypeCreatedEvent.getVesselType().setName("Nombre erroneo al crearlo");
		vesselTypeCreatedEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(vessel_type_topic, vesselTypeCreatedEvent.getAggregateId(), vesselTypeCreatedEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía updated para meterlo en el stream y lo saca de la cola
		VesselTypeUpdatedEvent vesselTypeUpdateEvent = VesselTypeDataUtil.getVesselTypeUpdatedEvent(code + "6");
		vesselTypeUpdateEvent.setSessionId(UUID.randomUUID().toString());
		kafkaTemplate.send(vessel_type_topic, vesselTypeUpdateEvent.getAggregateId(), vesselTypeUpdateEvent);
		blockingQueue.poll(10, TimeUnit.SECONDS);

		// Envía failed y espera un evento de cancelled con el vessel original dentro
		DeleteVesselTypeFailedEvent event = VesselTypeDataUtil.getDeleteVesselTypeFailedEvent(code + "6");

		// Añade completableFeature para que se resuelva al recibir el mensaje.
		CompletableFuture<VesselTypeDTO> completableFuture = Whitebox.invokeMethod(vesselTypeCommandHandler,
				"getCompletableFeature", event.getSessionId(), vesselTypeUpdateEvent.getVesselType());

		kafkaTemplate.send(vessel_type_topic, event.getAggregateId(), event);

		Event confirm = (Event) blockingQueue.poll(30, TimeUnit.SECONDS);

		// Obtiene el resultado
		Whitebox.invokeMethod(vesselTypeCommandHandler, "getResult", event.getSessionId(), completableFuture);

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.DELETE_VESSELTYPE_CANCELLED.toString(), confirm.getType());
		assertEquals(vesselTypeUpdateEvent.getVesselType(), ((DeleteVesselTypeCancelledEvent) confirm).getVesselType());
	}

	@KafkaHandler
	public void vesselTypeCreatedEvent(VesselTypeCreatedEvent vesselTypeCreatedEvent) {

		blockingQueue.offer(vesselTypeCreatedEvent);
	}

	@KafkaHandler
	public void createVesselTypeCancelledEvent(CreateVesselTypeCancelledEvent createVesselTypeCancelledEvent) {

		blockingQueue.offer(createVesselTypeCancelledEvent);
	}

	@KafkaHandler
	public void vesselTypeUpdatedEvent(VesselTypeUpdatedEvent vesselTypeUpdatedEvent) {

		blockingQueue.offer(vesselTypeUpdatedEvent);
	}

	@KafkaHandler
	public void updateVesselTypeCancelledEvent(UpdateVesselTypeCancelledEvent updateVesselTypeCancelledEvent) {

		blockingQueue.offer(updateVesselTypeCancelledEvent);
	}

	@KafkaHandler
	public void vesselTypeDeletedEvent(VesselTypeDeletedEvent vesselTypeDeletedEvent) {

		blockingQueue.offer(vesselTypeDeletedEvent);
	}

	@KafkaHandler
	public void deleteVesselTypeCancelledEvent(DeleteVesselTypeCancelledEvent deleteVesselTypeCancelledEvent) {

		blockingQueue.offer(deleteVesselTypeCancelledEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
