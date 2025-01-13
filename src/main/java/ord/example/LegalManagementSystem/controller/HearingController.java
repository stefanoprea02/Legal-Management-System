package ord.example.LegalManagementSystem.controller;

import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingUpdateDTO;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.service.HearingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hearings")
public class HearingController {
    private final HearingService hearingService;

    public HearingController(HearingService hearingService) {
        this.hearingService = hearingService;
    }

    @PostMapping
    public ResponseEntity<HearingReadDTO> createHearing(@RequestBody HearingCreateDTO hearingCreateDTO) {
        HearingReadDTO savedHearing = hearingService.saveHearing(hearingCreateDTO);
        return new ResponseEntity<>(savedHearing, HttpStatus.CREATED);
    }

    @GetMapping("/{hearingId}")
    public ResponseEntity<HearingReadDTO> getHearingById(@PathVariable Integer hearingId) {
        Optional<HearingReadDTO> hearing = hearingService.getHearingById(hearingId);
        return hearing.map(h -> new ResponseEntity<>(h, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<HearingReadDTO>> getAllHearings() {
        List<HearingReadDTO> hearings = hearingService.getHearings();
        return new ResponseEntity<>(hearings, HttpStatus.OK);
    }

    @PutMapping("/{hearingId}")
    public ResponseEntity<HearingReadDTO> updateHearing(@PathVariable Integer hearingId, @RequestBody HearingUpdateDTO hearingUpdateDTO) {
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
