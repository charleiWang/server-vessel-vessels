package es.redmic.vesselsview.service.vesseltracking;

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
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.vesselslib.dto.tracking.VesselTrackingDTO;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.vesselsview.repository.vesseltracking.VesselTrackingESRepository;
import es.redmic.viewlib.geodata.service.RWGeoDataService;

@Service
public class VesselTrackingESService extends RWGeoDataService<VesselTracking, VesselTrackingDTO, DataQueryDTO> {

	VesselTrackingESRepository repository;

	@Autowired
	public VesselTrackingESService(VesselTrackingESRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public EventApplicationResult updateVesselInVesselTracking(String vesselTrackingId, Vessel model) {
		return repository.updateVesselTypeInVessel(vesselTrackingId, model);
	}

	@Override
	protected String[] getDefaultSearchFields() {
		// @formatter:off
		return new String[] {
			"properties.dest",
			"properties.vessel.name",
			"properties.vessel.mmsi",
			"properties.vessel.imo",
			
			"properties.dest.suggest",
			"properties.vessel.name.suggest",
			"properties.vessel.mmsi.suggest",
			"properties.vessel.imo.suggest"
		};
		// @formatter:on
	}

	@Override
	protected String[] getDefaultHighlightFields() {
		// @formatter:off
		return new String[] {
			"properties.dest.suggest",
			"properties.vessel.name.suggest",
			"properties.vessel.mmsi.suggest",
			"properties.vessel.imo.suggest"
		};
		// @formatter:on
	}

	@Override
	protected String[] getDefaultSuggestFields() {
		// @formatter:off
		return new String[] {
			"properties.dest",
			"properties.vessel.name",
			"properties.vessel.mmsi",
			"properties.vessel.imo"
		};
		// @formatter:on
	}
}
