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

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Support methods for easier creation of {@link ServerResponse} values.
 * 
 * @author Cèsar Ordiñana
 */
public class ServerResponseSupport<T extends BaseEntity> {

    private final String defaultEntityModelName;

    public ServerResponseSupport(String defaultEntityModelName) {
        this.defaultEntityModelName = defaultEntityModelName;
    }

    public String buildRedirectURI(T entity, String path) {
        StringBuilder redirect = new StringBuilder("redirect:");
        if (!path.startsWith("/")) {
            redirect.append("/");
        }
        redirect.append(path);
        if (!path.endsWith("/")) {
            redirect.append("/");
        }
        redirect.append(entity.getId());
        return redirect.toString();
    }

    public ServerResponse redirectTo(T entity, String path) {
        return ok().render(buildRedirectURI(entity, path));
    }

    public ServerResponse view(T entity, String view) {
        return view(entity, defaultEntityModelName, view);
    }

    public ServerResponse view(T entity, String name, String view) {
        return ok().render(view, Map.of(name, entity));
    }

    public ServerResponse view(BindingResult results, String view) {
        return ok().render(view, results.getModel());
    }

}
