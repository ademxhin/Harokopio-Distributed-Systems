package dit.hua.gr.greenride.service.model;

import dit.hua.gr.greenride.core.model.PersonType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PersonView (
        @Schema(description = "Unique identifier of the person", example = "1")
        long id,
        String firstName,
        String lastName,
        String mobilePhoneNumber,
        String emailAddress,
        PersonType personType
){
}