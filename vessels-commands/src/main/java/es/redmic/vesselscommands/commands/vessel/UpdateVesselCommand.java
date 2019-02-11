package es.redmic.vesselscommands.commands.vessel;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.utils.VesselTypeUtil;

public class UpdateVesselCommand extends Command {

	private VesselDTO vessel;

	public UpdateVesselCommand() {
	}

	public UpdateVesselCommand(VesselDTO vessel) {

		vessel.setUpdated(DateTime.now());

		// Se añade id generado a vesselType para poder buscarlo
		if (vessel.getType() != null && vessel.getType().getId() == null) {
			vessel.getType().setId(VesselTypeUtil.generateId(vessel.getType().getCode()));
		}

		this.setVessel(vessel);
	}

	public VesselDTO getVessel() {
		return vessel;
	}

	public void setVessel(VesselDTO vessel) {
		this.vessel = vessel;
	}
}
