package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.LawyerLawsuitNotFoundException;
import ord.example.LegalManagementSystem.service.LawsuitService;
import ord.example.LegalManagementSystem.service.LawyerLawsuitService;
import ord.example.LegalManagementSystem.service.LawyerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/lawyer-lawsuits")
public class LawyerLawsuitController {
    private final LawyerLawsuitService lawyerLawsuitService;
    private final LawyerService lawyerService;
    private final LawsuitService lawsuitService;

    public LawyerLawsuitController(LawyerLawsuitService lawyerLawsuitService, LawyerService lawyerService, LawsuitService lawsuitService) {
        this.lawyerLawsuitService = lawyerLawsuitService;
        this.lawyerService = lawyerService;
        this.lawsuitService = lawsuitService;
    }

    @GetMapping("/create")
    public String showCreateLawyerLawsuitForm(Model model) {
        model.addAttribute("lawyerLawsuit", new LawyerLawsuitCreateDTO());
        model.addAttribute("lawyers", lawyerService.getLawyers());
        model.addAttribute("lawsuits", lawsuitService.getLawsuits());
        model.addAttribute("formAction", "/lawyer-lawsuits");
        return "lawyerLawsuit/form";
    }

    @PostMapping
    public String createLawyerLawsuit(@Valid @ModelAttribute("lawyerLawsuit") LawyerLawsuitCreateDTO lawyerLawsuitCreateDTO,
                                      BindingResult bindingResult,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("lawsuits", lawsuitService.getLawsuits());
            model.addAttribute("formAction", "/lawyer-lawsuits");
            return "lawyerLawsuit/form";
        }

        LawyerLawsuitReadDTO savedLawyerLawsuit = lawyerLawsuitService.createLawyerLawsuit(lawyerLawsuitCreateDTO);

        return "redirect:/lawyer-lawsuits/" + savedLawyerLawsuit.getId();
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
    public String getLawyerLawsuits(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            Model model) {
        Page<LawyerLawsuitReadDTO> lawyerLawsuits = lawyerLawsuitService.getAllLawyerLawsuitsPage(page, sortField, sortOrder);

        model.addAttribute("lawyerLawsuits", lawyerLawsuits);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "lawyerLawsuit/lawyerLawsuits";
    }

    @GetMapping("/edit/{lawyerLawsuitId}")
    public String showEditLawyerLawsuitForm(@PathVariable Integer lawyerLawsuitId, Model model) {
        Optional<LawyerLawsuitReadDTO> lawyerLawsuit = lawyerLawsuitService.getLawyerLawsuitById(lawyerLawsuitId);

        if (lawyerLawsuit.isEmpty()) {
            throw new LawyerLawsuitNotFoundException("LawyerLawsuit with ID " + lawyerLawsuitId + " not found");
        } else {
            model.addAttribute("lawyerLawsuit", lawyerLawsuit.get());
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("lawsuits", lawsuitService.getLawsuits());
            model.addAttribute("formAction", "/lawyer-lawsuits/" + lawyerLawsuitId);
            return "lawyerLawsuit/form";
        }
    }

    @PostMapping("/{lawyerLawsuitId}")
    public String updateLawyerLawsuit(@PathVariable Integer lawyerLawsuitId,
                                      @Valid @ModelAttribute("lawyerLawsuit") LawyerLawsuitUpdateDTO lawyerLawsuitUpdateDTO,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "lawyerLawsuit/form";
        }

        Optional<LawyerLawsuitReadDTO> updatedLawyerLawsuit = lawyerLawsuitService.updateLawyerLawsuitById(lawyerLawsuitId, lawyerLawsuitUpdateDTO);

        if (updatedLawyerLawsuit.isEmpty()) {
            throw new LawyerLawsuitNotFoundException("LawyerLawsuit with ID " + lawyerLawsuitId + " not found");
        } else {
            return "redirect:/lawyer-lawsuits/" + updatedLawyerLawsuit.get().getId();
        }
    }

    @PostMapping("/delete/{lawyerLawsuitId}")
    public String deleteLawyerLawsuit(@PathVariable Integer lawyerLawsuitId) {
        Optional<LawyerLawsuitReadDTO> lawyerLawsuitReadDTO = lawyerLawsuitService.getLawyerLawsuitById(lawyerLawsuitId);

        if (lawyerLawsuitReadDTO.isEmpty()) {
            throw new LawyerLawsuitNotFoundException("LawyerLawsuit with ID " + lawyerLawsuitId + " not found");
        } else {
            lawyerLawsuitService.deleteLawyerLawsuit(lawyerLawsuitId);
            return "redirect:/lawyer-lawsuits";
        }
    }
}
