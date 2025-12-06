package dit.hua.gr.greenride.service.model;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO used for searching available rides.
 */
public record SearchRideRequest(

        @NotBlank
        String startLocation,

        @NotBlank
        String endLocation
) {
}
