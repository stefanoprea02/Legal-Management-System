package ord.example.LegalManagementSystem.dtos.Lawyer;

import lombok.Getter;
import lombok.Setter;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class LawyerReadDTO {
    private Integer lawyerId;
    private String firstName;
    private String lastName;
    private Date hireDate;
    private String lawyerType;
    private Integer hourlyRate;
    private Float commission;
    private String lawyerAddress;
    private List<LawyerLawsuitReadDTO> lawyerLawsuits;
}