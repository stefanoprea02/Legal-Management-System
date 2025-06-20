package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerCUDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerReadDTO;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Lawsuit;
import ord.example.LegalManagementSystem.model.Lawyer;
import ord.example.LegalManagementSystem.model.LawyerLawsuit;
import ord.example.LegalManagementSystem.repository.LawsuitRepository;
import ord.example.LegalManagementSystem.repository.LawyerLawsuitRepository;
import ord.example.LegalManagementSystem.repository.LawyerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LawyerService {
    private final LawyerRepository lawyerRepository;
    private final LawyerLawsuitRepository lawyerLawsuitRepository;
    private final LawsuitRepository lawsuitRepository;
    private final GlobalMapper globalMapper;

    private static final int DEFAULT_PAGE_SIZE = 10;

    public LawyerService(LawyerRepository lawyerRepository, LawyerLawsuitRepository lawyerLawsuitRepository, LawsuitRepository lawsuitRepository, GlobalMapper globalMapper) {
        this.lawyerRepository = lawyerRepository;
        this.lawyerLawsuitRepository = lawyerLawsuitRepository;
        this.lawsuitRepository = lawsuitRepository;
        this.globalMapper = globalMapper;
    }

    public LawyerReadDTO saveLawyer(LawyerCUDTO lawyerCUDTO) {
        Lawyer lawyer = new Lawyer();
        lawyer.setLawyerAddress(lawyerCUDTO.getLawyerAddress());
        lawyer.setLawyerType(lawyerCUDTO.getLawyerType());
        lawyer.setCommission(lawyerCUDTO.getCommission());
        lawyer.setFirstName(lawyerCUDTO.getFirstName());
        lawyer.setLastName(lawyerCUDTO.getLastName());
        lawyer.setHireDate(lawyerCUDTO.getHireDate());
        lawyer.setHourlyRate(lawyerCUDTO.getHourlyRate());

        List<LawyerLawsuit> lawyerLawsuits = new ArrayList<>();

        if (lawyerCUDTO.getLawsuitIds() != null) {
            for (Integer lawsuitId : lawyerCUDTO.getLawsuitIds()) {
                Lawsuit lawsuit = lawsuitRepository.findById(lawsuitId)
                        .orElseThrow(() -> new LawsuitNotFoundException("Lawyer with ID " + lawsuitId + " not found."));

                LawyerLawsuit lawyerLawsuit = new LawyerLawsuit();
                lawyerLawsuit.setLawsuit(lawsuit);
                lawyerLawsuit.setLawyer(lawyer);
                lawyerLawsuit.setHoursWorked(0);

                lawyerLawsuits.add(lawyerLawsuit);
            }
        }

        lawyer.setLawyerLawsuits(lawyerLawsuits);

        return globalMapper.toLawyerReadDto(lawyerRepository.save(lawyer), true);
    }

    public Optional<LawyerReadDTO> getLawyerById(Integer lawyerId) {
        return lawyerRepository.findById(lawyerId).map(l -> globalMapper.toLawyerReadDto(l, true));
    }

    public List<LawyerReadDTO> getLawyers() {
        return lawyerRepository.findAll().stream()
                .map(l -> globalMapper.toLawyerReadDto(l, true))
                .collect(Collectors.toList());
    }

    public Page<LawyerReadDTO> getLawyersPage(int page, String sortField, String sortOrder) {
        Pageable pageable = PageRequest.of(
                page,
                DEFAULT_PAGE_SIZE,
                sortOrder.equalsIgnoreCase("desc")
                        ? Sort.by(sortField).descending()
                        : Sort.by(sortField).ascending()
        );

        return lawyerRepository.findAll(pageable)
                .map(l -> globalMapper.toLawyerReadDto(l, true));
    }

    @Transactional
    public Optional<LawyerReadDTO> updateLawyerById(Integer lawyerId, LawyerCUDTO lawyerCUDTO) {
        return lawyerRepository.findById(lawyerId).map(existingLawyer -> {
            existingLawyer.setLawyerAddress(lawyerCUDTO.getLawyerAddress());
            existingLawyer.setLawyerType(lawyerCUDTO.getLawyerType());
            existingLawyer.setCommission(lawyerCUDTO.getCommission());
            existingLawyer.setFirstName(lawyerCUDTO.getFirstName());
            existingLawyer.setLastName(lawyerCUDTO.getLastName());
            existingLawyer.setHireDate(lawyerCUDTO.getHireDate());
            existingLawyer.setHourlyRate(lawyerCUDTO.getHourlyRate());

            Set<Integer> newLawsuitIds = lawyerCUDTO.getLawsuitIds() != null
                    ? new HashSet<>(lawyerCUDTO.getLawsuitIds())
                    : Collections.emptySet();

            List<LawyerLawsuit> existingLawyerLawsuits = existingLawyer.getLawyerLawsuits();

            existingLawyerLawsuits.removeIf(lawyerLawsuit -> !newLawsuitIds.contains(lawyerLawsuit.getLawsuit().getLawsuitId()));

            Set<Integer> existingLawsuitIds = existingLawyerLawsuits.stream()
                    .map(lawyerLawsuit -> lawyerLawsuit.getLawsuit().getLawsuitId())
                    .collect(Collectors.toSet());

            for (Integer lawsuitId : newLawsuitIds) {
                if (!existingLawsuitIds.contains(lawsuitId)) {
                    Lawsuit lawsuit = lawsuitRepository.findById(lawsuitId)
                            .orElseThrow(() -> new LawsuitNotFoundException("Lawsuit not found for ID: " + lawsuitId));

                    LawyerLawsuit newLawyerLawsuit = new LawyerLawsuit();
                    newLawyerLawsuit.setLawsuit(lawsuit);
                    newLawyerLawsuit.setLawyer(existingLawyer);
                    newLawyerLawsuit.setHoursWorked(0);

                    existingLawyerLawsuits.add(newLawyerLawsuit);
                }
            }

            return globalMapper.toLawyerReadDto(lawyerRepository.save(existingLawyer), true);
        });
    }

    public void deleteLawyerById(int lawyerId) {
        lawyerRepository.deleteById(lawyerId);
    }
}
