package es.redmic.test.vesselscommands.integration.vessel;

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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.rule.KafkaEmbedded;
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
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.handler.VesselCommandHandler;
import es.redmic.vesselscommands.statestore.VesselStateStore;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselFromRestTest",
		"schema.registry.port=18081" })
@KafkaListener(topics = "${broker.topic.vessel}", groupId = "test")
public class CreateVesselFromRestTest extends DocumentationCommandBaseTest {

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private final Integer mmsi = 1111;

	// @formatter:off
	
	private final String HOST = "redmic.es/api/vessels/commands";
	
	// @formatter:on

	VesselStateStore vesselsStateStore;

	@Autowired
	VesselCommandHandler vesselCommandHandler;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	@Value("${broker.topic.vessel-type}")
	private String VESSEL_TYPE_TOPIC;

	@Value("${broker.topic.vessel}")
	private String VESSEL_TOPIC;

	@PostConstruct
	public void CreateVesselFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		vesselsStateStore = Mockito.mock(VesselStateStore.class);

		Whitebox.setInternalState(vesselCommandHandler, "vesselStateStore", vesselsStateStore);

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
	public void createVessel_SendCreateVesselEvent_IfCommandWasSuccess() throws Exception {

		VesselDTO vesselDTO = VesselDataUtil.getVessel(mmsi);

		String id = VesselDataUtil.PREFIX + mmsi;

		// @formatter:off
		this.mockMvc
				.perform(post("")
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(vesselDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.mmsi", is(vesselDTO.getMmsi())))
				.andExpect(jsonPath("$.body.imo", is(vesselDTO.getImo())))
				.andExpect(jsonPath("$.body.type.name", is(vesselDTO.getType().getName())))
				.andExpect(jsonPath("$.body.name", is(vesselDTO.getName())));
		
		// @formatter:on

		CreateVesselEvent event = (CreateVesselEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		CreateVesselEvent expectedEvent = VesselDataUtil.getCreateEvent(mmsi);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getVessel().getName(), expectedEvent.getVessel().getName());
		assertEquals(event.getVessel().getMmsi(), expectedEvent.getVessel().getMmsi());
		assertEquals(event.getVessel().getImo(), expectedEvent.getVessel().getImo());
		assertEquals(event.getVessel().getCallSign(), expectedEvent.getVessel().getCallSign());

	}

	@Test
	public void updateVessel_SendUpdateVesselEvent_IfCommandWasSuccess() throws Exception {

		String id = VesselDataUtil.PREFIX + mmsi;

		when(vesselsStateStore.getVessel(id)).thenReturn(VesselDataUtil.getVesselCreatedEvent(mmsi));

		VesselDTO vesselDTO = VesselDataUtil.getVessel(mmsi);

		// @formatter:off
		
		this.mockMvc
				.perform(put("/"+ id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(vesselDTO))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)));
		
		// @formatter:on

		UpdateVesselEvent event = (UpdateVesselEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		UpdateVesselEvent expectedEvent = VesselDataUtil.getUpdateEvent(mmsi);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getVessel().getName(), expectedEvent.getVessel().getName());
		assertEquals(event.getVessel().getMmsi(), expectedEvent.getVessel().getMmsi());
		assertEquals(event.getVessel().getImo(), expectedEvent.getVessel().getImo());
		assertEquals(event.getVessel().getCallSign(), expectedEvent.getVessel().getCallSign());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@Test
	public void deleteVessel_SendDeleteVesselEvent_IfCommandWasSuccess() throws Exception {

		when(vesselsStateStore.getVessel(anyString())).thenReturn(VesselDataUtil.getVesselUpdatedEvent(mmsi));

		String id = VesselDataUtil.PREFIX + mmsi;

		// @formatter:off
		
		this.mockMvc
				.perform(delete("/"+ id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)));
		
		// @formatter:on

		DeleteVesselEvent event = (DeleteVesselEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		DeleteVesselEvent expectedEvent = VesselDataUtil.getDeleteEvent(mmsi);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@Test
	public void getEditSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(editSchemaPath)
				.header("Authorization", "Bearer " + getTokenOAGUser())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title", is("Vessel DTO")))
			.andExpect(jsonPath("$.properties", notNullValue()))
			.andExpect(jsonPath("$.properties.id", notNullValue()))
			.andExpect(jsonPath("$.properties.type", notNullValue()))
			.andExpect(jsonPath("$.properties.type.type", notNullValue()))
			.andExpect(jsonPath("$.properties.type.url", notNullValue()));
			// TODO: aumentar el nivel de checkeo
		// @formatter:on
	}

	@KafkaHandler
	public void createVessel(CreateVesselEvent createVesselEvent) {

		CreateVesselConfirmedEvent createConfirmEvent = new CreateVesselConfirmedEvent().buildFrom(createVesselEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC,
				createVesselEvent.getAggregateId(), createConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(createVesselEvent);
	}

	@KafkaHandler
	public void updateVessel(UpdateVesselEvent updateVesselEvent) {

		UpdateVesselConfirmedEvent updateConfirmEvent = new UpdateVesselConfirmedEvent().buildFrom(updateVesselEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC,
				updateVesselEvent.getAggregateId(), updateConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(updateVesselEvent);
	}

	@KafkaHandler
	public void deleteVessel(DeleteVesselEvent deleteVesselEvent) {

		DeleteVesselConfirmedEvent deleteConfirmEvent = new DeleteVesselConfirmedEvent().buildFrom(deleteVesselEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TOPIC,
				deleteVesselEvent.getAggregateId(), deleteConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(deleteVesselEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
