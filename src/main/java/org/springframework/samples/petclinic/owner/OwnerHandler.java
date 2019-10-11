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

import static org.springframework.web.servlet.function.ServerResponse.notFound;
import static org.springframework.web.servlet.function.ServerResponse.ok;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.springframework.samples.petclinic.support.ServerResponseSupport;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Handler functions for the Owner class.
 * 
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Cèsar Ordiñana
 */
class OwnerHandler {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
    private final OwnerRepository owners;
    private final VisitRepository visits;
    private final BiFunction<Object, String, ServletRequestDataBinder> binderFactory;
    private final ServerResponseSupport<Owner> support;

    public OwnerHandler(OwnerRepository clinicService, VisitRepository visits, 
            BiFunction<Object, String, ServletRequestDataBinder> binderFactory) {
        this.owners = clinicService;
        this.visits = visits;
        this.binderFactory = binderFactory;
        this.support = new ServerResponseSupport<>("owner");
    }

    public ServerResponse initCreationForm(ServerRequest request) {
        return support.view(new Owner(), VIEWS_OWNER_CREATE_OR_UPDATE_FORM);
    }

    public ServerResponse processCreationForm(ServerRequest request) {
        return processOwnerForm(request, owners::save);
    }

    public ServerResponse processUpdateForm(ServerRequest request) {
        return processOwnerForm(request, owner -> {
            int ownerId = ownerIdParam(request);
            owner.setId(ownerId);
            owners.save(owner);
        });
    }

    private ServerResponse processOwnerForm(ServerRequest request, Consumer<Owner> operation) {
        ServletRequestDataBinder binder = binderFactory.apply(new Owner(), "owner");
        binder.bind(request.servletRequest());
        binder.validate();
        BindingResult result = binder.getBindingResult();
        if (result.hasErrors()) {
            return support.view(result, VIEWS_OWNER_CREATE_OR_UPDATE_FORM);
        } else {
            Owner owner = (Owner) binder.getTarget();
            operation.accept(owner);
            return support.redirectTo(owner, "/owners");
        }
    }

    public ServerResponse initFindForm(ServerRequest request) {
        return support.view(new Owner(), "owners/findOwners");
    }

    public ServerResponse processFindForm(ServerRequest request) {
        ServletRequestDataBinder binder = binderFactory.apply(new Owner(), "owner");
        binder.bind(request.servletRequest());
        Owner owner = (Owner) binder.getTarget();

        // allow parameterless GET request for /owners to return all records
        if (owner.getLastName() == null) {
            owner.setLastName(""); // empty string signifies broadest possible search
        }

        // find owners by last name
        Collection<Owner> results = this.owners.findByLastName(owner.getLastName());
        if (results.isEmpty()) {
            // no owners found
            BindingResult result = binder.getBindingResult();
            result.rejectValue("lastName", "notFound", "not found");
            return support.view(result, "owners/findOwners");
        } else if (results.size() == 1) {
            // 1 owner found
            return support.redirectTo(results.iterator().next(), "/owners");
        } else {
            // multiple owners found
            return ok().render("owners/ownersList", Map.of("selections", results));
        }
    }

    public ServerResponse initUpdateOwnerForm(ServerRequest request) {
        return owners.findById(ownerIdParam(request))
                .map(owner -> support.view(owner, VIEWS_OWNER_CREATE_OR_UPDATE_FORM))
                .orElseGet(notFound()::build);
    }

    /**
     * Custom handler for displaying an owner.
     *
     * @param ownerId the ID of the owner to display
     * @return a ModelMap with the model attributes for the view
     */
    public ServerResponse showOwner(ServerRequest request) {
        return owners.findById(ownerIdParam(request))
                .map(this::loadOwnerPetVisits)
                .map(owner -> support.view(owner, "owners/ownerDetails"))
                .orElseGet(notFound()::build);
    }

    private int ownerIdParam(ServerRequest request) {
        return Integer.parseInt(request.pathVariable("ownerId"));
    }

    private Owner loadOwnerPetVisits(Owner owner) {
        owner.getPets().stream().forEach(this::loadPetVisits);
        return owner;
    }

    private void loadPetVisits(Pet pet) {
        pet.setVisitsInternal(visits.findByPetId(pet.getId()));
    }
}
