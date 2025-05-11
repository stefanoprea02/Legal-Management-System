package ord.example.LegalManagementSystem.dtos.Lawsuit;

import lombok.Getter;
import lombok.Setter;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;

import java.util.List;

@Getter
@Setter
public class LawsuitReadDTO {
    private Integer lawsuitId;
    private ClientReadDTO client;
    private String reason;
    private String opposingParty;
    private HearingReadDTO hearing;
    private List<LawyerLawsuitReadDTO> lawyerLawsuits;
}