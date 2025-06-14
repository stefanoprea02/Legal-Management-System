package ord.example.LegalManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lawyer_lawsuit")
@Getter
@Setter
public class LawyerLawsuit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer hoursWorked;

    @ManyToOne
    @JoinColumn(name = "lawyer_id", nullable = false)
    private Lawyer lawyer;

    @ManyToOne
    @JoinColumn(name = "lawsuit_id", nullable = false)
    private Lawsuit lawsuit;
}