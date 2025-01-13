package ord.example.LegalManagementSystem.dtos.LawyerLawsuit;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LawyerLawsuitUpdateDTO {
    @NotNull
    private Integer hoursWorked;
}
