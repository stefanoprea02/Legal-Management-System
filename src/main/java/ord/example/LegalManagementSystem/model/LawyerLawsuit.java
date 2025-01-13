package ord.example.LegalManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
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
    private Lawsuit lawsuitEntity;
}