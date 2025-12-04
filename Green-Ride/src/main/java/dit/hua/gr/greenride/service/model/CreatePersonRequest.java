package dit.hua.gr.greenride.service.model;

import dit.hua.gr.greenride.core.model.PersonType;

public record CreatePersonRequest (PersonType type, String firstName, String lastName, String mobilePhoneNumber, String emailAddress, String rawPassword){

}