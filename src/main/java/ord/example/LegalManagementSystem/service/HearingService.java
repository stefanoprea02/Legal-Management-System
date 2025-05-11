package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.LawsuitHasHearingException;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Lawsuit;
import ord.example.LegalManagementSystem.model.Hearing;
import ord.example.LegalManagementSystem.repository.LawsuitRepository;
import ord.example.LegalManagementSystem.repository.HearingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HearingService {
    private final HearingRepository hearingRepository;
    private final LawsuitRepository lawsuitRepository;
    private final GlobalMapper globalMapper;

    public HearingService(HearingRepository hearingRepository, LawsuitRepository lawsuitRepository, GlobalMapper globalMapper) {
        this.hearingRepository = hearingRepository;
        this.lawsuitRepository = lawsuitRepository;
        this.globalMapper = globalMapper;
    }

    @Transactional
    public HearingReadDTO saveHearing(HearingCreateDTO hearingCreateDTO) {
        Hearing hearing = new Hearing();
        hearing.setDateTime(hearingCreateDTO.getDateTime());
        hearing.setAppointmentAddress(hearingCreateDTO.getAppointmentAddress());

        Lawsuit lawsuit = lawsuitRepository.findById(hearingCreateDTO.getLawsuitId())
                .orElseThrow(() -> new LawsuitNotFoundException("Lawsuit with ID " + hearingCreateDTO.getLawsuitId() + " not found"));

        if (lawsuit.getHearing() != null) {
            throw new LawsuitHasHearingException("Lawsuit with ID " + lawsuit.getLawsuitId() + " has a hearing.");
        }

        hearing.setLawsuit(lawsuit);

        return globalMapper.toHearingReadDto(hearingRepository.save(hearing), true);
    }

    public Optional<HearingReadDTO> getHearingById(Integer hearingId) {
        return hearingRepository.findById(hearingId)
                .map(hearing -> globalMapper.toHearingReadDto(hearing, true));
    }

    public List<HearingReadDTO> getHearings() {
        return hearingRepository.findAll().stream()
                .map(h -> globalMapper.toHearingReadDto(h, true))
                .collect(Collectors.toList());
    }

    public Optional<HearingReadDTO> updateHearingById(Integer hearingId, HearingUpdateDTO hearingUpdateDTO) {
        return hearingRepository.findById(hearingId).map(existingHearing -> {
            existingHearing.setAppointmentAddress(hearingUpdateDTO.getAppointmentAddress());
            existingHearing.setDateTime(hearingUpdateDTO.getDateTime());

            return globalMapper.toHearingReadDto(hearingRepository.save(existingHearing), true);
        });
    }

    public void deleteHearingById(Integer hearingId) {
        Optional<Hearing> hearing = hearingRepository.findById(hearingId);

        if(hearing.isPresent()) {
            Lawsuit lawsuit = hearing.get().getLawsuit();
            lawsuit.setHearing(null);
            lawsuitRepository.save(lawsuit);

            hearingRepository.delete(hearing.get());
        }
    }
}
