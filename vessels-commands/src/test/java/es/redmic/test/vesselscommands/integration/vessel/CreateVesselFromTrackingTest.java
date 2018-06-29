package es.redmic.test.vesselscommands.integration.vessel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.vesselscommands.integration.common.CommonIntegrationTest;
import es.redmic.test.vesselscommands.integration.vesseltype.VesselTypeDataUtil;
import es.redmic.vesselscommands.commands.VesselTypeCommandHandler;
import es.redmic.vesselscommands.statestore.VesselTypeStateStore;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=CreateVesselFromTrackingTest",
		"spring.kafka.client-id=CreateVesselFromTrackingTest" })
public class CreateVesselFromTrackingTest extends CommonIntegrationTest {

	@Value("${broker.topic.realtime.tracking.vessels}")
	String REALTIME_TRACKING_VESSELS_TOPIC;

	@Value("${broker.topic.vessel-type}")
	private String VESSEL_TYPE_TOPIC;

	@Autowired
	private KafkaTemplate<String, AISTrackingDTO> kafkaTemplate;

	protected static BlockingQueue<Object> blockingQueue;

	VesselTypeStateStore vesselTypeStateStore;

	@Autowired
	VesselTypeCommandHandler vesselTypeCommandHandler;

	@BeforeClass
	public static void setup() {

		blockingQueue = new LinkedBlockingDeque<>();
	}

	@Before
	public void before() {

		vesselTypeStateStore = Mockito.mock(VesselTypeStateStore.class);

		Whitebox.setInternalState(vesselTypeCommandHandler, "vesselTypeStateStore", vesselTypeStateStore);

		when(vesselTypeStateStore.getVesselType(VesselTypeDataUtil.PREFIX + "70"))
				.thenReturn(VesselTypeDataUtil.getCreateEvent("70"));
	}

	@Test
	public void createVessel_SendCreateVesselEvent_IfCommandWasSuccess() throws Exception {

		AISTrackingDTO dto = new AISTrackingDTO();
		dto.setMmsi(1);
		dto.setImo(1);
		dto.setName("Pedrito 2");
		dto.setType(70);
		dto.setCallSign("1");
		dto.setTstamp(new DateTime());
		dto.setLatitude(2.1);
		dto.setLongitude(3.2);
		dto.setA(2.9);
		dto.setB(3.5);
		dto.setC(1.4);
		dto.setD(1.4);

		ListenableFuture<SendResult<String, AISTrackingDTO>> future = kafkaTemplate
				.send(REALTIME_TRACKING_VESSELS_TOPIC, dto.getMmsi().toString(), dto);
		future.addCallback(new SendListener());

		VesselDTO vessel = (VesselDTO) blockingQueue.poll(30, TimeUnit.SECONDS);
		assertNotNull(vessel);
		assertEquals(vessel.getMmsi(), dto.getMmsi());
		assertEquals(vessel.getName(), dto.getName());
		Double length = dto.getA() + dto.getB();
		assertEquals(vessel.getLength(), length);

		Double beam = dto.getC() + dto.getD();
		assertEquals(vessel.getBeam(), beam);
	}

	@KafkaListener(topics = "${broker.topic.vessel}", groupId = "test")
	public void run(CreateVesselEvent vesselCreatedEvent) {

		blockingQueue.offer(vesselCreatedEvent.getVessel());
	}
}