package es.redmic.vesselscommands.config;

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

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import es.redmic.restlib.config.ResourceServerConfigurationBase;

@Configuration
public class Oauth2SecurityConfiguration {

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurationBase {

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			
			http.anonymous().and().authorizeRequests()
				.antMatchers(HttpMethod.GET, "/actuator/**").permitAll();
			
			http.authorizeRequests().antMatchers(HttpMethod.POST, "/**").access(
					"#oauth2.hasScope('write') and "
					+ "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
			
			http.authorizeRequests().antMatchers(HttpMethod.PUT, "/**").access(
					"#oauth2.hasScope('write') and hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
			
			http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/**").access(
					"#oauth2.hasScope('write') and "
					+ "hasAnyRole('ROLE_ADMINISTRATOR', 'ROLE_OAG', 'ROLE_COLLABORATOR')");
		}
	}
}
