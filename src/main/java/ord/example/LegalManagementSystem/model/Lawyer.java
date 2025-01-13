package ord.example.LegalManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Lawyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lawyerId;

    @NotBlank
    @Size(max = 20)
    private String firstName;

    @NotBlank
    @Size(max = 20)
    private String lastName;

    @NotNull
    private Date hireDate;

    @Size(max = 15)
    private String lawyerType;

    @Positive
    private Integer hourlyRate;

    @Positive
    @Max(1)
    private Float commission;

    @Size(max = 100)
    private String lawyerAddress;

    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LawyerLawsuit> lawyerLawsuits;
}