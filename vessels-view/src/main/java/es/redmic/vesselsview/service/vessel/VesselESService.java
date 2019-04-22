package es.redmic.vesselsview.service.vessel;

/*-
 * #%L
 * Vessels-query-endpoint
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.repository.vessel.VesselESRepository;
import es.redmic.viewlib.data.service.RWDataService;

@Service
public class VesselESService extends RWDataService<Vessel, VesselDTO, MetadataQueryDTO> {

	VesselESRepository repository;

	@Autowired
	public VesselESService(VesselESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult updateVesselTypeInVessel(String vesselId, VesselType model) {
		return repository.updateVesselTypeInVessel(vesselId, model);
	}
}
