package ord.example.LegalManagementSystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table
@Getter
@Setter
public class Lawsuit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lawsuitId;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotBlank
    @Size(max = 50)
    private String reason;

    @NotBlank
    @Size(max = 50)
    private String opposingParty;

    private byte[] lawsuitData;

    @OneToOne(mappedBy = "lawsuit", cascade = CascadeType.ALL)
    private Hearing hearing;

    @OneToMany(mappedBy = "lawsuit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LawyerLawsuit> lawyerLawsuits;
}

