package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.service.PersonService;
import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    @Override
    public CreatePersonResult createPerson(CreatePersonRequest request, boolean notify) {
        // TODO your logic
        return new CreatePersonResult();
    }
}
