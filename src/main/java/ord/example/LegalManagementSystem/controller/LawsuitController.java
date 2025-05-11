package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
import ord.example.LegalManagementSystem.service.LawsuitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/lawsuits")
public class LawsuitController {
    private final LawsuitService lawsuitService;

    public LawsuitController(LawsuitService lawsuitService) {
        this.lawsuitService = lawsuitService;
    }

    @PostMapping
    public ResponseEntity<LawsuitReadDTO> createLawsuit(@Valid @RequestBody LawsuitCreateDTO lawsuitCreateDTO) {
        LawsuitReadDTO savedlawsuit = lawsuitService.saveLawsuite(lawsuitCreateDTO);
        return new ResponseEntity<>(savedlawsuit, HttpStatus.CREATED);
    }

    @GetMapping("/{lawsuitId}")
    public String getLawsuitById(@PathVariable Integer lawsuitId, Model model) {
        Optional<LawsuitReadDTO> lawsuitOptional = lawsuitService.getLawsuitById(lawsuitId);

        if (lawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        } else {
            model.addAttribute("lawsuit", lawsuitOptional.get());
            return "lawsuit/lawsuit";
        }
    }

    @GetMapping("")
    public String getLawsuits(Model model) {
        List<LawsuitReadDTO> lawsuits = lawsuitService.getLawsuits();

        model.addAttribute("lawsuits", lawsuits);
        return "lawsuit/lawsuits";
    }

    @PutMapping("/{lawsuitId}")
    public ResponseEntity<LawsuitReadDTO> updateLawsuit(@PathVariable Integer lawsuitId, @Valid @RequestBody LawsuitUpdateDTO lawsuitUpdateDTO) {
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
