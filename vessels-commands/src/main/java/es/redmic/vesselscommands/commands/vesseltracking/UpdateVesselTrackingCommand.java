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

import org.joda.time.DateTime;

import es.redmic.commandslib.commands.Command;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.utils.VesselUtil;

public class UpdateVesselTrackingCommand extends Command {

	private VesselTrackingDTO vesselTracking;

	public UpdateVesselTrackingCommand() {
	}

	public UpdateVesselTrackingCommand(VesselTrackingDTO vesselTracking) {

		vesselTracking.getProperties().setUpdated(DateTime.now());

		VesselDTO vessel = vesselTracking.getProperties().getVessel();

		// Se añade id generado a vesselType para poder buscarlo
		if (vessel != null && vessel.getId() == null) {
			vesselTracking.getProperties().getVessel().setId(VesselUtil.generateId(vessel.getMmsi()));
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
