package es.redmic.test.vesselscommands.integration.vesseltracking;

/*-
 * #%L
 * Vessels-management
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
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
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.documentation.DocumentationCommandBaseTest;
import es.redmic.testutils.utils.JsonToBeanTestUtil;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.handler.VesselTrackingCommandHandler;
import es.redmic.vesselscommands.statestore.VesselTrackingStateStore;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.EnrichCreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.create.VesselTrackingCreatedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.VesselTrackingDeletedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.EnrichUpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingConfirmedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEnrichedEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.VesselTrackingUpdatedEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselTrackingFromRest",
		"schema.registry.port=18181" })
@KafkaListener(topics = "${broker.topic.vessel-tracking}", groupId = "CreateVesselTrackingFromRestTest")
public class CreateVesselTrackingFromRestTest extends DocumentationCommandBaseTest {

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private final Integer mmsi = 4444;

	// @formatter:off
	
		private final String
		        ACTIVITY_ID = "999",
				HOST = "redmic.es/api/vessels/commands",
				VESSELTRACKING_PATH = "/activities/" + ACTIVITY_ID + "/vesseltracking";
		
		// @formatter:on

	VesselTrackingStateStore vesselTrackingStateStore;

	@Autowired
	VesselTrackingCommandHandler vesselTrackingCommandHandler;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Value("${broker.topic.vessel-tracking}")
	private String VESSEL_TRACKING_TOPIC;

	@PostConstruct
	public void CreateVesselTrackingFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getEmbeddedKafka().getZookeeperConnectionString(),
				embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		vesselTrackingStateStore = Mockito.mock(VesselTrackingStateStore.class);

		Whitebox.setInternalState(vesselTrackingCommandHandler, "vesselTrackingStateStore", vesselTrackingStateStore);

		// @formatter:off

		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilters(springSecurityFilterChain)
				.apply(documentationConfiguration(this.restDocumentation)
						.uris().withScheme(SCHEME).withHost(HOST).withPort(PORT))
				.alwaysDo(this.document).build();

		// @formatter:on
	}

	@Test
	public void createVesselTracking_SendCreateVesselTrackingEvent_IfCommandWasSuccess() throws Exception {

		String tstamp = String.valueOf(new DateTime().getMillis());

		VesselTrackingDTO vesselTrackingDTO = VesselTrackingDataUtil.getVesselTracking(mmsi, tstamp);
		vesselTrackingDTO.getProperties().setActivity(ACTIVITY_ID);

		String id = vesselTrackingDTO.getId();

		// @formatter:off
		this.mockMvc
				.perform(post(VESSELTRACKING_PATH)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(vesselTrackingDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.uuid", is(vesselTrackingDTO.getUuid())))
				.andExpect(jsonPath("$.body.geometry", notNullValue()))
				.andExpect(jsonPath("$.body.properties", notNullValue()))
				.andExpect(jsonPath("$.body.properties.activity", is(vesselTrackingDTO.getProperties().getActivity())))
				.andExpect(jsonPath("$.body.properties.date", 
						is(vesselTrackingDTO.getProperties().getDate().toString("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))))
				.andExpect(jsonPath("$.body.properties.vessel.id", is(vesselTrackingDTO.getProperties().getVessel().getId())))
				.andExpect(jsonPath("$.body.properties.vessel.mmsi", is(vesselTrackingDTO.getProperties().getVessel().getMmsi())))
				.andExpect(jsonPath("$.body.properties.vessel.imo", is(vesselTrackingDTO.getProperties().getVessel().getImo())))
				.andExpect(jsonPath("$.body.properties.vessel.name", is(vesselTrackingDTO.getProperties().getVessel().getName())))
				.andExpect(jsonPath("$.body.properties.vessel.callSign", is(vesselTrackingDTO.getProperties().getVessel().getCallSign())))
				.andExpect(jsonPath("$.body.properties.vessel.beam", is(vesselTrackingDTO.getProperties().getVessel().getBeam())))
				.andExpect(jsonPath("$.body.properties.vessel.length", is(vesselTrackingDTO.getProperties().getVessel().getLength())))
				.andExpect(jsonPath("$.body.properties.vessel.type.id", is(vesselTrackingDTO.getProperties().getVessel().getType().getId())))
				.andExpect(jsonPath("$.body.properties.vessel.type.code", is(vesselTrackingDTO.getProperties().getVessel().getType().getCode())))
				.andExpect(jsonPath("$.body.properties.vessel.type.name", is(vesselTrackingDTO.getProperties().getVessel().getType().getName())))
				.andExpect(jsonPath("$.body.properties.vessel.type.name_en", is(vesselTrackingDTO.getProperties().getVessel().getType().getName_en())))
				.andExpect(jsonPath("$.body.properties.cog", is(vesselTrackingDTO.getProperties().getCog())))
				.andExpect(jsonPath("$.body.properties.dest", is(vesselTrackingDTO.getProperties().getDest())))
				.andExpect(jsonPath("$.body.properties.eta", is(vesselTrackingDTO.getProperties().getEta())))
				.andExpect(jsonPath("$.body.properties.heading", is(vesselTrackingDTO.getProperties().getHeading())))
				.andExpect(jsonPath("$.body.properties.navStat", is(vesselTrackingDTO.getProperties().getNavStat())))
				.andExpect(jsonPath("$.body.properties.sog", is(vesselTrackingDTO.getProperties().getSog())));
		
		// @formatter:on

		VesselTrackingCreatedEvent event = (VesselTrackingCreatedEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		vesselTrackingDTO.getProperties().setInserted(event.getVesselTracking().getProperties().getInserted());
		vesselTrackingDTO.getProperties().setUpdated(event.getVesselTracking().getProperties().getUpdated());

		assertNotNull(event);
		assertEquals(vesselTrackingDTO.getId(), event.getVesselTracking().getId());
		assertEquals(vesselTrackingDTO.getGeometry(), event.getVesselTracking().getGeometry());
		assertEquals(vesselTrackingDTO.getProperties(), event.getVesselTracking().getProperties());

		assertEquals(VesselTrackingEventTypes.CREATED, event.getType());
		assertEquals("1", event.getVersion().toString());
		assertEquals(vesselTrackingDTO.getId(), event.getAggregateId());
	}

	@Test
	public void updateVesselTracking_SendUpdateVesselTrackingEvent_IfCommandWasSuccess() throws Exception {

		String tstamp = String.valueOf(new DateTime().getMillis());

		VesselTrackingDTO vesselTrackingDTO = VesselTrackingDataUtil.getVesselTracking(mmsi, tstamp);
		vesselTrackingDTO.getProperties().setActivity(ACTIVITY_ID);

		String id = vesselTrackingDTO.getId();

		when(vesselTrackingStateStore.getVesselTracking(id))
				.thenReturn(VesselTrackingDataUtil.getVesselTrackingCreatedEvent(mmsi, tstamp));

		// @formatter:off
		
		this.mockMvc
				.perform(put(VESSELTRACKING_PATH + "/"+ id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(vesselTrackingDTO))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.uuid", is(vesselTrackingDTO.getUuid())))
				.andExpect(jsonPath("$.body.geometry", notNullValue()))
				.andExpect(jsonPath("$.body.properties", notNullValue()))
				.andExpect(jsonPath("$.body.properties.activity", is(vesselTrackingDTO.getProperties().getActivity())))
				.andExpect(jsonPath("$.body.properties.date", 
						is(vesselTrackingDTO.getProperties().getDate().toString("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"))))
				.andExpect(jsonPath("$.body.properties.vessel.id", is(vesselTrackingDTO.getProperties().getVessel().getId())))
				.andExpect(jsonPath("$.body.properties.vessel.mmsi", is(vesselTrackingDTO.getProperties().getVessel().getMmsi())))
				.andExpect(jsonPath("$.body.properties.vessel.imo", is(vesselTrackingDTO.getProperties().getVessel().getImo())))
				.andExpect(jsonPath("$.body.properties.vessel.name", is(vesselTrackingDTO.getProperties().getVessel().getName())))
				.andExpect(jsonPath("$.body.properties.vessel.callSign", is(vesselTrackingDTO.getProperties().getVessel().getCallSign())))
				.andExpect(jsonPath("$.body.properties.vessel.beam", is(vesselTrackingDTO.getProperties().getVessel().getBeam())))
				.andExpect(jsonPath("$.body.properties.vessel.length", is(vesselTrackingDTO.getProperties().getVessel().getLength())))
				.andExpect(jsonPath("$.body.properties.vessel.type.id", is(vesselTrackingDTO.getProperties().getVessel().getType().getId())))
				.andExpect(jsonPath("$.body.properties.vessel.type.code", is(vesselTrackingDTO.getProperties().getVessel().getType().getCode())))
				.andExpect(jsonPath("$.body.properties.vessel.type.name", is(vesselTrackingDTO.getProperties().getVessel().getType().getName())))
				.andExpect(jsonPath("$.body.properties.vessel.type.name_en", is(vesselTrackingDTO.getProperties().getVessel().getType().getName_en())))
				.andExpect(jsonPath("$.body.properties.cog", is(vesselTrackingDTO.getProperties().getCog())))
				.andExpect(jsonPath("$.body.properties.dest", is(vesselTrackingDTO.getProperties().getDest())))
				.andExpect(jsonPath("$.body.properties.eta", is(vesselTrackingDTO.getProperties().getEta())))
				.andExpect(jsonPath("$.body.properties.heading", is(vesselTrackingDTO.getProperties().getHeading())))
				.andExpect(jsonPath("$.body.properties.navStat", is(vesselTrackingDTO.getProperties().getNavStat())))
				.andExpect(jsonPath("$.body.properties.sog", is(vesselTrackingDTO.getProperties().getSog())));
		
		// @formatter:on

		VesselTrackingUpdatedEvent event = (VesselTrackingUpdatedEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		vesselTrackingDTO.getProperties().setInserted(event.getVesselTracking().getProperties().getInserted());
		vesselTrackingDTO.getProperties().setUpdated(event.getVesselTracking().getProperties().getUpdated());

		assertNotNull(event);
		assertEquals(vesselTrackingDTO.getId(), event.getVesselTracking().getId());
		assertEquals(vesselTrackingDTO.getGeometry(), event.getVesselTracking().getGeometry());
		JSONAssert.assertEquals(mapper.writeValueAsString(vesselTrackingDTO.getProperties()),
				mapper.writeValueAsString(event.getVesselTracking().getProperties()), false);

		assertEquals(VesselTrackingEventTypes.UPDATED, event.getType());
		assertEquals("2", event.getVersion().toString());
		assertEquals(vesselTrackingDTO.getId(), event.getAggregateId());
	}

	@Test
	public void deleteVesselTracking_SendDeleteVesselTrackingEvent_IfCommandWasSuccess() throws Exception {

		String tstamp = String.valueOf(new DateTime().getMillis());

		when(vesselTrackingStateStore.getVesselTracking(anyString()))
				.thenReturn(VesselTrackingDataUtil.getVesselTrackingUpdatedEvent(mmsi, tstamp));

		String id = VesselTrackingDataUtil.PREFIX + mmsi + "-" + tstamp;

		// @formatter:off
		
		this.mockMvc
				.perform(delete(VESSELTRACKING_PATH + "/"+ id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)));
		
		// @formatter:on

		VesselTrackingDeletedEvent event = (VesselTrackingDeletedEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		assertNotNull(event);
		assertEquals(VesselTrackingEventTypes.DELETED, event.getType());
		assertEquals("3", event.getVersion().toString());
		assertEquals(id, event.getAggregateId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getEditSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		Map<String, Object> schemaExpected = (Map<String, Object>) JsonToBeanTestUtil
				.getBean("/data/schemas/vesseltrackingschema.json", Map.class);

		// @formatter:off
		
		this.mockMvc.perform(get(VESSELTRACKING_PATH + editSchemaPath)
				.header("Authorization", "Bearer " + getTokenOAGUser())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", is(schemaExpected)));
		// @formatter:on
	}

	@KafkaHandler
	public void enrichCreateVesselTracking(EnrichCreateVesselTrackingEvent enrichCreateVesselTrackingEvent) {

		CreateVesselTrackingEnrichedEvent createEnrichedEvent = new CreateVesselTrackingEnrichedEvent()
				.buildFrom(enrichCreateVesselTrackingEvent);

		createEnrichedEvent.setVesselTracking(enrichCreateVesselTrackingEvent.getVesselTracking());

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TRACKING_TOPIC,
				enrichCreateVesselTrackingEvent.getAggregateId(), createEnrichedEvent);
		future.addCallback(new SendListener());
	}

	@KafkaHandler
	public void createVesselTracking(CreateVesselTrackingEvent createVesselTrackingEvent) {

		CreateVesselTrackingConfirmedEvent createConfirmEvent = new CreateVesselTrackingConfirmedEvent()
				.buildFrom(createVesselTrackingEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TRACKING_TOPIC,
				createVesselTrackingEvent.getAggregateId(), createConfirmEvent);
		future.addCallback(new SendListener());
	}

	@KafkaHandler
	public void VesselTrackingCreated(VesselTrackingCreatedEvent vesselTrackingCreatedEvent) {
		blockingQueue.offer(vesselTrackingCreatedEvent);
	}

	@KafkaHandler
	public void enrichUpdateVesselTracking(EnrichUpdateVesselTrackingEvent enrichUpdateVesselTrackingEvent) {

		UpdateVesselTrackingEnrichedEvent updateEnrichedEvent = new UpdateVesselTrackingEnrichedEvent()
				.buildFrom(enrichUpdateVesselTrackingEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TRACKING_TOPIC,
				enrichUpdateVesselTrackingEvent.getAggregateId(), updateEnrichedEvent);
		future.addCallback(new SendListener());
	}

	@KafkaHandler
	public void updateVesselTracking(UpdateVesselTrackingEvent updateVesselTrackingEvent) {

		UpdateVesselTrackingConfirmedEvent updateConfirmEvent = new UpdateVesselTrackingConfirmedEvent()
				.buildFrom(updateVesselTrackingEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TRACKING_TOPIC,
				updateVesselTrackingEvent.getAggregateId(), updateConfirmEvent);
		future.addCallback(new SendListener());
	}

	@KafkaHandler
	public void VesselTrackingUpdated(VesselTrackingUpdatedEvent vesselTrackingUpdatedEvent) {
		blockingQueue.offer(vesselTrackingUpdatedEvent);
	}

	@KafkaHandler
	public void deleteVesselTracking(DeleteVesselTrackingEvent deleteVesselTrackingEvent) {

		DeleteVesselTrackingConfirmedEvent deleteConfirmEvent = new DeleteVesselTrackingConfirmedEvent()
				.buildFrom(deleteVesselTrackingEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TRACKING_TOPIC,
				deleteVesselTrackingEvent.getAggregateId(), deleteConfirmEvent);
		future.addCallback(new SendListener());
	}

	@KafkaHandler
	public void VesselTrackingDeleted(VesselTrackingDeletedEvent vesselTrackingDeletedEvent) {
		blockingQueue.offer(vesselTrackingDeletedEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
