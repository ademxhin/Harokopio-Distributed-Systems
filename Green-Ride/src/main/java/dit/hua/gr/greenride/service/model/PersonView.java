package dit.hua.gr.greenride.service.model;

import dit.hua.gr.greenride.core.model.PersonType;

public record PersonView (long id, String firstName, String lastName, String mobilePhoneNumber, String emailAddress, PersonType personType){

}