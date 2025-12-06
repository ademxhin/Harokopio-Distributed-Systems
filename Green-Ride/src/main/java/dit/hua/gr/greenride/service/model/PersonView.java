package dit.hua.gr.greenride.service.model;

import dit.hua.gr.greenride.core.model.PersonType;

/**
 * Public view model for the Person entity.
 */
public record PersonView(
        String userId,
        String firstName,
        String lastName,
        String mobilePhoneNumber,
        String emailAddress,
        PersonType type
) {

    /**
     * @return the full name of the person (first + last name)
     */
    public String fullName() {
        return this.firstName + " " + this.lastName;
    }
}
