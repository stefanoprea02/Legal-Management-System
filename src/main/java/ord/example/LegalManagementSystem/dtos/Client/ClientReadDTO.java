package ord.example.LegalManagementSystem.dtos.Client;

import lombok.Getter;
import lombok.Setter;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;

import java.util.List;

@Getter
@Setter
public class ClientReadDTO {
    private Integer clientId;
    private String firstName;
    private String lastName;
    private String clientAddress;
    private List<InvoiceReadDTO> invoices;
    private List<LawsuitReadDTO> lawsuits;
}