package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerCUDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
import ord.example.LegalManagementSystem.exceptions.LawyerNotFoundException;
import ord.example.LegalManagementSystem.service.LawsuitService;
import ord.example.LegalManagementSystem.service.LawyerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lawyers")
public class LawyerController {
    private final LawyerService lawyerService;
    private final LawsuitService lawsuitService;

    public LawyerController(LawyerService lawyerService, LawsuitService lawsuitService) {
        this.lawyerService = lawyerService;
        this.lawsuitService = lawsuitService;
    }

    @GetMapping("/create")
    public String createLawyerForm(Model model) {
        model.addAttribute("lawyer", new LawyerCUDTO());
        model.addAttribute("lawsuits", lawsuitService.getLawsuits());
        model.addAttribute("formAction", "/lawyers");
        return "lawyer/form";
    }

    @PostMapping
    public String createLawyer(@Valid @ModelAttribute("lawyer") LawyerCUDTO lawyerCUDTO,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "lawyer/form";
        }

        LawyerReadDTO savedLawyer = lawyerService.saveLawyer(lawyerCUDTO);

        return "redirect:/lawyers/" + savedLawyer.getLawyerId();
    }

    @GetMapping("/{lawyerId}")
    public String getLawyerById(@PathVariable Integer lawyerId, Model model) {
        Optional<LawyerReadDTO> lawyer = lawyerService.getLawyerById(lawyerId);

        if (lawyer.isEmpty()) {
            throw new LawyerNotFoundException("Lawyer with ID " + lawyerId + " not found");
        } else {
            model.addAttribute("lawyer", lawyer.get());
            return "lawyer/lawyer";
        }
    }

    @GetMapping
    public String getAllLawyers(Model model) {
        List<LawyerReadDTO> lawyers = lawyerService.getLawyers();

        model.addAttribute("lawyers", lawyers);
        return "lawyer/lawyers";
    }

    @GetMapping("/edit/{lawyerId}")
    public String editLawyerForm(@PathVariable Integer lawyerId, Model model) {
        Optional<LawyerReadDTO> lawyerOptional = lawyerService.getLawyerById(lawyerId);

        if (lawyerOptional.isEmpty()) {
            throw new LawyerNotFoundException("Lawyer with ID " + lawyerId + " not found");
        } else {
            LawyerReadDTO readDTO = lawyerOptional.get();
            LawyerCUDTO updateDTO = new LawyerCUDTO();
            updateDTO.setFirstName(readDTO.getFirstName());
            updateDTO.setLastName(readDTO.getLastName());
            updateDTO.setHireDate(readDTO.getHireDate());
            updateDTO.setLawyerType(readDTO.getLawyerType());
            updateDTO.setHourlyRate(readDTO.getHourlyRate());
            updateDTO.setCommission(readDTO.getCommission());
            updateDTO.setLawyerAddress(readDTO.getLawyerAddress());
            updateDTO.setLawsuitIds(new ArrayList<>(readDTO.getLawyerLawsuits().stream()
                    .map(LawyerLawsuitReadDTO::getLawsuit)
                    .map(LawsuitReadDTO::getLawsuitId)
                    .collect(Collectors.toSet())));

            model.addAttribute("lawyer", updateDTO);
            model.addAttribute("lawsuits", lawsuitService.getLawsuits());
            model.addAttribute("formAction", "/lawyers/" + lawyerId);
            return "lawyer/form";
        }
    }

    @PostMapping("/{lawyerId}")
    public String updateLawyer(@PathVariable Integer lawyerId,
                               @Valid @ModelAttribute("lawyer") LawyerCUDTO lawyerCUDTO,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "lawyer/form";
        }

        Optional<LawyerReadDTO> updatedLawyer = lawyerService.updateLawyerById(lawyerId, lawyerCUDTO);

        if (updatedLawyer.isEmpty()) {
            throw new LawyerNotFoundException("Lawyer with ID " + lawyerId + " not found");
        } else {
            return "redirect:/lawyers/" + updatedLawyer.get().getLawyerId();
        }
    }

    @PostMapping("/delete/{lawyerId}")
    public String deleteLawyer(@PathVariable Integer lawyerId) {
        Optional<LawyerReadDTO> lawyerOptional = lawyerService.getLawyerById(lawyerId);

        if (lawyerOptional.isEmpty()) {
            throw new LawyerNotFoundException("Lawyer with ID " + lawyerId + " not found");
        } else {
            lawyerService.deleteLawyerById(lawyerId);
            return "redirect:/lawyers";
        }
    }
}

