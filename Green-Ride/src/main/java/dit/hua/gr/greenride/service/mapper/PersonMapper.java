package dit.hua.gr.greenride.service.mapper;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.service.model.PersonView;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

    public PersonView convertPersonToPersonView(Person person) {
        if (person == null)
            return null;

        return new PersonView(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getMobilePhoneNumber(),
                person.getEmailAddress(),
                person.getPersonType()
        );
    }
}