package ord.example.LegalManagementSystem.dtos.Invoice;

import lombok.Getter;
import lombok.Setter;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;

import java.sql.Date;

@Getter
@Setter
public class InvoiceReadDTO {
    private Integer invoiceId;
    private ClientReadDTO client;
    private Integer amount;
    private Date dueDate;
}