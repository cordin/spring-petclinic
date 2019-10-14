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
package org.springframework.samples.petclinic.support;

import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Support methods for Spring MVC handler functions.
 * 
 * @author Cèsar Ordiñana
 */
public class ServerResponseSupport<T extends BaseEntity> {

    private final Validator validator;
    private final ConversionService conversionService;

    /**
     * Creates a new instance.
     * 
     * @param defaultEntityModelName
     * @param validator
     * @param conversionService
     */
    public ServerResponseSupport(Validator validator, ConversionService conversionService) {
        this.validator = validator;
        this.conversionService = conversionService;
    }
    
    public ServerResponse redirectTo(T entity, String path) {
        return redirectTo(entity.getId(), path);
    }
    
    public ServerResponse redirectTo(Integer id, String path) {
        return ok().render(buildRedirectURI(id, path));
    }

    public String buildRedirectURI(Integer id, String path) {
        StringBuilder redirect = new StringBuilder("redirect:");
        if (!path.startsWith("/")) {
            redirect.append("/");
        }
        redirect.append(path);
        if (!path.endsWith("/")) {
            redirect.append("/");
        }
        redirect.append(id);
        return redirect.toString();
    }

    public ServerResponse view(T entity, String name, String view) {
        return ok().render(view, Map.of(name, entity));
    }

    public ServerResponse view(BindingResult results, String view) {
        return ok().render(view, results.getModel());
    }

    public ServletRequestDataBinder binder(@Nullable Object target, String objectName) {
        ServletRequestDataBinder binder = new ServletRequestDataBinder(target, objectName);
        binder.setDisallowedFields("id");
        binder.setValidator(validator);
        binder.setConversionService(conversionService);
        return binder;
    }

}
