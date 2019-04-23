package es.redmic.vesselsview.common.query;

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

import java.util.HashSet;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import es.redmic.elasticsearchlib.common.query.DataQueryUtils;
import es.redmic.models.es.common.query.dto.DataQueryDTO;

public abstract class VesselTrackingQueryUtils extends DataQueryUtils {

	// @FORMATTER:OFF

	/*-public final static BoolQueryBuilder INTERNAL_QUERY = QueryBuilders.boolQuery()
			.must(QueryBuilders.boolQuery()
					.should(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.PLATFORM_TRACKING))
					.should(QueryBuilders.termQuery(INTRACK_PATH + ".id", DataPrefixType.ANIMAL_TRACKING)));-*/

	// @FORMATTER:ON

	public static BoolQueryBuilder getQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {
		return getTrackingQuery(queryDTO, internalQuery, partialQuery);
	}

	protected static BoolQueryBuilder getTrackingQuery(DataQueryDTO queryDTO, QueryBuilder internalQuery,
			QueryBuilder partialQuery) {

		BoolQueryBuilder query = getGeoDataQuery(queryDTO, internalQuery, partialQuery);

		/*-addMustTermIfExist(query, getZQuery(INTRACK_PATH, Z_PROPERTY, SEARCH_BY_Z_RANGE_SCRIPT, queryDTO.getZ()));
		addMustTermIfExist(query, getDateLimitsQuery(queryDTO.getDateLimits(), INTRACK_PATH + "." + DATE_PROPERTY));
		addMustTermIfExist(query, getFlagQuery(queryDTO.getQFlags(), INTRACK_PATH + "." + QFLAG_PROPERTY));
		addMustTermIfExist(query, getFlagQuery(queryDTO.getVFlags(), INTRACK_PATH + "." + VFLAG_PROPERTY));
		addMustTermIfExist(query, getPrecisionQuery(queryDTO.getPrecision()));-*/

		return getResultQuery(query);
	}

	@SuppressWarnings("serial")
	public static HashSet<String> getFieldsExcludedOnQuery() {

		return new HashSet<String>() {
			{
				// add(VALUE_QUERY_FIELD);
			}
		};
	}
}
