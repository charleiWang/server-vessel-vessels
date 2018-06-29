package es.redmic.test.vesselscommands.integration.vesseltype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.ListenableFuture;

import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.listener.SendListener;
import es.redmic.test.vesselscommands.integration.common.CommonIntegrationTest;
import es.redmic.vesselscommands.commands.VesselTypeCommandHandler;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@TestPropertySource(properties = { "spring.kafka.consumer.group-id=GetVesselTypeStateFromStoreTest",
		"spring.kafka.client-id=GetVesselTypeStateFromStoreTest" })
public class GetVesselTypeStateFromStoreTest extends CommonIntegrationTest {

	protected static Logger logger = LogManager.getLogger();

	// @formatter:off
	
	private static final String code = "1234",
			codeOther = "5678",
			aggregateId = VesselTypeDataUtil.PREFIX + code,
			aggregateIdOther = VesselTypeDataUtil.PREFIX + codeOther;
	
	// @formatter:on

	@Value("${broker.topic.vessel-type}")
	private String vessel_type_topic;

	@Autowired
	private KafkaTemplate<String, Event> kafkaTemplate;

	@Autowired
	private VesselTypeCommandHandler vesselTypeCommandHandler;

	@Test
	public void createVesselType_getVesselTypeState_IfConfigIsCorrect() throws Exception {

		ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(vessel_type_topic, aggregateId,
				VesselTypeDataUtil.getCreateEvent(code));
		future.addCallback(new SendListener());

		UpdateVesselTypeEvent updateEvent = VesselTypeDataUtil.getUpdateEvent(code);

		updateEvent.getVesselType().setName("updated");

		ListenableFuture<SendResult<String, Event>> future2 = kafkaTemplate.send(vessel_type_topic, aggregateId,
				updateEvent);
		future2.addCallback(new SendListener());

		ListenableFuture<SendResult<String, Event>> future3 = kafkaTemplate.send(vessel_type_topic, aggregateIdOther,
				VesselTypeDataUtil.getCreateEvent(codeOther));
		future3.addCallback(new SendListener());

		VesselTypeDTO vesselType = null;

		while (!(future.isDone() && future2.isDone() && future3.isDone())) {
			logger.info("Waitting for msg send confirmation");
		}

		for (int i = 0; i < 10; i++) {
			try {
				logger.info("try " + i);
				vesselType = vesselTypeCommandHandler.getVesselType(VesselTypeDataUtil.getVesselType(code));
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
				}
			}
		}

		assertNotNull(vesselType);
		assertEquals(updateEvent.getAggregateId(), vesselType.getId());
		assertEquals(updateEvent.getVesselType().getId(), vesselType.getId());
		assertEquals(updateEvent.getVesselType().getCode(), vesselType.getCode());
		assertEquals(updateEvent.getVesselType().getName(), vesselType.getName());
	}
}
