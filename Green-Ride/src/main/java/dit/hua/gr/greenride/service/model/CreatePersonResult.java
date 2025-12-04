package dit.hua.gr.greenride.service.model;

public record CreatePersonResult(
        boolean created,
        PersonView personView
){
    public static CreatePersonResult success(final PersonView personView) {
        if(personView == null)
            throw new NullPointerException("personView is null");

        return new CreatePersonResult(true, personView);
    }
}