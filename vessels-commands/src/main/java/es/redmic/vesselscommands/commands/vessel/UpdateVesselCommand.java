package es.redmic.vesselscommands.commands.vessel;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.vesselslib.dto.VesselDTO;

public class UpdateVesselCommand extends Command {

	private VesselDTO vessel;

	public UpdateVesselCommand() {
	}

	public UpdateVesselCommand(VesselDTO vessel) {

		vessel.setUpdated(DateTime.now());
		this.setVessel(vessel);
	}

	public VesselDTO getVessel() {
		return vessel;
	}

	public void setVessel(VesselDTO vessel) {
		this.vessel = vessel;
	}
}
