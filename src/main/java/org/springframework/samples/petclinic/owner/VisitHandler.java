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

import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.samples.petclinic.support.ServerResponseSupport;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Cèsar Ordiñana
 */
class VisitHandler {

    private final VisitRepository visits;
    private final PetRepository pets;
    private final ServerResponseSupport<Pet> support;

    public VisitHandler(VisitRepository visits, PetRepository pets, Validator validator,
            ConversionService conversionService) {
        this.visits = visits;
        this.pets = pets;
        this.support = new ServerResponseSupport<>(validator, conversionService);
    }

    public ServerResponse initNewVisitForm(ServerRequest request) {
        Pet pet = findPetWithVisits(request);
        Visit visit = new Visit();
        pet.addVisit(visit);

        return view(pet, visit, "pets/createOrUpdateVisitForm");
    }

    public ServerResponse processNewVisitForm(ServerRequest request) {
        Pet pet = findPetWithVisits(request);
        Visit visit = new Visit();
        pet.addVisit(visit);

        ServletRequestDataBinder binder = support.binder(visit, "visit");
        binder.bind(request.servletRequest());
        binder.validate();
        BindingResult result = binder.getBindingResult();
        visit = (Visit)result.getTarget();

        if (result.hasErrors()) {
            return view(pet, result, "pets/createOrUpdateVisitForm");
        } else {
            this.visits.save(visit);
            return support.redirectTo(ownerIdParam(request), "/owners");
        }
    }

    private Pet findPetWithVisits(ServerRequest request) {
        Integer petId = petIdParam(request);
        Pet pet = this.pets.findById(petId).get();
        pet.setVisitsInternal(this.visits.findByPetId(petId));
        return pet;
    }

    private Integer petIdParam(ServerRequest request) {
        return Integer.parseInt(request.pathVariable("petId"));
    }

    private int ownerIdParam(ServerRequest request) {
        return Integer.parseInt(request.pathVariable("ownerId"));
    }

    private ServerResponse view(Pet pet, Visit visit, String view) {
        return ok().render(view, Map.of("pet", pet, "visit", visit));
    }

    private ServerResponse view(Pet pet, BindingResult results, String view) {
        Map<String, Object> model = results.getModel();
        model.put("pet", pet);
        return ok().render(view, model);
    }
}
