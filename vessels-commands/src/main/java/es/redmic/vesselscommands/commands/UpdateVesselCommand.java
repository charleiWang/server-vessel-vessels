package es.redmic.vesselscommands.commands;

import es.redmic.commandslib.commands.Command;
import es.redmic.vesselslib.dto.VesselDTO;

public class UpdateVesselCommand extends Command {

	private VesselDTO vessel;

	public UpdateVesselCommand() {
	}

	public UpdateVesselCommand(VesselDTO vessel) {
		this.setVessel(vessel);
	}

	public VesselDTO getVessel() {
		return vessel;
	}

	public void setVessel(VesselDTO vessel) {
		this.vessel = vessel;
	}
}
