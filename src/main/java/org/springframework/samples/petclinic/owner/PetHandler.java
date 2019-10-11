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

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Cèsar Ordiñana
 */
class PetHandler {

    private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
    private final PetRepository pets;
    private final OwnerRepository owners;
    private BiFunction<Object, String, ServletRequestDataBinder> binderFactory;

    public PetHandler(PetRepository pets, OwnerRepository owners, 
            BiFunction<Object, String, ServletRequestDataBinder> binderFactory) {
        this.pets = pets;
        this.owners = owners;
        this.binderFactory = binderFactory;
    }

    public ServerResponse initCreationForm(ServerRequest request) {
        return owners.findById(ownerIdParam(request))
                .map(this::createOrUpdatePetView)
                .orElseGet(notFound()::build);
    }

    public ServerResponse processCreationForm(ServerRequest request) {
        return processPetForm(request, 
                pet -> {}, 
                (owner, pet) -> {
                    pet.setOwner(owner);
                    pets.save(pet);
                });
    }

    public ServerResponse initUpdateForm(ServerRequest request) {
        return owners.findById(ownerIdParam(request))
                .map(owner -> {
                    return pets.findById(petIdParam(request))
                            .map(pet -> view(owner, pet, VIEWS_PETS_CREATE_OR_UPDATE_FORM))
                            .orElseGet(notFound()::build);
                })
                .orElseGet(notFound()::build);
    }
    
    public ServerResponse processUpdateForm(ServerRequest request) {
        return processPetForm(request, 
                pet -> pet.setId(petIdParam(request)),
                (owner, pet) -> {
                    pet.setOwner(owner);
                    pets.save(pet);
                });
    }
    
    private ServerResponse processPetForm(ServerRequest request,Consumer<Pet> preparePet, BiConsumer<Owner, Pet> successOperation) {
        Owner owner = owners.findById(ownerIdParam(request)).get();
        ServletRequestDataBinder binder = binderFactory.apply(new Pet(), "pet");
        binder.bind(request.servletRequest());
        binder.addValidators(new PetValidator());
        binder.validate();
        BindingResult result = binder.getBindingResult();
        Pet pet = (Pet) binder.getTarget();
        preparePet.accept(pet);
        
        if (StringUtils.hasLength(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null) {
            result.rejectValue("name", "duplicate", "already exists");
        }
        if (result.hasErrors()) {
            pet.setOwner(owner);
            return view(owner, result, VIEWS_PETS_CREATE_OR_UPDATE_FORM);
        } else {
            successOperation.accept(owner, pet);
            return redirectTo(owner);
        }
    }

    private ServerResponse createOrUpdatePetView(Owner owner) {
        Pet pet = new Pet();
        owner.addPet(pet);
        return view(owner, pet, VIEWS_PETS_CREATE_OR_UPDATE_FORM);
    }

    private ServerResponse view(Owner owner, Pet pet, String view) {
        return ok().render(view, 
                Map.of("owner", owner, "pet", pet, "types", pets.findPetTypes()));
    }

    private int ownerIdParam(ServerRequest request) {
        return Integer.parseInt(request.pathVariable("ownerId"));
    }

    private int petIdParam(ServerRequest request) {
        return Integer.parseInt(request.pathVariable("petId"));
    }

    private ServerResponse view(Owner owner, BindingResult results, String view) {
        Map<String, Object> model = results.getModel();
        model.put("owner", owner);
        model.put("types", pets.findPetTypes());
        return ok().render(view, model);
    }

    private ServerResponse redirectTo(Owner owner) {
        return ok().render("redirect:/owners/" + owner.getId());
    }
}
