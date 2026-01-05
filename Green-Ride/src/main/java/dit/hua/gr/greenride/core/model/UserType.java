package dit.hua.gr.greenride.core.model;

/**
 * Business usage mode for a USER.
 * - PASSENGER: can book/request rides
 * - DRIVER: can offer/manage rides
 * - BOTH: passenger + driver
 *
 * For ADMIN users this can be null (admins don't use passenger/driver flows).
 */
public enum UserType {
    PASSENGER,
    DRIVER,
    BOTH;

    public boolean isPassenger() {
        return this == PASSENGER || this == BOTH;
    }

    public boolean isDriver() {
        return this == DRIVER || this == BOTH;
    }
}