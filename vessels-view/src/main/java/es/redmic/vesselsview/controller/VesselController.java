package es.redmic.vesselsview.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventFactory;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselsview.model.Vessel;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.vesselsview.service.VesselESService;
import es.redmic.viewlib.common.controller.RWController;
import es.redmic.viewlib.config.MapperScanBeanItfc;

@Controller
@KafkaListener(topics = "${broker.topic.vessel}")
public class VesselController extends RWController<Vessel, VesselDTO, MetadataQueryDTO> {

	private static Logger logger = LogManager.getLogger();

	@Value("${broker.topic.vessel}")
	private String vessel_topic;

	@Autowired
	MapperScanBeanItfc mapper;

	VesselESService service;

	@Autowired
	public VesselController(VesselESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateVesselEvent event) {

		logger.info("Crear vessel");

		EventApplicationResult result = null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getVessel(), Vessel.class));
		} catch (Exception e) {
			logger.error(
					"Error al procesar CreateVesselEvent para vessel " + event.getAggregateId() + " " + e.getMessage());
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
		}

		if (result.isSuccess()) {
			logger.info("Vessel creado en la vista");
			publishConfirmedEvent(new CreateVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselEvent event) {

		logger.info("Modificar vessel");

		EventApplicationResult result = null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getVessel(), Vessel.class));
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
		}

		if (result.isSuccess()) {
			logger.info("Vessel modificado en la vista");
			publishConfirmedEvent(new UpdateVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselTypeInVesselEvent event) {

		logger.info("Modificar vesseltype en vessel");

		EventApplicationResult result = null;

		try {
			result = service.updateVesselTypeInVessel(event.getAggregateId(),
					mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
		}

		if (result.isSuccess()) {
			logger.info("Vessel modificado en la vista");
			publishConfirmedEvent(new UpdateVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteVesselEvent event) {

		logger.info("Eliminar vessel");

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
		}

		if (result.isSuccess()) {

			logger.info("Vessel eliminado de la vista");
			publishConfirmedEvent(new DeleteVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
