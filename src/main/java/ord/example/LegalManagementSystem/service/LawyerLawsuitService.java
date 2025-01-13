package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
import ord.example.LegalManagementSystem.exceptions.LawyerNotFoundException;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Lawsuit;
import ord.example.LegalManagementSystem.model.Lawyer;
import ord.example.LegalManagementSystem.model.LawyerLawsuit;
import ord.example.LegalManagementSystem.repository.LawsuitRepository;
import ord.example.LegalManagementSystem.repository.LawyerLawsuitRepository;
import ord.example.LegalManagementSystem.repository.LawyerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LawyerLawsuitService {

    private final LawyerLawsuitRepository lawyerLawsuitRepository;
    private final LawyerRepository lawyerRepository;
    private final LawsuitRepository lawsuitRepository;
    private final GlobalMapper globalMapper;

    public LawyerLawsuitService(LawyerLawsuitRepository lawyerLawsuitRepository, LawyerRepository lawyerRepository, LawsuitRepository lawsuitRepository, GlobalMapper globalMapper) {
        this.lawyerLawsuitRepository = lawyerLawsuitRepository;
        this.lawyerRepository = lawyerRepository;
        this.lawsuitRepository = lawsuitRepository;
        this.globalMapper = globalMapper;
    }

    @Transactional
    public LawyerLawsuitReadDTO createLawyerLawsuit(LawyerLawsuitCreateDTO lawyerLawsuitCreateDTO) {
        Lawyer lawyer = lawyerRepository.findById(lawyerLawsuitCreateDTO.getLawyerId())
                .orElseThrow(() -> new LawyerNotFoundException("Lawyer not found for ID: " + lawyerLawsuitCreateDTO.getLawyerId()));

        Lawsuit lawsuitEntity = lawsuitRepository.findById(lawyerLawsuitCreateDTO.getLawsuitId())
                .orElseThrow(() -> new LawsuitNotFoundException("Lawsuit not found for ID: " + lawyerLawsuitCreateDTO.getLawsuitId()));

        LawyerLawsuit lawyerLawsuit = new LawyerLawsuit();
        lawyerLawsuit.setLawyer(lawyer);
        lawyerLawsuit.setLawsuitEntity(lawsuitEntity);
        lawyerLawsuit.setHoursWorked(lawyerLawsuitCreateDTO.getHoursWorked());

        return globalMapper.toLawyerLawsuitReadDto(lawyerLawsuitRepository.save(lawyerLawsuit), true, true);
    }

    public Optional<LawyerLawsuitReadDTO> getLawyerLawsuitById(Integer lawyerLawsuitId) {
        return lawyerLawsuitRepository.findById(lawyerLawsuitId).map(l -> globalMapper.toLawyerLawsuitReadDto(l, true, true));
    }

    public Optional<LawyerLawsuitReadDTO> updateLawyerLawsuitById(int lawyerLawsuitId, LawyerLawsuitUpdateDTO lawsuitUpdateDTO) {
        return lawyerLawsuitRepository.findById(lawyerLawsuitId).map(existingLawyerLawsuit -> {
            existingLawyerLawsuit.setHoursWorked(lawsuitUpdateDTO.getHoursWorked());

            return globalMapper.toLawyerLawsuitReadDto(lawyerLawsuitRepository.save(existingLawyerLawsuit), true, true);
        });
    }

    public void deleteLawyerLawsuit(Integer lawyerLawsuitId) {
        lawyerLawsuitRepository.deleteById(lawyerLawsuitId);
    }

    public List<LawyerLawsuitReadDTO> getLawyerLawsuitsByLawsuitId(Integer lawsuitId) {
        Lawsuit lawsuitEntity = lawsuitRepository.findById(lawsuitId)
                .orElseThrow(() -> new LawsuitNotFoundException("Lawsuit not found for ID: " + lawsuitId));

        return lawyerLawsuitRepository.findByLawsuitEntity(lawsuitEntity).stream()
                .map(l -> globalMapper.toLawyerLawsuitReadDto(l, true, true))
                .collect(Collectors.toList());
    }
}
