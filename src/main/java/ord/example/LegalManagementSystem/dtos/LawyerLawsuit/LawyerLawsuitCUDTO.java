package ord.example.LegalManagementSystem.dtos.LawyerLawsuit;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LawyerLawsuitCUDTO {
    @NotNull
    Integer lawyerId;

    @NotNull
    Integer lawsuitId;

    @NotNull
    private Integer hoursWorked;
}
