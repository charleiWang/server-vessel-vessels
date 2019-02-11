package es.redmic.vesselscommands.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.commandslib.service.CommandServiceItfc;
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselscommands.commands.vessel.DeleteVesselCommand;
import es.redmic.vesselscommands.commands.vessel.UpdateVesselCommand;
import es.redmic.vesselscommands.handler.VesselCommandHandler;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

@Service
public class VesselCommandService implements CommandServiceItfc<VesselDTO> {

	protected static Logger logger = LogManager.getLogger();

	private final VesselCommandHandler commandHandler;

	@Autowired
	public VesselCommandService(VesselCommandHandler commandHandler) {
		this.commandHandler = commandHandler;
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
}
