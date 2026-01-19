package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.model.PersonDataService;
import dit.hua.gr.greenride.service.mapper.PersonMapper;

import org.springframework.stereotype.Service;

@Service
public class PersonDataServiceImpl implements PersonDataService {

    public PersonDataServiceImpl(final PersonRepository personRepository,
                                 final PersonMapper personMapper) {
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (personMapper == null) throw new NullPointerException("personMapper is null");
    }
}