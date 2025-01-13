package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitCUDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
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
    public LawyerLawsuitReadDTO createLawyerLawsuit(LawyerLawsuitCUDTO lawyerLawsuitCUDTO) {
        Lawyer lawyer = lawyerRepository.findById(lawyerLawsuitCUDTO.getLawyerId())
                .orElseThrow(() -> new LawyerNotFoundException("Lawyer not found for ID: " + lawyerLawsuitCUDTO.getLawyerId()));

        Lawsuit lawsuitEntity = lawsuitRepository.findById(lawyerLawsuitCUDTO.getLawsuitId())
                .orElseThrow(() -> new LawsuitNotFoundException("Lawsuit not found for ID: " + lawyerLawsuitCUDTO.getLawsuitId()));

        LawyerLawsuit lawyerLawsuit = new LawyerLawsuit();
        lawyerLawsuit.setLawyer(lawyer);
        lawyerLawsuit.setLawsuitEntity(lawsuitEntity);
        lawyerLawsuit.setHoursWorked(lawyerLawsuitCUDTO.getHoursWorked());

        return globalMapper.toLawyerLawsuitReadDto(lawyerLawsuitRepository.save(lawyerLawsuit), true, true);
    }

    public Optional<LawyerLawsuitReadDTO> getLawyerLawsuitById(Integer lawyerLawsuitId) {
        return lawyerLawsuitRepository.findById(lawyerLawsuitId).map(l -> globalMapper.toLawyerLawsuitReadDto(l, true, true));
    }

    public Optional<LawyerLawsuitReadDTO> updateLawyerLawsuitById(int lawyerLawsuitId, LawyerLawsuitCUDTO lawyerLawsuitCUDTO) {
        return lawyerLawsuitRepository.findById(lawyerLawsuitId).map(existingLawyerLawsuit -> {
            existingLawyerLawsuit.setHoursWorked(lawyerLawsuitCUDTO.getHoursWorked());

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
