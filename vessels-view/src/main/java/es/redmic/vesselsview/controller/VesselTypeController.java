package es.redmic.vesselsview.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.create.CreateVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.delete.DeleteVesselTypeEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeConfirmedEvent;
import es.redmic.vesselslib.events.vesseltype.update.UpdateVesselTypeEvent;
import es.redmic.vesselsview.config.MapperScanBean;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.vesselsview.service.VesselTypeESService;
import es.redmic.vesselsview.utils.VesselTypeEventFactory;
import es.redmic.viewlib.common.controller.RWController;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltype}")
@KafkaListener(topics = "${broker.topic.vessel-type}")
public class VesselTypeController extends RWController<VesselType, VesselTypeDTO, SimpleQueryDTO> {

	private static Logger logger = LogManager.getLogger();

	@Value("${broker.topic.vessel-type}")
	private String vessel_type_topic;

	@Autowired
	protected MapperScanBean mapper;

	VesselTypeESService service;

	@Autowired
	public VesselTypeController(VesselTypeESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateVesselTypeEvent event) {

		logger.info("Crear vessel type");

		EventApplicationResult result = null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));
		} catch (Exception e) {
			publishFailedEvent(VesselTypeEventFactory.getCreateVesselTypeFailedEvent(event,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_type_topic);
		}

		if (result.isSuccess()) {

			logger.info("Vessel type creado de la vista");
			publishConfirmedEvent(new CreateVesselTypeConfirmedEvent().buildFrom(event), vessel_type_topic);
		} else {
			publishFailedEvent(VesselTypeEventFactory.getCreateVesselTypeFailedEvent(event, result.getExeptionType(),
					result.getExceptionArguments()), vessel_type_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselTypeEvent event) {

		logger.info("Modificar vessel type");

		EventApplicationResult result = null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));
		} catch (Exception e) {
			publishFailedEvent(VesselTypeEventFactory.getUpdateVesselTypeFailedEvent(event,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_type_topic);
		}

		if (result.isSuccess()) {

			logger.info("Vessel type modificado en la vista");
			publishConfirmedEvent(new UpdateVesselTypeConfirmedEvent().buildFrom(event), vessel_type_topic);
		} else {
			publishFailedEvent(VesselTypeEventFactory.getUpdateVesselTypeFailedEvent(event, result.getExeptionType(),
					result.getExceptionArguments()), vessel_type_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteVesselTypeEvent event) {

		logger.info("Eliminar vessel type");

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(VesselTypeEventFactory.getDeleteVesselTypeFailedEvent(event,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_type_topic);
		}

		if (result.isSuccess()) {

			logger.info("Vessel type eliminado de la vista");
			publishConfirmedEvent(new DeleteVesselTypeConfirmedEvent().buildFrom(event), vessel_type_topic);
		} else {
			publishFailedEvent(VesselTypeEventFactory.getDeleteVesselTypeFailedEvent(event, result.getExeptionType(),
					result.getExceptionArguments()), vessel_type_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
