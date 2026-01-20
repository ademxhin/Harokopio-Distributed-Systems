package dit.hua.gr.greenride.service;

import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;

public interface PersonService {
    CreatePersonResult createPerson(CreatePersonRequest request, boolean notify);
}