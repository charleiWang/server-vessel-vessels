package es.redmic.vesselscommands.commands.vesseltracking;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.vesselscommands.commands.vessel.CreateVesselCommand;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

public class UpdateVesselTrackingCommand extends Command {

	private VesselTrackingDTO vesselTracking;

	public UpdateVesselTrackingCommand() {
	}

	public UpdateVesselTrackingCommand(VesselTrackingDTO vesselTracking) {

		vesselTracking.getProperties().setUpdated(DateTime.now());

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		// Se añade id generado a vesselType para poder buscarlo
		if (vessel != null && vessel.getId() == null) {
			vesselTracking.getProperties().getVessel().setId(new CreateVesselCommand(vessel).getVessel().getId());
		}

		this.setVesselTracking(vesselTracking);
	}

	public VesselTrackingDTO getVesselTracking() {
		return vesselTracking;
	}

	public void setVesselTracking(VesselTrackingDTO vesselTracking) {
		this.vesselTracking = vesselTracking;
	}
}
