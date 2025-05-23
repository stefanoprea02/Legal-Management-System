package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.HearingNotFoundException;
import ord.example.LegalManagementSystem.service.HearingService;
import ord.example.LegalManagementSystem.service.LawsuitService;
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
@RequestMapping("/hearings")
public class HearingController {
    private final HearingService hearingService;
    private final LawsuitService lawsuitService;

    public HearingController(HearingService hearingService, LawsuitService lawsuitService) {
        this.hearingService = hearingService;
        this.lawsuitService = lawsuitService;
    }

    @GetMapping("/create")
    public String getCreateHearingForm(Model model) {
        model.addAttribute("hearing", new HearingCreateDTO());
        model.addAttribute("lawsuits", lawsuitService.getLawsuits());
        model.addAttribute("formAction", "/hearings");
        return "hearing/form";
    }

    @PostMapping
    public String createHearing(@Valid @ModelAttribute("hearing") HearingCreateDTO hearingCreateDTO,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("lawsuits", lawsuitService.getLawsuits());
            model.addAttribute("formAction", "/hearings");
            return "hearing/form";
        }

        HearingReadDTO savedHearing = hearingService.saveHearing(hearingCreateDTO);

        return "redirect:/hearings/" + savedHearing.getHearingId();
    }

    @GetMapping("/{hearingId}")
    public String getHearingById(@PathVariable Integer hearingId, Model model) {
        Optional<HearingReadDTO> hearing = hearingService.getHearingById(hearingId);

        if (hearing.isEmpty()) {
            throw new HearingNotFoundException("Hearing with ID " + hearingId + " not found");
        } else {
            model.addAttribute("hearing", hearing.get());
            return "hearing/hearing";
        }
    }

    @GetMapping
    public String getAllHearings(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "hearingId") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            Model model) {
        Page<HearingReadDTO> hearings = hearingService.getHearingsPage(page, sortField, sortOrder);

        model.addAttribute("hearings", hearings);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "hearing/hearings";
    }

    @GetMapping("edit/{hearingId}")
    public String getEditHearingForm(@PathVariable Integer hearingId, Model model) {
        Optional<HearingReadDTO> hearing = hearingService.getHearingById(hearingId);

        if (hearing.isEmpty()) {
            throw new HearingNotFoundException("Hearing with ID " + hearingId + " not found");
        } else {
            model.addAttribute("hearing", hearing.get());
            model.addAttribute("formAction", "/hearings/" + hearingId);
            return "hearing/form";
        }
    }

    @PostMapping("/{hearingId}")
    public String updateHearing(@PathVariable Integer hearingId,
                                @Valid @ModelAttribute("hearing") HearingUpdateDTO hearingUpdateDTO,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formAction", "/hearings/" + hearingId);
            return "hearing/form";
        }

        Optional<HearingReadDTO> updatedHearing = hearingService.updateHearingById(hearingId, hearingUpdateDTO);

        if (updatedHearing.isEmpty()) {
            throw new HearingNotFoundException("Hearing with ID " + hearingId + " not found");
        } else {
            return "redirect:/hearings/" + updatedHearing.get().getHearingId();
        }
    }

    @PostMapping("/delete/{hearingId}")
    public String deleteHearing(@PathVariable Integer hearingId) {
        Optional<HearingReadDTO> hearing = hearingService.getHearingById(hearingId);

        if (hearing.isPresent()) {
            hearingService.deleteHearingById(hearingId);
            return "redirect:/hearings";
        } else {
            throw new HearingNotFoundException("Hearing with ID " + hearingId + " not found");
        }
    }
}
