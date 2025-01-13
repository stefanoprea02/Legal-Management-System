package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerCUDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerReadDTO;
import ord.example.LegalManagementSystem.service.LawyerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lawyers")
public class LawyerController {

    private final LawyerService lawyerService;

    public LawyerController(LawyerService lawyerService) {
        this.lawyerService = lawyerService;
    }

    @PostMapping
    public ResponseEntity<LawyerReadDTO> createLawyer(@Valid @RequestBody LawyerCUDTO lawyerCUDTO) {
        LawyerReadDTO savedLawyer = lawyerService.saveLawyer(lawyerCUDTO);
        return new ResponseEntity<>(savedLawyer, HttpStatus.CREATED);
    }

    @GetMapping("/{lawyerId}")
    public ResponseEntity<LawyerReadDTO> getLawyerById(@PathVariable Integer lawyerId) {
        Optional<LawyerReadDTO> lawyer = lawyerService.getLawyerById(lawyerId);
        return lawyer.map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<LawyerReadDTO>> getAllLawyers() {
        List<LawyerReadDTO> lawyers = lawyerService.getLawyers();
        return new ResponseEntity<>(lawyers, HttpStatus.OK);
    }

    @PutMapping("/{lawyerId}")
    public ResponseEntity<LawyerReadDTO> updateLawyer(@PathVariable Integer lawyerId, @Valid @RequestBody LawyerCUDTO lawyerCUDTO) {
        Optional<LawyerReadDTO> updatedLawyer = lawyerService.updateLawyerById(lawyerId, lawyerCUDTO);
        return updatedLawyer.map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{lawyerId}")
    public ResponseEntity<Void> deleteLawyer(@PathVariable Integer lawyerId) {
        try {
            lawyerService.deleteLawyerById(lawyerId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

