package ord.example.LegalManagementSystem.dtos.Hearing;

import lombok.Getter;
import lombok.Setter;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;

import java.sql.Timestamp;

@Getter
@Setter
public class HearingReadDTO {
    private Integer hearingId;
    private LawsuitReadDTO lawsuitEntity;
    private Timestamp dateTime;
    private String appointmentAddress;
}

