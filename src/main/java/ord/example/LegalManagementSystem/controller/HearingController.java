package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.HearingNotFoundException;
import ord.example.LegalManagementSystem.service.HearingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/hearings")
public class HearingController {
    private final HearingService hearingService;

    public HearingController(HearingService hearingService) {
        this.hearingService = hearingService;
    }

    @PostMapping
    public ResponseEntity<HearingReadDTO> createHearing(@Valid @RequestBody HearingCreateDTO hearingCreateDTO) {
        HearingReadDTO savedHearing = hearingService.saveHearing(hearingCreateDTO);
        return new ResponseEntity<>(savedHearing, HttpStatus.CREATED);
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
    public String getAllHearings(Model model) {
        List<HearingReadDTO> hearings = hearingService.getHearings();

        model.addAttribute("hearings", hearings);
        return "hearing/hearings";
    }

    @PutMapping("/{hearingId}")
    public ResponseEntity<HearingReadDTO> updateHearing(@PathVariable Integer hearingId, @Valid @RequestBody HearingUpdateDTO hearingUpdateDTO) {
        Optional<HearingReadDTO> updatedHearing = hearingService.updateHearingById(hearingId, hearingUpdateDTO);
        return updatedHearing.map(h -> new ResponseEntity<>(h, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{hearingId}")
    public ResponseEntity<Void> deleteHearing(@PathVariable Integer hearingId) {
        try {
            hearingService.deleteHearingById(hearingId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
