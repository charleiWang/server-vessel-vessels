package es.redmic.vesselscommands.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.commandslib.service.CommandGeoServiceItfc;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselscommands.commands.vesseltracking.CreateVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.DeleteVesselTrackingCommand;
import es.redmic.vesselscommands.commands.vesseltracking.UpdateVesselTrackingCommand;
import es.redmic.vesselscommands.handler.VesselTrackingCommandHandler;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingPropertiesDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@Service
public class VesselTrackingCommandService implements CommandGeoServiceItfc<VesselTrackingDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselTrackingCommandHandler commandHandler;

	@Value("${vesseltracking-activity-id}")
	protected String activityId;

	@Autowired
	public VesselTrackingCommandService(VesselTrackingCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	public void create(AISTrackingDTO aisTracking) {

		if (aisTracking.getMmsi() != null || aisTracking.getImo() != null) {
			create(convertTrackToVesselTracking(aisTracking), activityId);
		} else {
			logger.info("Descartado Vessel sin identificador váliado");
		}
	}

	@Override
	public VesselTrackingDTO create(VesselTrackingDTO vesselTracking, String activityId) {

		logger.debug("CreateVesselTracking");

		checkActivityId(activityId);

		vesselTracking.getProperties().setActivityId(activityId);

		return commandHandler.save(new CreateVesselTrackingCommand(vesselTracking));
	}

	@Override
	public VesselTrackingDTO update(String id, VesselTrackingDTO vesselTracking, String activityId) {

		logger.debug("UpdateVesselTracking");

		checkActivityId(activityId);

		vesselTracking.getProperties().setActivityId(activityId);

		return commandHandler.update(id, new UpdateVesselTrackingCommand(vesselTracking));
	}

	@Override
	public VesselTrackingDTO delete(String id, String activityId) {

		logger.debug("DeleteVesselTracking");

		checkActivityId(activityId);

		return commandHandler.update(id, new DeleteVesselTrackingCommand(id));
	}

	private void checkActivityId(String requestId) {

		if (!requestId.equals(this.activityId))
			throw new FieldNotValidException("activityId", requestId);
	}

	private VesselTrackingDTO convertTrackToVesselTracking(AISTrackingDTO aisTracking) {

		if (aisTracking.getMmsi() == null)
			throw new FieldNotValidException("mmsi", "null");

		if (aisTracking.getTstamp() == null)
			throw new FieldNotValidException("date", "null");

		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

		VesselTrackingDTO vesselTracking = new VesselTrackingDTO();

		Point geometry = geometryFactory
				.createPoint(new Coordinate(aisTracking.getLatitude(), aisTracking.getLongitude()));

		vesselTracking.setGeometry(geometry);

		VesselTrackingPropertiesDTO properties = new VesselTrackingPropertiesDTO();

		// TODO: asignar una actividad para este tipo de datos?
		// properties.setActivityId(activityId);

		VesselDTO vessel = new VesselDTO();
		vessel.setMmsi(aisTracking.getMmsi());
		vessel.setImo(aisTracking.getImo());
		properties.setVessel(vessel);

		properties.setDate(aisTracking.getTstamp());

		properties.setCog(aisTracking.getCog());
		properties.setSog(aisTracking.getSog());
		properties.setHeading(aisTracking.getHeading());
		properties.setNavStat(aisTracking.getNavStat());
		properties.setDest(aisTracking.getDest());
		properties.setEta(aisTracking.getEta());

		vesselTracking.setProperties(properties);

		return vesselTracking;
	}
}
