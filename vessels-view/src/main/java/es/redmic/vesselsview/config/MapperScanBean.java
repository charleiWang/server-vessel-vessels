package es.redmic.vesselsview.config;

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

import org.joda.time.DateTime;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import es.redmic.viewlib.config.MapperScanBeanBase;
import es.redmic.viewlib.config.MapperScanBeanItfc;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public class MapperScanBean extends MapperScanBeanBase implements MapperScanBeanItfc {

	public MapperScanBean() {
		super();
	}

	@Override
	protected void addDefaultActions() {

		addConverter(new PassThroughConverter(DateTime.class));
		addConverter(new PassThroughConverter(Point.class));

	}

	@Override
	protected void addObjectFactory() {

	}

	public MapperScanBean build() {

		if (factory == null) {

			factory = new DefaultMapperFactory.Builder().build();
			addObjectFactory();
			addDefaultActions();
		}
		return this;
	}
}
