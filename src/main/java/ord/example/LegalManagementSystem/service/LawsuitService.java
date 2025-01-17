package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.ClientNotFoundException;
import ord.example.LegalManagementSystem.exceptions.LawyerNotFoundException;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Lawsuit;
import ord.example.LegalManagementSystem.model.Client;
import ord.example.LegalManagementSystem.model.Lawyer;
import ord.example.LegalManagementSystem.model.LawyerLawsuit;
import ord.example.LegalManagementSystem.repository.LawsuitRepository;
import ord.example.LegalManagementSystem.repository.ClientRepository;
import ord.example.LegalManagementSystem.repository.LawyerLawsuitRepository;
import ord.example.LegalManagementSystem.repository.LawyerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LawsuitService {
    private final LawsuitRepository lawsuitRepository;
    private final ClientRepository clientRepository;
    private final LawyerRepository lawyerRepository;
    private final LawyerLawsuitRepository lawyerLawsuitRepository;
    private final GlobalMapper globalMapper;

    public LawsuitService(LawsuitRepository lawsuitRepository, ClientRepository clientRepository, LawyerRepository lawyerRepository, LawyerLawsuitRepository lawyerLawsuitRepository, GlobalMapper globalMapper) {
        this.lawsuitRepository = lawsuitRepository;
        this.clientRepository = clientRepository;
        this.lawyerRepository = lawyerRepository;
        this.lawyerLawsuitRepository = lawyerLawsuitRepository;
        this.globalMapper = globalMapper;
    }

    @Transactional
    public LawsuitReadDTO saveLawsuite(LawsuitCreateDTO lawsuitCreateDTO) {
        Lawsuit lawsuitEntity = new Lawsuit();
        lawsuitEntity.setReason(lawsuitCreateDTO.getReason());
        lawsuitEntity.setOpposingParty(lawsuitCreateDTO.getOpposingParty());

        Client client = clientRepository.findById(lawsuitCreateDTO.getClientId())
                .orElseThrow(() -> new ClientNotFoundException("Client with ID " + lawsuitCreateDTO.getClientId() + " not found"));

        lawsuitEntity.setClient(client);

        Lawsuit savedLawsuit = lawsuitRepository.save(lawsuitEntity);

        if (lawsuitCreateDTO.getLawyerIds() != null) {
            for (Integer lawyerId : lawsuitCreateDTO.getLawyerIds()) {
                Lawyer lawyer = lawyerRepository.findById(lawyerId)
                        .orElseThrow(() -> new LawyerNotFoundException("Lawyer with ID " + lawyerId + " not found."));

                LawyerLawsuit lawyerLawsuit = new LawyerLawsuit();
                lawyerLawsuit.setLawsuitEntity(savedLawsuit);
                lawyerLawsuit.setLawyer(lawyer);
                lawyerLawsuit.setHoursWorked(0);

                lawyerLawsuitRepository.save(lawyerLawsuit);
            }
        }

        return globalMapper.toLawsuitReadDto(savedLawsuit, true, true);
    }

    public Optional<LawsuitReadDTO> getLawsuitById(Integer lawsuitId) {
        return lawsuitRepository.findById(lawsuitId)
                .map(l -> globalMapper.toLawsuitReadDto(l, true, true));
    }

    public List<LawsuitReadDTO> getLawsuits() {
        return lawsuitRepository.findAll().stream()
                .map(l -> globalMapper.toLawsuitReadDto(l, true, true))
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<LawsuitReadDTO> updateLawsuitById(Integer lawsuitId, LawsuitUpdateDTO lawsuitUpdateDTO) {
        return lawsuitRepository.findById(lawsuitId).map(existingLawsuit -> {
            existingLawsuit.setReason(lawsuitUpdateDTO.getReason());
            existingLawsuit.setOpposingParty(lawsuitUpdateDTO.getOpposingParty());

            Set<Integer> newLawyerIds = lawsuitUpdateDTO.getLawyerIds() != null
                    ? new HashSet<>(lawsuitUpdateDTO.getLawyerIds())
                    : Collections.emptySet();

            List<LawyerLawsuit> existingLawyerLawsuits = existingLawsuit.getLawyerLawsuits();

            existingLawyerLawsuits.removeIf(lawyerLawsuit -> !newLawyerIds.contains(lawyerLawsuit.getLawyer().getLawyerId()));

            Set<Integer> existingLawyerIds = existingLawyerLawsuits.stream()
                    .map(lawyerLawsuit -> lawyerLawsuit.getLawyer().getLawyerId())
                    .collect(Collectors.toSet());

            for (Integer lawyerId : newLawyerIds) {
                if (!existingLawyerIds.contains(lawyerId)) {
                    Lawyer lawyer = lawyerRepository.findById(lawyerId)
                            .orElseThrow(() -> new LawyerNotFoundException("Lawyer not found for ID: " + lawyerId));

                    LawyerLawsuit newLawyerLawsuit = new LawyerLawsuit();
                    newLawyerLawsuit.setLawsuitEntity(existingLawsuit);
                    newLawyerLawsuit.setLawyer(lawyer);
                    newLawyerLawsuit.setHoursWorked(0);

                    existingLawyerLawsuits.add(newLawyerLawsuit);
                }
            }

            return globalMapper.toLawsuitReadDto(lawsuitRepository.save(existingLawsuit), true, true);
        });
    }

    public void deleteLawsuitById(Integer lawsuitId) {
        lawsuitRepository.deleteById(lawsuitId);
    }
}
