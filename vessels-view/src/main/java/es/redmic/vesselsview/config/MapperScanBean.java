package es.redmic.vesselsview.config;

import org.joda.time.DateTime;
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

		addConverter(new PassThroughConverter(org.joda.time.DateTime.class));
		addConverter(new PassThroughConverter(DateTime.class));
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