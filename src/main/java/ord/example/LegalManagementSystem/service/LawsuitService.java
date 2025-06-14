package ord.example.LegalManagementSystem.service;

import lombok.extern.slf4j.Slf4j;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitCreateDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.ClientNotFoundException;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LawsuitService {
    private final LawsuitRepository lawsuitRepository;
    private final ClientRepository clientRepository;
    private final LawyerRepository lawyerRepository;
    private final LawyerLawsuitRepository lawyerLawsuitRepository;
    private final GlobalMapper globalMapper;

    private static final int DEFAULT_PAGE_SIZE = 10;

    public LawsuitService(LawsuitRepository lawsuitRepository, ClientRepository clientRepository, LawyerRepository lawyerRepository, LawyerLawsuitRepository lawyerLawsuitRepository, GlobalMapper globalMapper) {
        this.lawsuitRepository = lawsuitRepository;
        this.clientRepository = clientRepository;
        this.lawyerRepository = lawyerRepository;
        this.lawyerLawsuitRepository = lawyerLawsuitRepository;
        this.globalMapper = globalMapper;
    }

    @Transactional
    public LawsuitReadDTO saveLawsuit(LawsuitCreateDTO lawsuitCreateDTO, MultipartFile file) {
        Lawsuit lawsuit = new Lawsuit();
        lawsuit.setReason(lawsuitCreateDTO.getReason());
        lawsuit.setOpposingParty(lawsuitCreateDTO.getOpposingParty());

        Client client = clientRepository.findById(lawsuitCreateDTO.getClientId())
                .orElseThrow(() -> new ClientNotFoundException("Client with ID " + lawsuitCreateDTO.getClientId() + " not found"));

        lawsuit.setClient(client);

        if (file != null && !file.isEmpty()) {
            try {
                lawsuit.setLawsuitData(file.getBytes());
            } catch (IOException e) {
                log.error("Error while uploading PDF file: {}", e.getMessage());
                throw new RuntimeException("Failed to upload PDF file", e);
            }
        }

        List<LawyerLawsuit> lawyerLawsuits = new ArrayList<>();

        if (lawsuitCreateDTO.getLawyerIds() != null) {
            for (Integer lawyerId : lawsuitCreateDTO.getLawyerIds()) {
                Lawyer lawyer = lawyerRepository.findById(lawyerId)
                        .orElseThrow(() -> new LawyerNotFoundException("Lawyer with ID " + lawyerId + " not found."));

                LawyerLawsuit lawyerLawsuit = new LawyerLawsuit();
                lawyerLawsuit.setLawsuit(lawsuit);
                lawyerLawsuit.setLawyer(lawyer);
                lawyerLawsuit.setHoursWorked(0);

                lawyerLawsuits.add(lawyerLawsuit);
            }
        }

        lawsuit.setLawyerLawsuits(lawyerLawsuits);

        return globalMapper.toLawsuitReadDto(lawsuitRepository.save(lawsuit), true, true, true, false);
    }

    public Optional<LawsuitReadDTO> getLawsuitById(Integer lawsuitId) {
        return lawsuitRepository.findById(lawsuitId)
                .map(l -> globalMapper.toLawsuitReadDto(l, true, true, true, true));
    }

    public List<LawsuitReadDTO> getLawsuits() {
        return lawsuitRepository.findAll().stream()
                .map(l -> globalMapper.toLawsuitReadDto(l, true, true, true, false))
                .collect(Collectors.toList());
    }

    public Page<LawsuitReadDTO> getLawsuitsPage(int page, String sortField, String sortOrder) {
        Pageable pageable = PageRequest.of(
                page,
                DEFAULT_PAGE_SIZE,
                sortOrder.equalsIgnoreCase("desc")
                        ? Sort.by(sortField).descending()
                        : Sort.by(sortField).ascending()
        );

        return lawsuitRepository.findAll(pageable)
                .map(l -> globalMapper.toLawsuitReadDto(l, true, true, true, false));
    }

    @Transactional
    public Optional<LawsuitReadDTO> updateLawsuitById(Integer lawsuitId, LawsuitUpdateDTO lawsuitUpdateDTO, MultipartFile file) {
        return lawsuitRepository.findById(lawsuitId).map(existingLawsuit -> {
            existingLawsuit.setReason(lawsuitUpdateDTO.getReason());
            existingLawsuit.setOpposingParty(lawsuitUpdateDTO.getOpposingParty());

            if (file != null && !file.isEmpty()) {
                log.info("Uploading new PDF file for lawsuit ID: {}", lawsuitId);
                try {
                    existingLawsuit.setLawsuitData(file.getBytes());
                } catch (IOException e) {
                    log.error("Error while uploading PDF file: {}", e.getMessage());
                    throw new RuntimeException("Failed to upload PDF file", e);
                }
            }

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
                    newLawyerLawsuit.setLawsuit(existingLawsuit);
                    newLawyerLawsuit.setLawyer(lawyer);
                    newLawyerLawsuit.setHoursWorked(0);

                    existingLawyerLawsuits.add(newLawyerLawsuit);
                }
            }

            return globalMapper.toLawsuitReadDto(lawsuitRepository.save(existingLawsuit), true, true, true, false);
        });
    }

    public void deleteLawsuitById(Integer lawsuitId) {
        lawsuitRepository.deleteById(lawsuitId);
    }

    @Cacheable(value = "lawsuitPdf", key = "#lawsuitId")
    public byte[] getPdfData(Integer lawsuitId) {
        Optional<LawsuitReadDTO> lawsuitOptional = this.getLawsuitById(lawsuitId);
        if (lawsuitOptional.isEmpty()) {
            throw new LawsuitNotFoundException("Lawsuit with ID " + lawsuitId + " not found");
        }

        byte[] pdfData = lawsuitOptional.get().getLawsuitData();
        if (pdfData == null || pdfData.length == 0) {
            throw new LawsuitNotFoundException("Lawsuit data for lawsuit with ID " + lawsuitId + " not found");
        }

        return pdfData;
    }
}
