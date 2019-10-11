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
package org.springframework.samples.petclinic.owner;

import static org.springframework.web.servlet.function.RouterFunctions.route;

import java.util.function.BiFunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.function.RouterFunction;

/**
 * Configuration of routes for Pet endpoints.
 * 
 * @author Cèsar Ordiñana
 */
@Configuration
public class PetRouteConfiguration {

    @Bean
    public PetHandler petHandler(PetRepository repository, OwnerRepository owners,
            BiFunction<Object, String, ServletRequestDataBinder> binderFactory) {
        return new PetHandler(repository, owners, binderFactory);
    }

    @Bean
    public RouterFunction<?> petRouterFunction(PetHandler handler) {
        return route()
                .path("/owners/{ownerId}", builder -> builder
                    .GET("/pets/new", handler::initCreationForm)
                    .POST("/pets/new", handler::processCreationForm)
                    .GET("/pets/{petId}/edit", handler::initUpdateForm)
                    .POST("/pets/{petId}/edit", handler::processUpdateForm)
                )
                .build();
    }

}
