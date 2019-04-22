package es.redmic.vesselsview.controller.vessel;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.vesselslib.dto.vessel.VesselDTO;
import es.redmic.vesselslib.events.vessel.VesselEventFactory;
import es.redmic.vesselslib.events.vessel.VesselEventTypes;
import es.redmic.vesselslib.events.vessel.create.CreateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.create.CreateVesselEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselEvent;
import es.redmic.vesselslib.events.vessel.partialupdate.vesseltype.UpdateVesselTypeInVesselEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselConfirmedEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltype.VesselType;
import es.redmic.vesselsview.service.vessel.VesselESService;
import es.redmic.viewlib.config.MapperScanBeanItfc;
import es.redmic.viewlib.data.controller.DataController;

@Controller
@KafkaListener(topics = "${broker.topic.vessel}")
public class VesselController extends DataController<Vessel, VesselDTO, MetadataQueryDTO> {

	@Value("${broker.topic.vessel}")
	private String vessel_topic;

	@Autowired
	MapperScanBeanItfc mapper;

	VesselESService service;

	@Autowired
	public VesselController(VesselESService service) {
		super(service);
		this.service = service;
	}

	@KafkaHandler
	public void listen(CreateVesselEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.save(mapper.getMapperFacade().map(event.getVessel(), Vessel.class));
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.CREATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new CreateVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.CREATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.update(mapper.getMapperFacade().map(event.getVessel(), Vessel.class));
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new UpdateVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler
	public void listen(UpdateVesselTypeInVesselEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.updateVesselTypeInVessel(event.getAggregateId(),
					mapper.getMapperFacade().map(event.getVesselType(), VesselType.class));
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new UpdateVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.UPDATE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler
	public void listen(DeleteVesselEvent event) {

		EventApplicationResult result = null;

		try {
			result = service.delete(event.getAggregateId());
		} catch (Exception e) {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.DELETE_FAILED,
					ExceptionType.INTERNAL_EXCEPTION.name(), null), vessel_topic);
			return;
		}

		if (result.isSuccess()) {
			publishConfirmedEvent(new DeleteVesselConfirmedEvent().buildFrom(event), vessel_topic);
		} else {
			publishFailedEvent(VesselEventFactory.getEvent(event, VesselEventTypes.DELETE_FAILED,
					result.getExeptionType(), result.getExceptionArguments()), vessel_topic);
		}
	}

	@KafkaHandler(isDefault = true)
	public void listenDefualt(Object event) {
	}
}
