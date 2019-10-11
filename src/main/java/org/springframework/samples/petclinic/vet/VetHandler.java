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
package org.springframework.samples.petclinic.vet;

import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.util.Map;

import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Handler functions for the Vet class.
 * @author Cèsar Ordiñana
 */
public class VetHandler {

    private final VetRepository vets;

    public VetHandler(VetRepository vets) {
        this.vets = vets;
    }
    
    public ServerResponse showVetList(ServerRequest request) {
        return ok().render("vets/vetList" , Map.of("vets", allVets()));
    }
    
    public ServerResponse showResourcesVetList(ServerRequest request) {
        return ok().body(allVets());
    }

    private Vets allVets() {
        // Here we are returning an object of type 'Vets' rather than a collection of Vet
        // objects so it is simpler for Object-Xml mapping
        return new Vets(this.vets.findAll());
    }
}
