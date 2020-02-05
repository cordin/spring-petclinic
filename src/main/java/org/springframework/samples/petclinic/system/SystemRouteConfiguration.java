/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.system;

import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;

/**
 * Configuration of routes for Vet functions.
 *
 * @author Cèsar Ordiñana
 */
@Configuration
public class SystemRouteConfiguration {

	@Bean
	public RouterFunction<?> systemFunction() {
		return route().GET("/", request -> ok().render("welcome")).GET("/oups", request -> {
			throw new RuntimeException(
					"Expected: route used to showcase what " + "happens when an exception is thrown");
		}).build();
	}

}
