package es.redmic.test.vesselscommands.integration.vesseltype;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.vesselscommands.integration.KafkaEmbeddedConfig;
import es.redmic.testutils.documentation.DocumentationCommandBaseTest;
import es.redmic.vesselscommands.VesselsCommandsApplication;
import es.redmic.vesselscommands.handler.VesselTypeCommandHandler;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { VesselsCommandsApplication.class })
@ActiveProfiles("test")
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselTypeFromRestTest",
		"schema.registry.port=18086" })
@KafkaListener(topics = "${broker.topic.vessel-type}", groupId = "test")
public class CreateVesselTypeFromRestTest extends DocumentationCommandBaseTest {

	@ClassRule
	public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(KafkaEmbeddedConfig.NUM_BROKERS, true,
			KafkaEmbeddedConfig.PARTITIONS_PER_TOPIC, KafkaEmbeddedConfig.TOPICS_NAME);

	private final String CODE = "70";

	// @formatter:off
	
	private final String HOST = "redmic.es/api/vessels/commands",
			VESSELTYPE_PATH = "/vesseltype";
	
	// @formatter:on

	@Autowired
	VesselTypeCommandHandler vesselTypeCommandHandler;

	VesselTypeStateStore vesselTypeStateStore;

	protected static BlockingQueue<Object> blockingQueue;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Value("${broker.topic.vessel-type}")
	private String VESSEL_TYPE_TOPIC;

	@PostConstruct
	public void CreateVesselTypeFromRestTestPostConstruct() throws Exception {

		createSchemaRegistryRestApp(embeddedKafka.getZookeeperConnectionString(), embeddedKafka.getBrokersAsString());
	}

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		vesselTypeStateStore = Mockito.mock(VesselTypeStateStore.class);

		Whitebox.setInternalState(vesselTypeCommandHandler, "vesselTypeStateStore", vesselTypeStateStore);

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
	public void createVesselType_SendCreateVesselTypeEvent_IfCommandWasSuccess() throws Exception {

		VesselTypeDTO vesselTypeDTO = VesselTypeDataUtil.getVesselType(CODE);

		// @formatter:off
		
		String id = VesselTypeDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(post(VESSELTYPE_PATH)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(vesselTypeDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)))
				.andExpect(jsonPath("$.body.code", is(vesselTypeDTO.getCode())))
				.andExpect(jsonPath("$.body.name", is(vesselTypeDTO.getName())))
				.andExpect(jsonPath("$.body.name_en", is(vesselTypeDTO.getName_en())));
		
		// @formatter:on

		CreateVesselTypeEvent event = (CreateVesselTypeEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		CreateVesselTypeEvent expectedEvent = VesselTypeDataUtil.getCreateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getVesselType().getName(), expectedEvent.getVesselType().getName());
		assertEquals(event.getVesselType().getName_en(), expectedEvent.getVesselType().getName_en());
		assertEquals(event.getVesselType().getCode(), expectedEvent.getVesselType().getCode());
	}

	@Test
	public void updateVesselType_SendUpdateVesselTypeEvent_IfCommandWasSuccess() throws Exception {

		when(vesselTypeStateStore.getVesselType(anyString()))
				.thenReturn(VesselTypeDataUtil.getVesselTypeCreatedEvent(CODE));

		VesselTypeDTO vesselTypeDTO = VesselTypeDataUtil.getVesselType(CODE);

		// @formatter:off
		
		String id = VesselTypeDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(put(VESSELTYPE_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.content(mapper.writeValueAsString(vesselTypeDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.body", notNullValue()))
				.andExpect(jsonPath("$.body.id", is(id)));
		
		// @formatter:on

		UpdateVesselTypeEvent event = (UpdateVesselTypeEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		UpdateVesselTypeEvent expectedEvent = VesselTypeDataUtil.getUpdateEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getVesselType().getName(), expectedEvent.getVesselType().getName());
		assertEquals(event.getVesselType().getName_en(), expectedEvent.getVesselType().getName_en());
		assertEquals(event.getVesselType().getCode(), expectedEvent.getVesselType().getCode());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@Test
	public void deleteVesselType_SendDeleteVesselTypeEvent_IfCommandWasSuccess() throws Exception {

		when(vesselTypeStateStore.getVesselType(anyString()))
				.thenReturn(VesselTypeDataUtil.getVesselTypeUpdatedEvent(CODE));

		// @formatter:off
		
		String id = VesselTypeDataUtil.PREFIX + CODE;
		
		this.mockMvc
				.perform(delete(VESSELTYPE_PATH + "/" + id)
						.header("Authorization", "Bearer " + getTokenOAGUser())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)));
		
		// @formatter:on

		DeleteVesselTypeEvent event = (DeleteVesselTypeEvent) blockingQueue.poll(50, TimeUnit.SECONDS);

		DeleteVesselTypeEvent expectedEvent = VesselTypeDataUtil.getDeleteEvent(CODE);
		assertNotNull(event);
		assertEquals(event.getType(), expectedEvent.getType());
		assertEquals(event.getVersion(), expectedEvent.getVersion());
		assertEquals(event.getAggregateId(), expectedEvent.getAggregateId());
	}

	@Test
	public void getEditSchema_Return200_WhenSchemaIsAvailable() throws Exception {

		// @formatter:off
		
		this.mockMvc.perform(get(VESSELTYPE_PATH + editSchemaPath)
				.header("Authorization", "Bearer " + getTokenOAGUser())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title", is("Vessel Type DTO")))
			.andExpect(jsonPath("$.properties", notNullValue()))
			.andExpect(jsonPath("$.properties.id", notNullValue()));
			// TODO: aumentar el nivel de checkeo
		// @formatter:on
	}

	@KafkaHandler
	public void createVesselType(CreateVesselTypeEvent createVesselTypeEvent) {

		CreateVesselTypeConfirmedEvent createVesselTypeConfirmEvent = new CreateVesselTypeConfirmedEvent()
				.buildFrom(createVesselTypeEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				createVesselTypeEvent.getAggregateId(), createVesselTypeConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(createVesselTypeEvent);
	}

	@KafkaHandler
	public void updateVesselType(UpdateVesselTypeEvent updateVesselTypeEvent) {

		UpdateVesselTypeConfirmedEvent updateConfirmEvent = new UpdateVesselTypeConfirmedEvent()
				.buildFrom(updateVesselTypeEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				updateVesselTypeEvent.getAggregateId(), updateConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(updateVesselTypeEvent);
	}

	@KafkaHandler
	public void deleteVesselType(DeleteVesselTypeEvent deleteVesselTypeEvent) {

		DeleteVesselTypeConfirmedEvent deleteConfirmEvent = new DeleteVesselTypeConfirmedEvent()
				.buildFrom(deleteVesselTypeEvent);

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(VESSEL_TYPE_TOPIC,
				deleteVesselTypeEvent.getAggregateId(), deleteConfirmEvent);
		future.addCallback(new SendListener());

		blockingQueue.offer(deleteVesselTypeEvent);
	}

	@KafkaHandler(isDefault = true)
	public void defaultEvent(Object def) {

	}
}
