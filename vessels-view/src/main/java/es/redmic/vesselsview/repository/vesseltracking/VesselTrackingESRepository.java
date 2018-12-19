package es.redmic.vesselsview.repository.vesseltracking;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.elasticsearchlib.geodata.repository.RWGeoDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.DataQueryDTO;
import es.redmic.vesselslib.utils.VesselTrackingUtil;
import es.redmic.vesselsview.model.vessel.Vessel;
import es.redmic.vesselsview.model.vesseltracking.VesselTracking;
import es.redmic.viewlib.geodata.repository.IGeoDataRepository;

@Repository
public class VesselTrackingESRepository extends RWGeoDataESRepository<VesselTracking, DataQueryDTO>
		implements IGeoDataRepository<VesselTracking, DataQueryDTO> {

	private static String[] INDEX = { "vessel" };
	private static String[] TYPE = { "tracking" };

	// @formatter:off
 
		private final String ID_PROPERTY = "id",
				UUID_PROPERTY = "uuid",
				MMSI_PROPERTY = "properties.vessel.mmsi",
				DATE_PROPERTY = "properties.date",
				VESSEL_PROPERTY = "properties.vessel";
	// @formatter:on

	public VesselTrackingESRepository() {
		super(INDEX, TYPE);
	}

	@SuppressWarnings("unchecked")
	public EventApplicationResult updateVesselTypeInVessel(String vesselTrackingId, Vessel vessel) {

		XContentBuilder doc;

		try {
			doc = jsonBuilder().startObject().field(VESSEL_PROPERTY, objectMapper.convertValue(vessel, Map.class))
					.endObject();
		} catch (IllegalArgumentException | IOException e1) {
			LOGGER.debug("Error modificando el item con id " + vesselTrackingId + " en " + getIndex()[0] + " "
					+ getType()[0]);
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString());
		}

		return update(vesselTrackingId, doc);
	}

	/**
	 * Función que comprueba que un elemento puede ser añadido a elasticsearch
	 * cumpliendo todas las restricciones
	 * 
	 * Que no exista un elemento con el mismo id. | Que no exista un elemento con el
	 * mismo mmsi para la misma fecha. | En caso de ser un elemento procesado (No
	 * speed layer), no debe existir un elmento con el mismo uuid
	 */

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(VesselTracking modelToIndex) {

		boolean notProcessed = modelToIndex.getUuid().equals(VesselTrackingUtil.UUID_DEFAULT);

		// @formatter:off

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				mmsiTerm = QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery(MMSI_PROPERTY, modelToIndex.getProperties().getVessel().getMmsi()))
						.must(QueryBuilders.termQuery(DATE_PROPERTY, modelToIndex.getProperties().getDate())),
				uuidTerm = QueryBuilders.boolQuery()
					.must(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()))
					.must(QueryBuilders.termQuery(UUID_PROPERTY, modelToIndex.getUuid()));
		
		if (!notProcessed) {
			QueryBuilder aux = uuidTerm;
			uuidTerm = QueryBuilders.boolQuery()
					.should(QueryBuilders.boolQuery()
							.must(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()))
							.mustNot(QueryBuilders.termQuery(UUID_PROPERTY, VesselTrackingUtil.UUID_DEFAULT)))
					.should(aux);
		}
		
		SearchRequestBuilder requestBuilderId = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(idTerm).setSize(1),
			requestBuilderMmsi = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(mmsiTerm).setSize(1),
			requestBuilderUuid = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(uuidTerm).setSize(1);

		MultiSearchRequestBuilder multiSearchRequestBuilder = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderId)
			.add(requestBuilderMmsi)
			.add(requestBuilderUuid);
		
		MultiSearchResponse sr = multiSearchRequestBuilder.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId());
		}

		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(MMSI_PROPERTY, modelToIndex.getProperties().getVessel().getMmsi().toString());
			arguments.put(DATE_PROPERTY, modelToIndex.getProperties().getDate().toString());
		}

		if (responses != null && responses[2].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(UUID_PROPERTY, modelToIndex.getUuid().toString());
		} else if (!notProcessed) { // Si no es un item no procesado y el uuid del item almacenado no es diferente a
									// not_process entonces no se tienen en cuenta los conflictos
			return new EventApplicationResult(true);
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	/**
	 * Función que comprueba que un elemento puede ser editado cumpliendo todas las
	 * restricciones
	 * 
	 * Que no exista un elemento con diferente id, pero el mismo mmsi y la misma
	 * fecha (Que ya exista). | Que no exista ese uuid en otro elemento
	 */

	// TODO: Controlar ediciones de items no procesados
	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(VesselTracking modelToIndex) {

		// @formatter:off

		BoolQueryBuilder mmsiTerm = QueryBuilders.boolQuery()
					.must(QueryBuilders.termQuery(MMSI_PROPERTY, modelToIndex.getProperties().getVessel().getMmsi()))
					.must(QueryBuilders.termQuery(DATE_PROPERTY, modelToIndex.getProperties().getDate()))
					.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId())),
				idTerm = QueryBuilders.boolQuery()
					.must(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()))
					.mustNot(QueryBuilders.termQuery(UUID_PROPERTY, modelToIndex.getUuid()));
		
		SearchRequestBuilder requestBuilderMmsi = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(mmsiTerm).setSize(1),
				requestBuilderId = null;
		
		if (idTerm != null) {
			requestBuilderId = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(idTerm).setSize(1);
		}

		MultiSearchRequestBuilder multiSearchRequestBuilder = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderMmsi);
		
		if (requestBuilderId != null) {
			multiSearchRequestBuilder.add(requestBuilderId);
		}
		
		MultiSearchResponse sr = multiSearchRequestBuilder.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(MMSI_PROPERTY, modelToIndex.getProperties().getVessel().getMmsi().toString());
			arguments.put(DATE_PROPERTY, modelToIndex.getProperties().getDate().toString());
		}

		if (responses != null && requestBuilderId != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_UPDATE_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkDeleteConstraintsFulfilled(String modelToIndexId) {
		return new EventApplicationResult(true);
	}
}
