package es.redmic.vesselscommands.commands.vessel;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselscommands.commands.vesseltype.CreateVesselTypeCommand;
import es.redmic.vesselslib.dto.vessel.VesselDTO;

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

		vessel.setInserted(DateTime.now());
		vessel.setUpdated(DateTime.now());

		// Se añade id generado a vesselType para poder buscarlo
		if (vessel.getType() != null && vessel.getType().getId() == null) {
			vessel.getType().setId(new CreateVesselTypeCommand(vessel.getType()).getVesselType().getId());
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
