package es.redmic.test.vesselsview.integration.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.test.vesselsview.integration.common.CommonIntegrationTest;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeFailedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeFailedEvent;
import es.redmic.vesselsview.VesselsViewApplication;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.vesselsview.repository.VesselTypeESRepository;
import es.redmic.viewlib.config.MapperScanBeanItfc;

@SpringBootTest(classes = { VesselsViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.vessel-type}", groupId = "test")
public class VesselTypeEventHandlerTest extends CommonIntegrationTest {

	private final String USER_ID = "1";

	@Autowired
	MapperScanBeanItfc mapper;

	@Autowired
	VesselTypeESRepository repository;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.vessel-type}")
	private String VESSEL_TYPE_TOPIC;

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Test
	public void sendVesselTypeCreatedEvent_SaveItem_IfEventIsOk() throws Exception {

		CreateVesselTypeEvent event = getCreateVesselTypeEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVesselType().getId());

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.CREATE_VESSELTYPE_CONFIRMED.toString(), confirm.getType());

		VesselType vesselType = (VesselType) item.get_source();
		assertEquals(vesselType.getId(), event.getAggregateId());
		assertEquals(vesselType.getCode(), event.getVesselType().getCode());
		assertEquals(vesselType.getName(), event.getVesselType().getName());
	}

	@Test
	public void sendVesselTypeUpdatedEvent_callUpdate_IfEventIsOk() throws Exception {

		UpdateVesselTypeEvent event = getUpdateVesselTypeEvent();

		repository.save(mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVesselType().getId());

		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.UPDATE_VESSELTYPE_CONFIRMED.toString(), confirm.getType());

		VesselType vesselType = (VesselType) item.get_source();
		assertEquals(vesselType.getId(), event.getAggregateId());
		assertEquals(vesselType.getCode(), event.getVesselType().getCode());
		assertEquals(vesselType.getName(), event.getVesselType().getName());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendVesselTypeDeleteEvent_callDelete_IfEventIsOk() throws Exception {

		DeleteVesselTypeEvent event = getDeleteVesselEvent();

		repository.save(mapper.getMapperFacade().map(getUpdateVesselTypeEvent().getVesselType(), VesselType.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(VesselTypeEventType.DELETE_VESSELTYPE_CONFIRMED.toString(), confirm.getType());

		repository.findById(event.getAggregateId());
	}

	@Test
	public void sendVesselTypeCreatedEvent_PublishCreateVesselTypeFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		CreateVesselTypeEvent event = getCreateVesselTypeEvent();

		repository.save(mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getVesselType().getId());

		assertNotNull(fail);
		assertEquals(VesselTypeEventType.CREATE_VESSELTYPE_FAILED.toString(), fail.getType());

		CreateVesselTypeFailedEvent createVesselFailedEvent = (CreateVesselTypeFailedEvent) fail;

		Map<String, String> arguments = createVesselFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(2, arguments.size());

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("code"));

	}

	@Test
	public void sendVesselTypeUpdateEvent_PublishUpdateVesselTypeFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		UpdateVesselTypeEvent event = getUpdateVesselTypeEvent();

		// @formatter:off
		VesselTypeDTO conflict = getVesselType(),
				original = event.getVesselType();
		// @formatter:on
		conflict.setId(original.getId() + "cpy");
		conflict.setCode("171");

		// Guarda el que se va a modificar
		repository.save(mapper.getMapperFacade().map(original, VesselType.class));

		// Guarda el que va a entrar en conflicto
		repository.save(mapper.getMapperFacade().map(conflict, VesselType.class));

		// Edita el mmsi del que se va a modificar para entrar en conflicto
		original.setCode(conflict.getCode());
		event.setVesselType(original);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(original.getId());
		repository.delete(conflict.getId());

		assertNotNull(fail);
		assertEquals(VesselTypeEventType.UPDATE_VESSELTYPE_FAILED.toString(), fail.getType());

		UpdateVesselTypeFailedEvent createVesselFailedEvent = (UpdateVesselTypeFailedEvent) fail;

		Map<String, String> arguments = createVesselFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 1);

		assertNotNull(arguments.get("code"));
	}

	@Test
	public void sendVesselTypeDeleteEvent_PublishDeleteVesselTypeFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		// TODO: Implementar cuando se metan las referencias en la vista.
		assertTrue(true);
	}

	@KafkaHandler
	public void createTypeVesselConfirmed(CreateVesselTypeConfirmedEvent createVesselTypeConfirmedEvent) {

		blockingQueue.offer(createVesselTypeConfirmedEvent);
	}

	@KafkaHandler
	public void createVesselTypeFailed(CreateVesselTypeFailedEvent createVesselTypeFailedEvent) {

		blockingQueue.offer(createVesselTypeFailedEvent);
	}

	@KafkaHandler
	public void updateVesselTypeConfirmed(UpdateVesselTypeConfirmedEvent updateVesselTypeConfirmedEvent) {

		blockingQueue.offer(updateVesselTypeConfirmedEvent);
	}

	@KafkaHandler
	public void updateVesselTypeFailed(UpdateVesselTypeFailedEvent updateVesselTypeFailedEvent) {

		blockingQueue.offer(updateVesselTypeFailedEvent);
	}

	@KafkaHandler
	public void deleteVesselTypeConfirmed(DeleteVesselTypeConfirmedEvent deleteVesselTypeConfirmedEvent) {

		blockingQueue.offer(deleteVesselTypeConfirmedEvent);
	}

	@KafkaHandler
	public void deleteVesselTypeFailed(DeleteVesselTypeFailedEvent deleteVesselTypeFailedEvent) {

		blockingQueue.offer(deleteVesselTypeFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}

	protected VesselTypeDTO getVesselType() {

		VesselTypeDTO vessel = new VesselTypeDTO();
		vessel.setId("009918f0-1dd8-4ca3-b241-d0fdf65766ea");
		vessel.setCode("170");
		vessel.setName("Cargo, all ships of this type");
		vessel.setName_en("Cargo, all ships of this type");
		return vessel;
	}

	protected CreateVesselTypeEvent getCreateVesselTypeEvent() {

		CreateVesselTypeEvent createdEvent = new CreateVesselTypeEvent();
		createdEvent.setId(UUID.randomUUID().toString());
		createdEvent.setDate(DateTime.now());
		createdEvent.setType(VesselTypeEventType.CREATE_VESSELTYPE.name());
		createdEvent.setVesselType(getVesselType());
		createdEvent.setAggregateId(createdEvent.getVesselType().getId());
		createdEvent.setVersion(1);
		createdEvent.setSessionId(UUID.randomUUID().toString());
		createdEvent.setUserId(USER_ID);
		return createdEvent;
	}

	protected UpdateVesselTypeEvent getUpdateVesselTypeEvent() {

		UpdateVesselTypeEvent updatedEvent = new UpdateVesselTypeEvent();
		updatedEvent.setId(UUID.randomUUID().toString());
		updatedEvent.setDate(DateTime.now());
		updatedEvent.setType(VesselTypeEventType.UPDATE_VESSELTYPE.name());
		VesselTypeDTO vessel = getVesselType();
		vessel.setName(vessel.getName() + "2");
		updatedEvent.setVesselType(vessel);
		updatedEvent.setAggregateId(updatedEvent.getVesselType().getId());
		updatedEvent.setVersion(2);
		updatedEvent.setSessionId(UUID.randomUUID().toString());
		updatedEvent.setUserId(USER_ID);
		return updatedEvent;
	}

	protected DeleteVesselTypeEvent getDeleteVesselEvent() {

		DeleteVesselTypeEvent deletedEvent = new DeleteVesselTypeEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(VesselTypeEventType.DELETE_VESSELTYPE.name());
		deletedEvent.setAggregateId(getVesselType().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		return deletedEvent;
	}
}