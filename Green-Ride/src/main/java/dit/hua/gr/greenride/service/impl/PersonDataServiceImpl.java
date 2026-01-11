package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.model.PersonDataService;
import dit.hua.gr.greenride.service.mapper.PersonMapper;
import dit.hua.gr.greenride.service.model.PersonView;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonDataServiceImpl implements PersonDataService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonDataServiceImpl(final PersonRepository personRepository,
                                 final PersonMapper personMapper) {
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        if (personMapper == null) throw new NullPointerException("personMapper is null");

        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    @Override
    public List<PersonView> getAllPeople() {
        return this.personRepository.findAll()
                .stream()
                .map(this.personMapper::convertPersonToPersonView)
                .toList();
    }
}