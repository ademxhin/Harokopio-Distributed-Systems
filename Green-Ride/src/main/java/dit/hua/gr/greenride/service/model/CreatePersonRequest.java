package dit.hua.gr.greenride.service.model;

public record CreatePersonRequest(String firstName, String lastName, String mobilePhoneNumber, String emailAddress, String rawPassword) {

}