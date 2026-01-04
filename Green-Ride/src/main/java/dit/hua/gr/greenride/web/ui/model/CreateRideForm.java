package dit.hua.gr.greenride.web.ui.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateRideForm {
    @NotBlank private String origin;
    @NotBlank private String destination;
    @NotNull private LocalDate date;
    @NotNull private LocalTime time;
    @Min(1) @Max(4) private int seatsAvailable;
}