package es.redmic.vesselscommands.commands.vesseltype;

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

import es.redmic.commandslib.commands.Command;
import es.redmic.exception.databinding.FieldNotValidException;
import es.redmic.vesselslib.dto.vesseltype.VesselTypeDTO;
import es.redmic.vesselslib.utils.VesselTypeUtil;

public class CreateVesselTypeCommand extends Command {

	private VesselTypeDTO vesselType;

	public CreateVesselTypeCommand() {
	}

	public CreateVesselTypeCommand(VesselTypeDTO vesselType) {

		if (vesselType.getId() == null && vesselType.getCode() == null)
			throw new FieldNotValidException("code", "null");

		if (vesselType.getId() == null && vesselType.getCode() != null) {
			// Se crea un id único para el vessel
			vesselType.setId(VesselTypeUtil.generateId(vesselType.getCode()));
		}
		this.setVessel(vesselType);
	}

	public VesselTypeDTO getVesselType() {
		return vesselType;
	}

	public void setVessel(VesselTypeDTO vesselType) {
		this.vesselType = vesselType;
	}
}
