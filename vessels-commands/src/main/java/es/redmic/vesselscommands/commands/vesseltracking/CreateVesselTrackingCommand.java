package es.redmic.vesselscommands.commands.vesseltracking;

/*-
 * #%L
 * Vessels-management
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.UUID;

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.utils.VesselTrackingUtil;
import es.redmic.vesselslib.utils.VesselTypeUtil;
import es.redmic.vesselslib.utils.VesselUtil;

public class CreateVesselTrackingCommand extends Command {

	private VesselTrackingDTO vesselTracking;

	public CreateVesselTrackingCommand() {
	}

	public CreateVesselTrackingCommand(VesselTrackingDTO vesselTracking) {

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		if (vessel == null || (vessel.getMmsi() == null && vessel.getId() == null))
			throw new FieldNotValidException("mmsi", "null");

		if (vesselTracking.getProperties().getDate() == null)
			throw new FieldNotValidException("date", "null");

		// Se añade id generado a vessel para poder buscarlo
		if (vessel != null && vessel.getId() == null) {
			vesselTracking.getProperties().getVessel().setId(VesselUtil.generateId(vessel.getMmsi()));
			vesselTracking.getProperties().getVessel().getType()
					.setId(VesselTypeUtil.generateId(vessel.getType().getCode()));
		}

		if (vesselTracking.getId() == null) {
			// Se crea un id único para vesselTracking
			vesselTracking.setId(VesselTrackingUtil.generateId(vesselTracking.getProperties().getVessel().getMmsi(),
					vesselTracking.getProperties().getDate().getMillis()));
		}

		if (vesselTracking.getUuid() == null || vesselTracking.getUuid().equals(VesselTrackingUtil.UUID_DEFAULT)) {
			vesselTracking.setUuid(UUID.randomUUID().toString());
		}

		vesselTracking.getProperties().setInserted(DateTime.now());
		vesselTracking.getProperties().setUpdated(DateTime.now());

		this.setVesselTracking(vesselTracking);
	}

	public VesselTrackingDTO getVesselTracking() {
		return vesselTracking;
	}

	public void setVesselTracking(VesselTrackingDTO vesselTracking) {
		this.vesselTracking = vesselTracking;
	}
}
