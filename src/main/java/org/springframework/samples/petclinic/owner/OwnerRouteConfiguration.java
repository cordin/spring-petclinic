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
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.function.RouterFunction;

/**
 * Configuration of routes for Owner endpoints.
 * 
 * @author Cèsar Ordiñana
 */
@Configuration
public class OwnerRouteConfiguration {

    @Bean
    public OwnerHandler ownerHandler(OwnerRepository repository, VisitRepository visits,
            BiFunction<Object, String, ServletRequestDataBinder> binderFactory) {
        return new OwnerHandler(repository, visits, binderFactory);
    }

    @Bean
    public RouterFunction<?> ownerRouterFunction(OwnerHandler handler) {
        return route().GET("/owners/new", handler::initCreationForm)
                .POST("/owners/new", handler::processCreationForm)
                .GET("/owners/find", handler::initFindForm)
                .GET("/owners", handler::processFindForm)
                .GET("/owners/{ownerId}/edit", handler::initUpdateOwnerForm)
                .POST("/owners/{ownerId}/edit", handler::processUpdateForm)
                .GET("/owners/{ownerId}", handler::showOwner).build();
    }

}
