package dit.hua.gr.greenride.web.ui.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateRideForm {

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // expects yyyy-MM-dd
    private LocalDate date;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm") // expects 24h time like 00:30 or 14:15
    private LocalTime time;

    @Min(1)
    @Max(4)
    private int seatsAvailable;
}
