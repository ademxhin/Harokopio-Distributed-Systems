package dit.hua.gr.greenride.service;

import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;

public interface PersonService {

    /**
     * Creates a new Person based on the given request.
     *
     * @param request the data required to create the person
     * @param notify  whether the system should send a notification (e.g. email) after creation
     * @return result containing status and created person view
     */
    CreatePersonResult createPerson(CreatePersonRequest request, boolean notify);
}