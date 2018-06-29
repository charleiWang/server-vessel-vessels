package es.redmic.vesselscommands.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.brokerlib.avro.geodata.tracking.vessels.AISTrackingDTO;
import es.redmic.commandslib.service.CommandServiceItfc;
import es.redmic.vesselscommands.commands.CreateVesselCommand;
import es.redmic.vesselscommands.commands.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.UpdateVesselCommand;
import es.redmic.vesselscommands.commands.VesselCommandHandler;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;

@Service
public class VesselCommandService implements CommandServiceItfc<VesselDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselCommandHandler commandHandler;

	@Autowired
	public VesselCommandService(VesselCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	public void create(AISTrackingDTO aisTracking) {

		if (aisTracking.getMmsi() != null || aisTracking.getImo() != null) {
			create(convertTrackToVessel(aisTracking));
		} else {
			logger.info("Descartado Vessel sin identificador váliado");
		}
	}

	@Override
	public VesselDTO create(VesselDTO vessel) {

		logger.debug("CreateVessel");

		return commandHandler.save(new CreateVesselCommand(vessel));
	}

	@Override
	public VesselDTO update(String id, VesselDTO vessel) {

		logger.debug("UpdateVessel");

		return commandHandler.update(id, new UpdateVesselCommand(vessel));
	}

	@Override
	public VesselDTO delete(String id) {

		logger.debug("DeleteVessel");

		return commandHandler.update(id, new DeleteVesselCommand(id));
	}

	private VesselDTO convertTrackToVessel(AISTrackingDTO aisTracking) {

		VesselDTO vessel = new VesselDTO();

		vessel.setMmsi(aisTracking.getMmsi());
		vessel.setName(aisTracking.getName());
		vessel.setCallSign(aisTracking.getCallSign());
		vessel.setImo(aisTracking.getImo());
		vessel.setLength(aisTracking.getA() + aisTracking.getB());
		vessel.setBeam(aisTracking.getC() + aisTracking.getD());

		VesselTypeDTO vesselType = new VesselTypeDTO();
		vesselType.setCode(aisTracking.getType().toString());
		vessel.setType(vesselType);

		return vessel;
	}
}
