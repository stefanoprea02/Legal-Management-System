package ord.example.LegalManagementSystem.controller;

import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitUpdateDTO;
import ord.example.LegalManagementSystem.service.LawsuitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lawsuits")
public class LawsuitController {
    private final LawsuitService lawsuitService;

    public LawsuitController(LawsuitService lawsuitService) {
        this.lawsuitService = lawsuitService;
    }

    @PostMapping
    public ResponseEntity<LawsuitReadDTO> createLawsuit(@RequestBody LawsuitCreateDTO lawsuitCreateDTO) {
        LawsuitReadDTO savedlawsuit = lawsuitService.saveLawsuite(lawsuitCreateDTO);
        return new ResponseEntity<>(savedlawsuit, HttpStatus.CREATED);
    }

    @GetMapping("/{lawsuitId}")
    public ResponseEntity<LawsuitReadDTO> getLawsuitById(@PathVariable Integer lawsuitId) {
        Optional<LawsuitReadDTO> lawsuitOptional = lawsuitService.getLawsuitById(lawsuitId);
        return lawsuitOptional.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("")
    public ResponseEntity<List<LawsuitReadDTO>> getLawsuits() {
        List<LawsuitReadDTO> lawsuits = lawsuitService.getLawsuits();
        return new ResponseEntity<>(lawsuits, HttpStatus.OK);
    }

    @PutMapping("/{lawsuitId}")
    public ResponseEntity<LawsuitReadDTO> updateLawsuit(@PathVariable Integer lawsuitId, @RequestBody LawsuitUpdateDTO lawsuitUpdateDTO) {
        Optional<LawsuitReadDTO> updatedLawsuitOptional = lawsuitService.updateLawsuitById(lawsuitId, lawsuitUpdateDTO);
        return updatedLawsuitOptional.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{lawsuitId}")
    public ResponseEntity<Void> deleteLawsuit(@PathVariable Integer lawsuitId) {
        try {
            lawsuitService.deleteLawsuitById(lawsuitId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
