package es.redmic.vesselsview.controller.vesseltracking;

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
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventFactory;
import es.redmic.vesselslib.events.vesseltracking.VesselTrackingEventTypes;
import es.redmic.vesselslib.events.vesseltracking.create.CreateVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.delete.DeleteVesselTrackingEvent;
import es.redmic.vesselslib.events.vesseltracking.update.UpdateVesselTrackingEvent;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.vesselsview.service.vesseltracking.VesselTrackingESService;
import es.redmic.viewlib.config.MapperScanBeanItfc;
import es.redmic.viewlib.geodata.controller.GeoDataController;

@Controller
@RequestMapping(value = "${controller.mapping.vesseltracking}")
@KafkaListener(topics = "${broker.topic.vessel-tracking}")
public class VesselTrackingController extends GeoDataController<VesselTracking, VesselTrackingDTO, DataQueryDTO> {

	private static Logger logger = LogManager.getLogger();

	@Value("${broker.topic.vessel-tracking}")
	private String vesseltracking_topic;

	@Autowired
	MapperScanBeanItfc mapper;

	VesselTrackingESService service;

	@Autowired
	public VesselTrackingController(VesselTrackingESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateVesselTrackingEvent event) {

		logger.info("Crear vesselTracking");

		EventApplicationResult result = null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));
		} catch (Exception e) {
			logger.error("Error al procesar CreateVesselTrackingEvent para vesselTracking " + event.getAggregateId()
					+ " " + e.getMessage());
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vesseltracking_topic);
			return;
		}

		if (result.isSuccess()) {
			logger.info("VesselTracking creado en la vista");
			publishConfirmedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_CONFIRMED),
					vesseltracking_topic);
		} else {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vesseltracking_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselTrackingEvent event) {

		logger.info("Modificar vesselTracking");

		EventApplicationResult result = null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getVesselTracking(), VesselTracking.class));
		} catch (Exception e) {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vesseltracking_topic);
			return;
		}

		if (result.isSuccess()) {
			logger.info("VesselTracking modificado en la vista");
			publishConfirmedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE_CONFIRMED),
					vesseltracking_topic);
		} else {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vesseltracking_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteVesselTrackingEvent event) {

		logger.info("Eliminar vesselTracking");

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vesseltracking_topic);
			return;
		}

		if (result.isSuccess()) {

			logger.info("VesselTracking eliminado de la vista");
			publishConfirmedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETE_CONFIRMED),
					vesseltracking_topic);
		} else {
			publishFailedEvent(VesselTrackingEventFactory.getEvent(event, VesselTrackingEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vesseltracking_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
