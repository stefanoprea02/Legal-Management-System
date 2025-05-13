package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitUpdateDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
import ord.example.LegalManagementSystem.service.ClientService;
import ord.example.LegalManagementSystem.service.LawsuitService;
import ord.example.LegalManagementSystem.service.LawyerService;
import org.springframework.data.domain.Page;
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
@RequestMapping("/lawsuits")
public class LawsuitController {
    private final LawsuitService lawsuitService;
    private final ClientService clientService;
    private final LawyerService lawyerService;

    public LawsuitController(LawsuitService lawsuitService, ClientService clientService, LawyerService lawyerService) {
        this.lawsuitService = lawsuitService;
        this.clientService = clientService;
        this.lawyerService = lawyerService;
    }

    @GetMapping("/create")
    public String createLawsuitForm(Model model) {
        LawsuitCreateDTO lawsuitCreateDTO = new LawsuitCreateDTO();
        model.addAttribute("lawsuit", lawsuitCreateDTO);
        model.addAttribute("clients", clientService.getClients());
        model.addAttribute("lawyers", lawyerService.getLawyers());
        model.addAttribute("formAction", "/lawsuits");
        return "lawsuit/form";
    }

    @PostMapping
    public String createLawsuit(@Valid @ModelAttribute("lawsuit") LawsuitCreateDTO lawsuitCreateDTO,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getClients());
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("formAction", "/lawsuits");
            return "lawsuit/form";
        }

        LawsuitReadDTO savedlawsuit = lawsuitService.saveLawsuite(lawsuitCreateDTO);

        return "redirect:/lawsuits/" + savedlawsuit.getLawsuitId();
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

    @GetMapping
    public String getLawsuits(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "lawsuitId") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            Model model) {
        Page<LawsuitReadDTO> lawsuits = lawsuitService.getLawsuitsPage(page, sortField, sortOrder);

        model.addAttribute("lawsuits", lawsuits);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "lawsuit/lawsuits";
    }

    @GetMapping("/edit/{lawsuitId}")
    public String getEditForm(@PathVariable Integer lawsuitId, Model model) {
        Optional<LawsuitReadDTO> lawsuitOptional = lawsuitService.getLawsuitById(lawsuitId);

        if (lawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        } else {
            LawsuitReadDTO readDTO = lawsuitOptional.get();
            LawsuitUpdateDTO updateDTO = new LawsuitUpdateDTO();
            updateDTO.setReason(readDTO.getReason());
            updateDTO.setOpposingParty(readDTO.getOpposingParty());
            updateDTO.setLawyerIds(new ArrayList<>(readDTO.getLawyerLawsuits().stream()
                    .map(LawyerLawsuitReadDTO::getLawyer)
                    .map(LawyerReadDTO::getLawyerId)
                    .collect(Collectors.toSet())));

            model.addAttribute("lawsuit", updateDTO);
            model.addAttribute("clients", clientService.getClients());
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("formAction", "/lawsuits/" + lawsuitId);
            return "lawsuit/form";
        }
    }

    @PostMapping("/{lawsuitId}")
    public String updateLawsuit(@PathVariable Integer lawsuitId,
                                @Valid @ModelAttribute("lawsuit") LawsuitUpdateDTO lawsuitUpdateDTO,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getClients());
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("formAction", "/lawsuits/" + lawsuitId);
            return "lawsuit/form";
        }

        Optional<LawsuitReadDTO> updatedLawsuitOptional = lawsuitService.updateLawsuitById(lawsuitId, lawsuitUpdateDTO);

        if (updatedLawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        } else {
            return "redirect:/lawsuits/" + updatedLawsuitOptional.get().getLawsuitId();
        }
    }

    @PostMapping("/delete/{lawsuitId}")
    public String deleteLawsuit(@PathVariable Integer lawsuitId) {
        Optional<LawsuitReadDTO> lawsuitOptional = lawsuitService.getLawsuitById(lawsuitId);

        if (lawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        } else {
            lawsuitService.deleteLawsuitById(lawsuitId);
            return "redirect:/lawsuits";
        }
    }
}
