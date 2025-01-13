package ord.example.LegalManagementSystem.dtos.LawyerLawsuit;

import lombok.Getter;
import lombok.Setter;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerReadDTO;

@Getter
@Setter
public class LawyerLawsuitReadDTO {
    private Integer id;
    private Integer hoursWorked;
    private LawyerReadDTO lawyer;
    private LawsuitReadDTO lawsuitEntity;
}