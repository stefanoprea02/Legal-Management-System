package ord.example.LegalManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "hearing")
@Getter
@Setter
public class Hearing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hearingId;

    @OneToOne
    @JoinColumn(name = "lawsuit_id", nullable = false)
    private Lawsuit lawsuit;

    @NotNull
    private Timestamp dateTime;

    @Size(max = 100)
    private String appointmentAddress;
}
