package es.redmic.test.vesselsview.integration.handler;

/*-
 * #%L
 * Vessels-query-endpoint
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.exception.data.ItemNotFoundException;
import es.redmic.models.es.geojson.wrapper.GeoHitWrapper;
import es.redmic.testutils.documentation.DocumentationViewBaseTest;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingFailedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingFailedEvent;
import es.redmic.vesselslib.utils.VesselTrackingUtil;
import es.redmic.vesselsview.VesselsViewApplication;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.vesselsview.repository.vesseltracking.VesselTrackingESRepository;
import es.redmic.viewlib.config.MapperScanBeanItfc;

@SpringBootTest(classes = { VesselsViewApplication.class })
@RunWith(SpringJUnit4ClassRunner.class)
@KafkaListener(topics = "${broker.topic.vessel-tracking}", groupId = "test")
@TestPropertySource(properties = { "schema.registry.port=18183" })
@DirtiesContext
@ActiveProfiles("test")
public class VesselTrackingEventHandlerTest extends DocumentationViewBaseTest {

	// @formatter:off

	private final String USER_ID = "1",
			PREFIX = "vesseltracking-mmsi-tstamp-",
			MMSI = "1234",
			_UUID = UUID.randomUUID().toString();
	
	// @formatter:on

	Long TSTAMP = new DateTime().getMillis();

	@Autowired
	MapperScanBeanItfc mapper;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Value("${broker.topic.vessel-tracking}")
	private String VESSELTRACKING_TOPIC;

	@Autowired
	VesselTrackingESRepository repository;

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1);

	@PostConstruct
	public void VesselTrackingEventHandlerTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
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
	public void sendVesselTrackingCreatedEvent_SaveItem_IfEventIsOk() throws Exception {

		CreateVesselTrackingEvent event = getCreateVesselTrackingEvent();

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSELTRACKING_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		GeoHitWrapper<?> item = repository.findById(event.getVesselTracking().getId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVesselTracking().getId());

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.CREATE_CONFIRMED, confirm.getType());

		VesselTracking vesselTracking = (VesselTracking) item.get_source();
		assertEqualsVesselTracking(vesselTracking, event.getVesselTracking());
	}

	@Test
	public void sendVesselTrackingCreatedEvent_SaveItem_IfItemIsNotProcessed() throws Exception {

		CreateVesselTrackingEvent event = getCreateVesselTrackingEvent();

		event.getVesselTracking().setUuid(VesselTrackingUtil.UUID_DEFAULT);

		repository.save(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));

		event.getVesselTracking().setUuid(UUID.randomUUID().toString());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSELTRACKING_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		GeoHitWrapper<?> item = repository.findById(event.getVesselTracking().getId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVesselTracking().getId());

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.CREATE_CONFIRMED, confirm.getType());

		VesselTracking vesselTracking = (VesselTracking) item.get_source();
		assertEqualsVesselTracking(vesselTracking, event.getVesselTracking());
	}

	@Test
	public void sendVesselTrackingUpdatedEvent_UpdateItem_IfEventIsOk() throws Exception {

		UpdateVesselTrackingEvent event = getUpdateVesselTrackingEvent();

		repository.save(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSELTRACKING_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		GeoHitWrapper<?> item = repository.findById(event.getVesselTracking().getId());
		assertNotNull(item.get_source());

		// Se restablece el estado de la vista
		repository.delete(event.getVesselTracking().getId());

		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.UPDATE_CONFIRMED.toString(), confirm.getType());

		VesselTracking vesselTracking = (VesselTracking) item.get_source();
		assertEqualsVesselTracking(vesselTracking, event.getVesselTracking());
	}

	@Test(expected = ItemNotFoundException.class)
	public void sendVesselTrackingDeleteEvent_DeleteItem_IfEventIsOk() throws Exception {

		DeleteVesselTrackingEvent event = getDeleteVesselTrackingEvent();

		repository.save(
				mapper.getMapperFacade().map(getUpdateVesselTrackingEvent().getVesselTracking(), VesselTracking.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSELTRACKING_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event confirm = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);
		assertNotNull(confirm);
		assertEquals(VesselTrackingEventTypes.DELETE_CONFIRMED.toString(), confirm.getType());

		GeoHitWrapper<?> item = repository.findById(event.getAggregateId());
		assertNull(item.get_source());
	}

	@Test
	public void sendVesselTrackingCreatedEvent_PublishCreateVesselTrackingFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		CreateVesselTrackingEvent event = getCreateVesselTrackingEvent();

		repository.save(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSELTRACKING_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(event.getVesselTracking().getId());

		assertNotNull(fail);
		assertEquals(VesselTrackingEventTypes.CREATE_FAILED.toString(), fail.getType());
		CreateVesselTrackingFailedEvent createVesselTrackingFailedEvent = (CreateVesselTrackingFailedEvent) fail;

		Map<String, String> arguments = createVesselTrackingFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(4, arguments.size());

		assertNotNull(arguments.get("id"));
		assertNotNull(arguments.get("uuid"));
		assertNotNull(arguments.get("properties.vessel.mmsi"));
		assertNotNull(arguments.get("properties.date"));
	}

	@Test
	public void sendVesselTrackingUpdateEvent_PublishUpdateVesselTrackingFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		UpdateVesselTrackingEvent event = getUpdateVesselTrackingEvent();

		// @formatter:off

		VesselTrackingDTO conflict = event.getVesselTracking(),
				original = getVesselTracking();
		
		conflict.setId(original.getId()+"cpy");
		conflict.setUuid(original.getUuid()+"cpy");
		conflict.getProperties().getVessel().setMmsi(3456);
		
		// @formatter:on

		// Guarda el que se va a modificar
		repository.save(mapper.getMapperFacade().map(original, VesselTracking.class));

		// Guarda el que va a entrar en conflicto
		repository.save(mapper.getMapperFacade().map(conflict, VesselTracking.class));

		// Edita el mmsi del que se va a modificar para entrar en conflicto
		original.getProperties().getVessel().setMmsi(conflict.getProperties().getVessel().getMmsi());
		original.getProperties().setDate(conflict.getProperties().getDate());

		event.setVesselTracking(original);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSELTRACKING_TOPIC,
				event.getAggregateId(), event);
		future.addCallback(new SendListener());

		Event fail = (Event) blockingQueue.poll(50, TimeUnit.SECONDS);

		// Se restablece el estado de la vista
		repository.delete(original.getId());
		repository.delete(conflict.getId());

		assertNotNull(fail);
		assertEquals(VesselTrackingEventTypes.UPDATE_FAILED.toString(), fail.getType());

		UpdateVesselTrackingFailedEvent createVesselTrackingFailedEvent = (UpdateVesselTrackingFailedEvent) fail;

		Map<String, String> arguments = createVesselTrackingFailedEvent.getArguments();
		assertNotNull(arguments);

		assertEquals(arguments.size(), 2);

		assertNotNull(arguments.get("properties.vessel.mmsi"));
		assertNotNull(arguments.get("properties.date"));
	}

	@Test
	public void sendVesselTrackingDeleteEvent_PublishDeleteVesselTrackingFailedEvent_IfNoConstraintsFulfilled()
			throws Exception {

		// TODO: Implementar cuando se metan las referencias en la vista.
		assertTrue(true);
	}

	@KafkaHandler
	public void createVesselTrackingConfirmed(CreateVesselTrackingConfirmedEvent createVesselTrackingConfirmedEvent) {

		blockingQueue.offer(createVesselTrackingConfirmedEvent);
	}

	@KafkaHandler
	public void createVesselTrackingFailed(CreateVesselTrackingFailedEvent createVesselTrackingFailedEvent) {

		blockingQueue.offer(createVesselTrackingFailedEvent);
	}

	@KafkaHandler
	public void updateVesselTrackingConfirmed(UpdateVesselTrackingConfirmedEvent updateVesselTrackingConfirmedEvent) {

		blockingQueue.offer(updateVesselTrackingConfirmedEvent);
	}

	@KafkaHandler
	public void updateVesselTrackingFailed(UpdateVesselTrackingFailedEvent updateVesselTrackingFailedEvent) {

		blockingQueue.offer(updateVesselTrackingFailedEvent);
	}

	@KafkaHandler
	public void deleteVesselTrackingConfirmed(DeleteVesselTrackingConfirmedEvent deleteVesselTrackingConfirmedEvent) {

		blockingQueue.offer(deleteVesselTrackingConfirmedEvent);
	}

	@KafkaHandler
	public void deleteVesselTrackingFailed(DeleteVesselTrackingFailedEvent deleteVesselTrackingFailedEvent) {

		blockingQueue.offer(deleteVesselTrackingFailedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {
		System.out.println("Entrando por default " + def);
	}

	protected VesselTrackingDTO getVesselTracking() {

		VesselTrackingDTO vesselTracking = new VesselTrackingDTO();

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

		vesselTracking.setId(PREFIX + MMSI + TSTAMP);
		vesselTracking.setUuid(_UUID);

		GeometryFactory geometryFactory = new GeometryFactory();

		Point geometry = geometryFactory.createPoint(new Coordinate(44.56433, 37.94388));
		vesselTracking.setGeometry(geometry);

		VesselTrackingPropertiesDTO properties = new VesselTrackingPropertiesDTO();
		vesselTracking.setProperties(properties);

		properties.setActivity("r.1.8.22");
		properties.setVessel(vessel);
		properties.setDate(DateTime.now());

		properties.setCog(23.3);
		properties.setSog(23.3);
		properties.setHeading(12);
		properties.setNavStat(33);
		properties.setDest("Santa Cruz de Tenerife");
		properties.setEta("00:00 00:00");
		properties.setQFlag("0");
		properties.setVFlag("N");

		return vesselTracking;
	}

	protected CreateVesselTrackingEvent getCreateVesselTrackingEvent() {

		CreateVesselTrackingEvent createdEvent = new CreateVesselTrackingEvent();
		createdEvent.setId(UUID.randomUUID().toString());
		createdEvent.setDate(DateTime.now());
		createdEvent.setType(VesselTrackingEventTypes.CREATE);
		createdEvent.setVesselTracking(getVesselTracking());
		createdEvent.setAggregateId(createdEvent.getVesselTracking().getId());
		createdEvent.setVersion(1);
		createdEvent.setSessionId(UUID.randomUUID().toString());
		createdEvent.setUserId(USER_ID);
		return createdEvent;
	}

	protected UpdateVesselTrackingEvent getUpdateVesselTrackingEvent() {

		UpdateVesselTrackingEvent updatedEvent = new UpdateVesselTrackingEvent();
		updatedEvent.setId(UUID.randomUUID().toString());
		updatedEvent.setDate(DateTime.now());
		updatedEvent.setType(VesselTrackingEventTypes.UPDATE);
		VesselTrackingDTO vesselTracking = getVesselTracking();
		vesselTracking.getProperties().setDest(vesselTracking.getProperties().getDest() + "2");
		updatedEvent.setVesselTracking(vesselTracking);
		updatedEvent.setAggregateId(updatedEvent.getVesselTracking().getId());
		updatedEvent.setVersion(2);
		updatedEvent.setSessionId(UUID.randomUUID().toString());
		updatedEvent.setUserId(USER_ID);
		return updatedEvent;
	}

	protected DeleteVesselTrackingEvent getDeleteVesselTrackingEvent() {

		DeleteVesselTrackingEvent deletedEvent = new DeleteVesselTrackingEvent();
		deletedEvent.setId(UUID.randomUUID().toString());
		deletedEvent.setDate(DateTime.now());
		deletedEvent.setType(VesselTrackingEventTypes.DELETE);
		deletedEvent.setAggregateId(getVesselTracking().getId());
		deletedEvent.setVersion(3);
		deletedEvent.setSessionId(UUID.randomUUID().toString());
		deletedEvent.setUserId(USER_ID);
		return deletedEvent;
	}

	private void assertEqualsVesselTracking(VesselTracking vesselTracking, VesselTrackingDTO vesselTrackingDTO)
			throws JsonProcessingException, JSONException {

		JSONAssert.assertEquals(objectMapper.writeValueAsString(vesselTracking),
				objectMapper.writeValueAsString(vesselTrackingDTO), false);
	}
}
