package es.redmic.vesselscommands.commands.vessel;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.utils.VesselTypeUtil;
import es.redmic.vesselslib.utils.VesselUtil;

public class CreateVesselCommand extends Command {

	private VesselDTO vessel;

	public CreateVesselCommand() {
	}

	public CreateVesselCommand(VesselDTO vessel) {

		if (vessel.getId() == null && vessel.getMmsi() == null)
			throw new FieldNotValidException("mmsi", "null");

		if (vessel.getId() == null && vessel.getMmsi() != null) {
			// Se crea un id único para el vessel
			vessel.setId(VesselUtil.generateId(vessel.getMmsi()));
		}

		vessel.setInserted(DateTime.now());
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
