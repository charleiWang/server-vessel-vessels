package es.redmic.vesselscommands.commands;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.VesselDTO;

public class CreateVesselCommand extends Command {

	private final String PREFIX = "vessel-mmsi-";

	private VesselDTO vessel;

	public CreateVesselCommand() {
	}

	public CreateVesselCommand(VesselDTO vessel) {

		if (vessel.getId() == null && vessel.getMmsi() == null)
			throw new FieldNotValidException("mmsi", "null");

		if (vessel.getId() == null && vessel.getMmsi() != null) {
			// Se crea un id único para el vessel
			vessel.setId(PREFIX + vessel.getMmsi());
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
