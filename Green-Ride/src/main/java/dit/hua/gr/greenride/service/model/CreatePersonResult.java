package dit.hua.gr.greenride.service.model;

/**
 * CreatePersonResult DTO.
 *
 * @see dit.hua.gr.greenride.service.impl.PersonServiceImpl#createPerson(CreatePersonRequest, boolean)
 */
public record CreatePersonResult(
        boolean created,
        String reason,
        PersonView personView
) {

    /**
     * Factory method for a successful result.
     *
     * @param personView the created person view (must not be null)
     * @return a successful CreatePersonResult
     */
    public static CreatePersonResult success(final PersonView personView) {
        if (personView == null) throw new NullPointerException();
        return new CreatePersonResult(true, null, personView);
    }

    /**
     * Factory method for a failed result.
     *
     * @param reason the failure reason (must not be null or blank)
     * @return a failed CreatePersonResult
     */
    public static CreatePersonResult fail(final String reason) {
        if (reason == null) throw new NullPointerException();
        if (reason.isBlank()) throw new IllegalArgumentException();
        return new CreatePersonResult(false, reason, null);
    }
}