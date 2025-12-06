package dit.hua.gr.greenride.service.model;

/**
 * Result DTO for ride creation.
 */
public record CreateRideResult(
        boolean created,
        String reason,
        RideView rideView
) {

    /**
     * Factory method for a successful ride creation.
     *
     * @param rideView the created ride view (must not be null)
     * @return a successful CreateRideResult
     */
    public static CreateRideResult success(final RideView rideView) {
        if (rideView == null) {
            throw new NullPointerException();
        }
        return new CreateRideResult(true, null, rideView);
    }

    /**
     * Factory method for a failed ride creation.
     *
     * @param reason the failure reason (must not be null or blank)
     * @return a failed CreateRideResult
     */
    public static CreateRideResult fail(final String reason) {
        if (reason == null) {
            throw new NullPointerException();
        }
        if (reason.isBlank()) {
            throw new IllegalArgumentException();
        }
        return new CreateRideResult(false, reason, null);
    }
}
