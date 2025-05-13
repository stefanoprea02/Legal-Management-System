package ord.example.LegalManagementSystem.controller;

import jakarta.annotation.Resource;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
                                @RequestParam("lawsuitData") MultipartFile file,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getClients());
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("formAction", "/lawsuits");
            return "lawsuit/form";
        }

        LawsuitReadDTO savedLawsuit = lawsuitService.saveLawsuite(lawsuitCreateDTO, file);

        return "redirect:/lawsuits/" + savedLawsuit.getLawsuitId();
    }

    @GetMapping("/{lawsuitId}")
    public String getLawsuitById(@PathVariable Integer lawsuitId, Model model) {
        Optional<LawsuitReadDTO> lawsuitOptional = lawsuitService.getLawsuitById(lawsuitId);

        if (lawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        } else {
            LawsuitReadDTO lawsuit = lawsuitOptional.get();

            boolean hasPdf = lawsuit.getLawsuitData() != null &&
                    lawsuit.getLawsuitData().length > 0;

            model.addAttribute("lawsuit", lawsuit);
            model.addAttribute("hasPdf", hasPdf);
            return "lawsuit/lawsuit";
        }
    }

    @GetMapping("/download/{lawsuitId}")
    public ResponseEntity<ByteArrayResource> downloadLawsuitPdf(@PathVariable Integer lawsuitId) {
        Optional<LawsuitReadDTO> lawsuitOptional = lawsuitService.getLawsuitById(lawsuitId);

        if (lawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        }

        LawsuitReadDTO lawsuit = lawsuitOptional.get();

        byte[] pdfData = lawsuit.getLawsuitData();
        if (pdfData == null || pdfData.length == 0) {
            throw new LawsuitNotFoundException("Lawsuit data for lawsuit with ID " + lawsuitId + " not found");
        }

        ByteArrayResource resource = new ByteArrayResource(pdfData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"lawsuit_" + lawsuitId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfData.length)
                .body(resource);
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
                                @RequestParam(value = "lawsuitData", required = false) MultipartFile file,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.getClients());
            model.addAttribute("lawyers", lawyerService.getLawyers());
            model.addAttribute("formAction", "/lawsuits/" + lawsuitId);
            return "lawsuit/form";
        }

        Optional<LawsuitReadDTO> updatedLawsuitOptional =
                lawsuitService.updateLawsuitById(lawsuitId, lawsuitUpdateDTO, file);

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
