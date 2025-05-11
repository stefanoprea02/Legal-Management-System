package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.LawyerLawsuitNotFoundException;
import ord.example.LegalManagementSystem.service.LawyerLawsuitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/lawyer-lawsuits")
public class LawyerLawsuitController {

    private final LawyerLawsuitService lawyerLawsuitService;

    public LawyerLawsuitController(LawyerLawsuitService lawyerLawsuitService) {
        this.lawyerLawsuitService = lawyerLawsuitService;
    }

    @PostMapping
    public ResponseEntity<LawyerLawsuitReadDTO> createLawyerLawsuit(@Valid @RequestBody LawyerLawsuitCreateDTO lawyerLawsuitCreateDTO) {
        LawyerLawsuitReadDTO savedLawyerLawsuit = lawyerLawsuitService.createLawyerLawsuit(lawyerLawsuitCreateDTO);
        return new ResponseEntity<>(savedLawyerLawsuit, HttpStatus.CREATED);
    }

    @GetMapping("/{lawyerLawsuitId}")
    public String getLawyerLawsuitById(@PathVariable Integer lawyerLawsuitId, Model model) {
        Optional<LawyerLawsuitReadDTO> lawyerLawsuit = lawyerLawsuitService.getLawyerLawsuitById(lawyerLawsuitId);

        if (lawyerLawsuit.isEmpty()) {
            throw new LawyerLawsuitNotFoundException("LawyerLawsuit with ID " + lawyerLawsuitId + " not found");
        } else {
            model.addAttribute("lawyerLawsuit", lawyerLawsuit.get());
            return "lawyerLawsuit/lawyerLawsuit";
        }
    }

    @GetMapping
    public String getLawyerLawsuits(Model model) {
        List<LawyerLawsuitReadDTO> lawyerLawsuits = lawyerLawsuitService.getAllLawyerLawsuits();

        model.addAttribute("lawyerLawsuits", lawyerLawsuits);
        return "lawyerLawsuit/lawyerLawsuits";
    }

    @GetMapping("/lawsuit/{lawsuitId}")
    public ResponseEntity<List<LawyerLawsuitReadDTO>> getLawyerLawsuitsByLawsuitId(@PathVariable Integer lawsuitId) {
        List<LawyerLawsuitReadDTO> lawyerLawsuits = lawyerLawsuitService.getLawyerLawsuitsByLawsuitId(lawsuitId);
        return new ResponseEntity<>(lawyerLawsuits, HttpStatus.OK);
    }

    @PutMapping("/{lawyerLawsuitId}")
    public ResponseEntity<LawyerLawsuitReadDTO> updateLawyerLawsuit(@PathVariable Integer lawyerLawsuitId, @Valid @RequestBody LawyerLawsuitUpdateDTO lawyerLawsuitUpdateDTO) {
        Optional<LawyerLawsuitReadDTO> updatedLawyerLawsuit = lawyerLawsuitService.updateLawyerLawsuitById(lawyerLawsuitId, lawyerLawsuitUpdateDTO);
        return updatedLawyerLawsuit.map(lc -> new ResponseEntity<>(lc, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{lawyerLawsuitId}")
    public ResponseEntity<Void> deleteLawyerLawsuit(@PathVariable Integer lawyerLawsuitId) {
        try {
            lawyerLawsuitService.deleteLawyerLawsuit(lawyerLawsuitId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
