package ord.example.LegalManagementSystem.dtos.Hearing;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class HearingCreateDTO {
    @NotNull
    private Integer lawsuitId;

    @NotNull
    private Timestamp dateTime;

    @Size(max = 100)
    private String appointmentAddress;
}

