package ord.example.LegalManagementSystem.dtos.Lawsuit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LawsuitCreateDTO {
    @NotNull
    private Integer clientId;

    @NotBlank
    @Size(max = 50)
    private String reason;

    @NotBlank
    @Size(max = 50)
    private String opposingParty;

    private List<Integer> lawyerIds;
}
