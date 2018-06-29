package es.redmic.vesselsview.repository;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Repository;

import es.redmic.elasticsearchlib.data.repository.RWDataESRepository;
import es.redmic.exception.common.ExceptionType;
import es.redmic.models.es.common.dto.EventApplicationResult;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.vesselsview.model.VesselType;
import es.redmic.viewlib.data.repository.IDataRepository;

@Repository
public class VesselTypeESRepository extends RWDataESRepository<VesselType, SimpleQueryDTO>
		implements IDataRepository<VesselType, SimpleQueryDTO> {

	private static String[] INDEX = { "platform-domains" };
	private static String[] TYPE = { "vesseltype" };

	// @formatter:off
	 
		private final String ID_PROPERTY = "id",
				CODE_PROPERTY = "code";
	// @formatter:on

	public VesselTypeESRepository() {
		super(INDEX, TYPE);
	}

	@Override
	protected EventApplicationResult checkInsertConstraintsFulfilled(VesselType modelToIndex) {
		// @formatter:off

		QueryBuilder idTerm = QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()),
				codeTerm = QueryBuilders.termQuery(CODE_PROPERTY, modelToIndex.getCode());
		
		SearchRequestBuilder requestBuilderId = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(idTerm).setSize(1),
			requestBuilderCode = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(codeTerm).setSize(1);

		MultiSearchResponse sr = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderId)
			.add(requestBuilderCode)
				.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(ID_PROPERTY, modelToIndex.getId());
		}

		if (responses != null && responses[1].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(CODE_PROPERTY, modelToIndex.getCode().toString());
		}

		if (arguments.size() > 0) {
			return new EventApplicationResult(ExceptionType.ES_INSERT_DOCUMENT.toString(), arguments);
		}

		return new EventApplicationResult(true);
	}

	@Override
	protected EventApplicationResult checkUpdateConstraintsFulfilled(VesselType modelToIndex) {
		// @formatter:off

		BoolQueryBuilder codeTerm = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(CODE_PROPERTY, modelToIndex.getCode()))
				.mustNot(QueryBuilders.termQuery(ID_PROPERTY, modelToIndex.getId()));
		
		SearchRequestBuilder requestBuilderCode = ESProvider.getClient().prepareSearch(getIndex()).setTypes(getType())
				.setQuery(codeTerm).setSize(1);

		MultiSearchResponse sr = ESProvider.getClient().prepareMultiSearch()
			.add(requestBuilderCode)
				.get();

		// @formatter:on

		Map<String, String> arguments = new HashMap<>();

		Item[] responses = sr.getResponses();

		if (responses != null && responses[0].getResponse().getHits().getTotalHits() > 0) {
			arguments.put(CODE_PROPERTY, modelToIndex.getCode().toString());
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
