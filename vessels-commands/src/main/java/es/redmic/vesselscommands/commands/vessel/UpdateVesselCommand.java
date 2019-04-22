package es.redmic.vesselscommands.commands.vessel;

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
