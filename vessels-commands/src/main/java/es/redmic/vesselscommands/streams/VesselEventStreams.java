package es.redmic.vesselscommands.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;

import es.redmic.brokerlib.alert.AlertService;
import es.redmic.brokerlib.avro.common.Event;
import es.redmic.brokerlib.avro.common.EventError;
import es.redmic.brokerlib.avro.serde.hashmap.HashMapSerde;
import es.redmic.commandslib.statestore.StreamConfig;
import es.redmic.commandslib.streams.EventStreams;
import es.redmic.vesselslib.dto.VesselDTO;
import es.redmic.vesselslib.dto.VesselTypeDTO;
import es.redmic.vesselslib.events.vessel.VesselEventType;
import es.redmic.vesselslib.events.vessel.common.VesselEvent;
import es.redmic.vesselslib.events.vessel.create.VesselCreatedEvent;
import es.redmic.vesselslib.events.vessel.delete.DeleteVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselCancelledEvent;
import es.redmic.vesselslib.events.vessel.update.UpdateVesselEvent;
import es.redmic.vesselslib.events.vessel.update.VesselUpdatedEvent;
import es.redmic.vesselslib.events.vesseltype.VesselTypeEventType;
import es.redmic.vesselslib.events.vesseltype.common.VesselTypeEvent;

public class VesselEventStreams extends EventStreams {

	private String vesselTypeTopic;

	private AlertService alertService;

	public VesselEventStreams(StreamConfig config, String vesselTypeTopic, AlertService alertService) {
		super(config);
		this.vesselTypeTopic = vesselTypeTopic;
		this.alertService = alertService;
		logger.info("Arrancado servicio de compensación de errores de edición y PostUpdate de Vessel con Id: "
				+ this.serviceId);
		init();
	}

	@Override
	protected void processCreatedStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de confirmación al crear
		KStream<String, Event> createConfirmedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.CREATE_VESSEL_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de crear
		KStream<String, Event> createRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.CREATE_VESSEL.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		createConfirmedEvents.join(createRequestEvents,
				(confirmedEvent, requestEvent) -> getCreatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getCreatedEvent(Event confirmedEvent, Event requestEvent) {

		if (!isSameSession(confirmedEvent, requestEvent)) {
			String message = "Recibido evento de petición con id de sessión diferente al evento de confirmación para item "
					+ confirmedEvent.getAggregateId();
			logger.error(message);
			alertService.errorAlert(confirmedEvent.getAggregateId(), message);
			return null;
		}

		if (!(requestEvent.getType().equals(VesselEventType.CREATE_VESSEL.name()))) {
			logger.error("Se esperaba un evento de petición de tipo CREATE para Vessel.");
			return null;
		}

		logger.debug("Creando evento de creado exitoso para Vessel");

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		if (confirmedEvent.getType().equals(VesselEventType.CREATE_VESSEL_CONFIRMED.name())) {

			logger.info("Enviando evento VesselCreatedEvent para: " + confirmedEvent.getAggregateId());

			VesselCreatedEvent successfulEvent = new VesselCreatedEvent().buildFrom(confirmedEvent);
			successfulEvent.setVessel(vessel);
			return successfulEvent;
		} else {
			logger.error("Se esperaba un evento de confirmación de tipo CREATE para Vessel.");
			return null;
		}
	}

	@Override
	protected void processUpdatedStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de confirmación al modificar
		KStream<String, Event> updateConfirmedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.UPDATE_VESSEL_CONFIRMED.toString().equals(event.getType())));

		// Stream filtrado por eventos de petición de modificar
		KStream<String, Event> updateRequestEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.UPDATE_VESSEL.toString().equals(event.getType())));

		// Join por id, mandando a kafka el evento de éxito
		updateConfirmedEvents.join(updateRequestEvents,
				(confirmedEvent, requestEvent) -> getUpdatedEvent(confirmedEvent, requestEvent),
				JoinWindows.of(windowsTime)).to(topic);
	}

	private Event getUpdatedEvent(Event confirmedEvent, Event requestEvent) {

		if (!isSameSession(confirmedEvent, requestEvent)) {
			String message = "Recibido evento de petición con id de sessión diferente al evento de confirmación para item "
					+ confirmedEvent.getAggregateId();
			logger.error(message);
			alertService.errorAlert(confirmedEvent.getAggregateId(), message);
			return null;
		}

		if (!(requestEvent.getType().equals(VesselEventType.UPDATE_VESSEL.name()))) {
			logger.error("Se esperaba un evento de petición de UPDATE para Vessel.");
			return null;
		}

		logger.debug("Creando evento de modificado exitoso para Vessel");

		VesselDTO vessel = ((VesselEvent) requestEvent).getVessel();

		if (confirmedEvent.getType().equals(VesselEventType.UPDATE_VESSEL_CONFIRMED.name())) {

			logger.info("Enviar evento VesselUpdatedEvent para: " + confirmedEvent.getAggregateId());

			VesselUpdatedEvent successfulEvent = new VesselUpdatedEvent().buildFrom(confirmedEvent);
			successfulEvent.setVessel(vessel);
			return successfulEvent;
		} else {
			logger.error("Se esperaba un evento de confirmación de tipo UPDATE para Vessel.");
			return null;
		}
	}

	@Override
	protected void processFailedChangeStream(KStream<String, Event> vesselEvents) {

		// Stream filtrado por eventos de fallo al modificar y borrar
		KStream<String, Event> failedEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.UPDATE_VESSEL_FAILED.toString().equals(event.getType())
						|| VesselEventType.DELETE_VESSEL_FAILED.toString().equals(event.getType())));

		// Stream filtrado por eventos de creaciones y modificaciones correctos (solo el
		// último que se produzca por id)
		KStream<String, Event> successEvents = vesselEvents
				.filter((id, event) -> (VesselEventType.VESSEL_CREATED.toString().equals(event.getType())
						|| VesselEventType.VESSEL_UPDATED.toString().equals(event.getType())));

		KTable<String, Event> successEventsTable = successEvents.groupByKey().reduce((aggValue, newValue) -> newValue);

		// Join por id, mandando a kafka el evento de compensación
		failedEvents.join(successEventsTable,
				(failedEvent, lastSuccessEvent) -> getCancelledEvent(failedEvent, lastSuccessEvent)).to(topic);
	}

	private Event getCancelledEvent(Event failedEvent, Event lastSuccessEvent) {

		if (!(lastSuccessEvent.getType().equals(VesselEventType.VESSEL_CREATED.name())
				|| lastSuccessEvent.getType().equals(VesselEventType.VESSEL_UPDATED.name()))) {
			logger.error("Se esperaba un evento satisfactorio de tipo CREATED o UPDATED para Vessel.");
			return null;
		}

		logger.debug("Creando evento de cancelación para Vessel");

		VesselDTO vessel = ((VesselEvent) lastSuccessEvent).getVessel();

		EventError eventError = (EventError) failedEvent;

		if (failedEvent.getType().equals(VesselEventType.UPDATE_VESSEL_FAILED.name())) {

			logger.info("Enviando evento UpdateVesselCancelledEvent para: " + failedEvent.getAggregateId());

			UpdateVesselCancelledEvent cancelledEvent = new UpdateVesselCancelledEvent().buildFrom(failedEvent);
			cancelledEvent.setVessel(vessel);
			cancelledEvent.setExceptionType(eventError.getExceptionType());
			cancelledEvent.setArguments(eventError.getArguments());
			return cancelledEvent;

		} else if (failedEvent.getType().equals(VesselEventType.DELETE_VESSEL_FAILED.name())) {

			logger.info("Enviando evento DeleteVesselCancelledEvent para: " + failedEvent.getAggregateId());

			DeleteVesselCancelledEvent cancelledEvent = new DeleteVesselCancelledEvent().buildFrom(failedEvent);
			cancelledEvent.setVessel(vessel);
			cancelledEvent.setExceptionType(eventError.getExceptionType());
			cancelledEvent.setArguments(eventError.getArguments());
			return cancelledEvent;
		} else {
			logger.error("Se esperaba un evento fallido de tipo UPDATE o DELETE para Vessel.");
			return null;
		}
	}

	@Override
	protected void processPostUpdateStream(KStream<String, Event> vesselEvents) {

		KStream<String, Event> vesselEventsStreamByTypeId = vesselEvents
				.selectKey((k, v) -> ((VesselEvent) v).getVessel().getType().getId());

		KTable<String, HashMap<String, VesselEvent>> vesselEventsTable = vesselEventsStreamByTypeId.groupByKey()
				.aggregate(HashMap::new, (key, value, hashMap) -> {
					// Añade a hashmap cada uno de los values
					hashMap.put(value.getAggregateId(), (VesselEvent) value);
					return hashMap;
				}, Materialized.with(Serdes.String(), new HashMapSerde<>(schemaRegistry)));

		// Vesseltypes modificados
		KStream<String, Event> vesselTypeEvents = builder.stream(vesselTypeTopic);

		KStream<String, Event> updateReferenceEvents = vesselTypeEvents
				.filter((id, event) -> (VesselTypeEventType.VESSELTYPE_UPDATED.toString().equals(event.getType())));

		KStream<String, ArrayList<VesselEvent>> join = updateReferenceEvents.join(vesselEventsTable,
				(updateReferenceEvent, vesselWithReferenceEvents) -> getPostUpdateEvent(updateReferenceEvent,
						vesselWithReferenceEvents));

		// desagregar, cambiar clave por la de vessel y enviar a topic
		join.flatMapValues(value -> value).selectKey((k, v) -> v.getVessel().getId()).to(topic);
	}

	private ArrayList<VesselEvent> getPostUpdateEvent(Event updateReferenceEvent,
			HashMap<String, VesselEvent> vesselWithReferenceEvents) {

		ArrayList<VesselEvent> result = new ArrayList<>();

		for (Map.Entry<String, VesselEvent> entry : vesselWithReferenceEvents.entrySet()) {

			VesselEvent vesselEvent = entry.getValue();
			VesselTypeDTO vesselType = ((VesselTypeEvent) updateReferenceEvent).getVesselType();

			if (!isEventCompleted(vesselEvent.getType())) {

				String message = "Item con id " + vesselEvent.getAggregateId()
						+ " se encuentra en mitad de un ciclo de creación o edición, por lo que no se modificó la referencia "
						+ updateReferenceEvent.getAggregateId();

				logger.info(message);
				alertService.errorAlert(vesselEvent.getAggregateId(), message);

			} else {

				logger.debug("Creando evento de update para Vessel " + vesselEvent.getAggregateId()
						+ " por cambio en vesselType");

				if (!vesselEvent.getVessel().getType().equals(vesselType)) {

					UpdateVesselEvent updateVesselEvent = new UpdateVesselEvent();
					updateVesselEvent.setAggregateId(vesselEvent.getAggregateId());
					updateVesselEvent.setUserId(updateReferenceEvent.getUserId());
					updateVesselEvent.setVersion(vesselEvent.getVersion() + 1);

					VesselDTO vessel = vesselEvent.getVessel();
					vessel.setType(((VesselTypeEvent) updateReferenceEvent).getVesselType());
					updateVesselEvent.setVessel(vessel);
					result.add(updateVesselEvent);
				} else {
					logger.debug("VesselType ya estaba actualizado o los campos indexados no han cambiado ");
				}
			}
		}
		return result;
	}

	private boolean isEventCompleted(String type) {

		return (VesselEventType.VESSEL_CREATED.toString().equals(type)
				|| VesselEventType.VESSEL_UPDATED.toString().equals(type)
				|| VesselEventType.VESSEL_DELETED.toString().equals(type)
				|| VesselEventType.CREATE_VESSEL_CANCELLED.toString().equals(type)
				|| VesselEventType.UPDATE_VESSEL_CANCELLED.toString().equals(type)
				|| VesselEventType.DELETE_VESSEL_CANCELLED.toString().equals(type));
	}
}
