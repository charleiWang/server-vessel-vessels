package es.redmic.test.vesselsview.integration.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.junit.Before;
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

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.data.common.model.DataHitWrapper;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselFailedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselFailedEvent;
import es.redmic.vesselsview.VesselsViewApplication;
import es.redmic.vesselsview.model.Vessel;
import es.redmic.vesselsview.repository.VesselESRepository;
import es.redmic.viewlib.config.MapperScanBeanItfc;

@SpringBootTest(classes = { VesselsViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "test")
@TestPropertySource(properties = { "schema.registry.port=18083" })
@DirtiesContext
@ActiveProfiles("test")
public class VesselEventHandlerTest extends DocumentationViewBaseTest {

	private final String USER_ID = "1";

	@Autowired
	MapperScanBeanItfc mapper;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@Autowired
	VesselESRepository repository;

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1);

	@PostConstruct
	public void CreateVesselFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Override
	@Before
	public void setUp() {
	}

	@Test
	public void sendVesselCreatedEvent_SaveItem_IfEventIsOk() throws Exception {

		CreateVesselEvent event = getCreateVesselEvent();

		repository.delete(event.getVessel().getId());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVessel().getId());

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.CREATE_CONFIRMED, confirm.getType());

		Vessel vessel = (Vessel) item.get_source();
		assertEquals(vessel.getId(), event.getAggregateId());
		assertEquals(vessel.getMmsi(), event.getVessel().getMmsi());
		assertEquals(vessel.getName(), event.getVessel().getName());
	}

	@Test
	public void sendVesselUpdatedEvent_UpdateItem_IfEventIsOk() throws Exception {

		UpdateVesselEvent event = getUpdateVesselEvent();

		repository.save(mapper.getMapperFacade().map(event.getVessel(), Vessel.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVessel().getId());

		assertNotNull(confirm);
		assertEquals(VesselEventTypes.UPDATE_CONFIRMED.toString(), confirm.getType());

		Vessel vessel = (Vessel) item.get_source();
		assertEquals(vessel.getId(), event.getAggregateId());
		assertEquals(vessel.getMmsi(), event.getVessel().getMmsi());
		assertEquals(vessel.getName(), event.getVessel().getName());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendVesselDeleteEvent_DeleteItem_IfEventIsOk() throws Exception {

		DeleteVesselEvent event = getDeleteVesselEvent();

		repository.save(mapper.getMapperFacade().map(getUpdateVesselEvent().getVessel(), Vessel.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(VesselEventTypes.DELETE_CONFIRMED.toString(), confirm.getType());

		DataHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNull(item.get_source());
	}

	@Test
	public void sendVesselCreatedEvent_PublishCreateVesselFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		CreateVesselEvent event = getCreateVesselEvent();

		repository.save(mapper.getMapperFacade().map(event.getVessel(), Vessel.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getVessel().getId());

		assertNotNull(fail);
		assertEquals(VesselEventTypes.CREATE_FAILED.toString(), fail.getType());
		CreateVesselFailedEvent createVesselFailedEvent = (CreateVesselFailedEvent) fail;

		Map<String, String> arguments = createVesselFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 3);

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("mmsi"));
		assertNotNull(arguments.get("imo"));
	}

	@Test
	public void sendVesselUpdateEvent_PublishUpdateVesselFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		UpdateVesselEvent event = getUpdateVesselEvent();

		// @formatter:off

		VesselDTO conflict = getVessel(),
				original = event.getVessel();
		
		conflict.setId(original.getId()+"cpy");
		conflict.setMmsi(3456);
		conflict.setImo(3456);
		
		// @formatter:on

		// Guarda el que se va a modificar
		repository.save(mapper.getMapperFacade().map(original, Vessel.class));

		// Guarda el que va a entrar en conflicto
		repository.save(mapper.getMapperFacade().map(conflict, Vessel.class));

		// Edita el mmsi del que se va a modificar para entrar en conflicto
		original.setMmsi(conflict.getMmsi());
		// Edita el imo del que se va a modificar para entrar en conflicto
		original.setImo(conflict.getImo());
		event.setVessel(original);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC, event.getAggregateId(),
				event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(original.getId());
		repository.delete(conflict.getId());

		assertNotNull(fail);
		assertEquals(VesselEventTypes.UPDATE_FAILED.toString(), fail.getType());

		UpdateVesselFailedEvent createVesselFailedEvent = (UpdateVesselFailedEvent) fail;

		Map<String, String> arguments = createVesselFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 2);

		assertNotNull(arguments.get("mmsi"));
		assertNotNull(arguments.get("imo"));
	}

	@Test
	public void sendVesselDeleteEvent_PublishDeleteVesselFailedEvent_IfNoConstraintsFulfilled() throws Exception {

		// TODO: Implementar cuando se metan las referencias en la vista.
		assertTrue(true);
	}

	@KafkaHandler
	public void createVesselConfirmed(CreateVesselConfirmedEvent createVesselConfirmedEvent) {

		blockingQueue.offer(createVesselConfirmedEvent);
	}

	@KafkaHandler
	public void createVesselFailed(CreateVesselFailedEvent createVesselFailedEvent) {

		blockingQueue.offer(createVesselFailedEvent);
	}

	@KafkaHandler
	public void updateVesselConfirmed(UpdateVesselConfirmedEvent updateVesselConfirmedEvent) {

		blockingQueue.offer(updateVesselConfirmedEvent);
	}

	@KafkaHandler
	public void updateVesselFailed(UpdateVesselFailedEvent updateVesselFailedEvent) {

		blockingQueue.offer(updateVesselFailedEvent);
	}

	@KafkaHandler
	public void deleteVesselConfirmed(DeleteVesselConfirmedEvent deleteVesselConfirmedEvent) {

		blockingQueue.offer(deleteVesselConfirmedEvent);
	}

	@KafkaHandler
	public void deleteVesselFailed(DeleteVesselFailedEvent deleteVesselFailedEvent) {

		blockingQueue.offer(deleteVesselFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {
		System.out.println("Entrando por default " + def);
	}

	protected VesselDTO getVessel() {

		VesselDTO vessel = new VesselDTO();
		vessel.setId("ww1effbf-700f-4dfb-8aba-8212ac2d26b3");
		vessel.setMmsi(1234);
		vessel.setName("Avatar");
		vessel.setImo(1234);
		vessel.setCallSign("PACH");

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setCode("70");
		vesselType.setId("1234");
		vesselType.setName("Cargo, all ships of this type");
		vesselType.setName_en("Cargo, all ships of this type");
		vessel.setType(vesselType);

		return vessel;
	}

	protected CreateVesselEvent getCreateVesselEvent() {

		CreateVesselEvent createdEvent = new CreateVesselEvent();
		createdEvent.setId(UUID.randomUUID().toString());
		createdEvent.setDate(DateTime.now());
		createdEvent.setType(VesselEventTypes.CREATE);
		createdEvent.setVessel(getVessel());
		createdEvent.setAggregateId(createdEvent.getVessel().getId());
		createdEvent.setVersion(1);
		createdEvent.setSessionId(UUID.randomUUID().toString());
		createdEvent.setUserId(USER_ID);
		return createdEvent;
	}

	protected UpdateVesselEvent getUpdateVesselEvent() {

		UpdateVesselEvent updatedEvent = new UpdateVesselEvent();
		updatedEvent.setId(UUID.randomUUID().toString());
		updatedEvent.setDate(DateTime.now());
		updatedEvent.setType(VesselEventTypes.UPDATE);
		VesselDTO vessel = getVessel();
		vessel.setName(vessel.getName() + "2");
		updatedEvent.setVessel(vessel);
		updatedEvent.setAggregateId(updatedEvent.getVessel().getId());
		updatedEvent.setVersion(2);
		updatedEvent.setSessionId(UUID.randomUUID().toString());
		updatedEvent.setUserId(USER_ID);
		return updatedEvent;
	}

	protected DeleteVesselEvent getDeleteVesselEvent() {

		DeleteVesselEvent deletedEvent = new DeleteVesselEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(VesselEventTypes.DELETE);
		deletedEvent.setAggregateId(getVessel().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		return deletedEvent;
	}
}