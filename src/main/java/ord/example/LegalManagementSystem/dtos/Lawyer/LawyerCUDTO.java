package ord.example.LegalManagementSystem.dtos.Lawyer;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class LawyerCUDTO {
    @NotBlank
    @Size(max = 20)
    private String firstName;

    @NotBlank
    @Size(max = 20)
    private String lastName;

    @NotNull
    private Date hireDate;

    @Size(max = 15)
    private String lawyerType;

    @Positive
    private Integer hourlyRate;

    @Positive
    @Max(1)
    private Float commission;

    @Size(max = 100)
    private String lawyerAddress;

    private List<Integer> lawsuitIds;
}
