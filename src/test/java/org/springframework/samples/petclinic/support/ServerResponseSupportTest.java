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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Unit tests for the {@link ServerResponseSupport} class
 * 
 * @author Cèsar Ordiñana
 */
class ServerResponseSupportTest {
    
    private static ServerResponseSupport<SimpleEntity> support;
    
    @BeforeAll
    static void prepare() {
        support = new ServerResponseSupport<>("entity");
    }
    
    @Test
    void redirectCreatesValidRedirectURI() {
        SimpleEntity entity = new SimpleEntity(1);
        String[] paths = {"/entity", "entity/", "/entity/", "entity"};
        Stream.of(paths).forEach(
            path -> assertThat(support.buildRedirectURI(entity, path)).isEqualTo("redirect:/entity/1") 
        );
    }
    
    @Test
    void viewEntity() {
        SimpleEntity entity = new SimpleEntity(1);
        ServerResponse view = support.view(entity, "entityView");
        assertThat(view.statusCode()).isEqualTo(HttpStatus.OK);
        ServerResponse viewOther = support.view(entity, "entityOther", "entityView");
        assertThat(viewOther.statusCode()).isEqualTo(HttpStatus.OK);
    }

    private class SimpleEntity extends BaseEntity {
        public SimpleEntity(Integer id) {
            setId(id);
        }
    }
    
}
