package dit.hua.gr.greenride.service.model;

public record CreatePersonRequest(dit.hua.gr.greenride.core.model.PersonType user, String firstName, String lastName, String mobilePhoneNumber, String emailAddress, String rawPassword,
                                  String s) {

}