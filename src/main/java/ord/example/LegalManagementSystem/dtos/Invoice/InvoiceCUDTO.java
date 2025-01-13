package ord.example.LegalManagementSystem.dtos.Invoice;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class InvoiceCUDTO {
    @NotNull
    private Integer clientId;

    @Positive
    private Integer amount;

    @NotNull
    private Date dueDate;
}
