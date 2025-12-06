package dit.hua.gr.greenride.service.mapper;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.service.model.PersonView;

import org.springframework.stereotype.Component;

/**
 * Mapper to convert {@link Person} to {@link PersonView}.
 */
@Component
public class PersonMapper {

    public PersonView convertPersonToPersonView(final Person person) {
        if (person == null) {
            return null;
        }

        final PersonView personView = new PersonView(
                person.getUserId(),           // GreenRide public user identifier
                person.getFirstName(),
                person.getLastName(),
                person.getMobilePhoneNumber(),
                person.getEmailAddress(),
                person.getPersonType()
        );

        return personView;
    }
}
