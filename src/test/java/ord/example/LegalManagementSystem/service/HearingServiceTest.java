package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.exceptions.LawsuitHasHearingException;
import ord.example.LegalManagementSystem.exceptions.LawsuitNotFoundException;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Hearing;
import ord.example.LegalManagementSystem.model.Lawsuit;
import ord.example.LegalManagementSystem.repository.HearingRepository;
import ord.example.LegalManagementSystem.repository.LawsuitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import java.sql.Timestamp;

@ExtendWith(MockitoExtension.class)
public class HearingServiceTest {
    @Mock
    private HearingRepository hearingRepository;

    @Mock
    private LawsuitRepository lawsuitRepository;

    @Mock
    private GlobalMapper globalMapper;

    @InjectMocks
    private HearingService hearingService;

    @Test
    public void testSaveHearing_Success() {
        HearingCreateDTO hearingCreateDTO = new HearingCreateDTO();
        hearingCreateDTO.setLawsuitId(1);
        hearingCreateDTO.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearingCreateDTO.setAppointmentAddress("123 Main Street");

        Lawsuit lawsuit = new Lawsuit();
        lawsuit.setLawsuitId(1);

        Hearing hearing = new Hearing();
        hearing.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearing.setAppointmentAddress("123 Main Street");
        hearing.setLawsuit(lawsuit);

        HearingReadDTO hearingReadDTO = new HearingReadDTO();
        hearingReadDTO.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearingReadDTO.setAppointmentAddress("123 Main Street");

        when(lawsuitRepository.findById(1)).thenReturn(Optional.of(lawsuit));
        when(hearingRepository.save(any(Hearing.class))).thenReturn(hearing);
        when(globalMapper.toHearingReadDto(any(Hearing.class), eq(true))).thenReturn(hearingReadDTO);

        HearingReadDTO result = hearingService.saveHearing(hearingCreateDTO);

        assertNotNull(result);
        assertEquals("123 Main Street", result.getAppointmentAddress());
        verify(hearingRepository).save(any(Hearing.class));
        verify(lawsuitRepository).findById(1);
    }

    @Test
    public void testSaveHearing_LawsuitNotFound() {
        HearingCreateDTO hearingCreateDTO = new HearingCreateDTO();
        hearingCreateDTO.setLawsuitId(1);
        hearingCreateDTO.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearingCreateDTO.setAppointmentAddress("123 Main Street");

        when(lawsuitRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(LawsuitNotFoundException.class, () -> hearingService.saveHearing(hearingCreateDTO));
    }

    @Test
    public void testSaveHearing_LawsuitHasHearing() {
        HearingCreateDTO hearingCreateDTO = new HearingCreateDTO();
        hearingCreateDTO.setLawsuitId(1);
        hearingCreateDTO.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearingCreateDTO.setAppointmentAddress("123 Main Street");

        Hearing existingHearing = new Hearing();
        existingHearing.setLawsuit(new Lawsuit());
        Lawsuit lawsuit = new Lawsuit();
        lawsuit.setLawsuitId(1);
        lawsuit.setHearing(existingHearing);

        when(lawsuitRepository.findById(1)).thenReturn(Optional.of(lawsuit));

        assertThrows(LawsuitHasHearingException.class, () -> hearingService.saveHearing(hearingCreateDTO));
    }

    @Test
    public void testGetHearingById_Found() {
        Hearing hearing = new Hearing();
        hearing.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearing.setAppointmentAddress("123 Main Street");

        HearingReadDTO hearingReadDTO = new HearingReadDTO();
        hearingReadDTO.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearingReadDTO.setAppointmentAddress("123 Main Street");

        when(hearingRepository.findById(1)).thenReturn(Optional.of(hearing));
        when(globalMapper.toHearingReadDto(any(Hearing.class), eq(true))).thenReturn(hearingReadDTO);

        Optional<HearingReadDTO> result = hearingService.getHearingById(1);

        assertTrue(result.isPresent());
        assertEquals("123 Main Street", result.get().getAppointmentAddress());
        verify(hearingRepository).findById(1);
    }

    @Test
    public void testGetHearingById_NotFound() {
        when(hearingRepository.findById(1)).thenReturn(Optional.empty());

        Optional<HearingReadDTO> result = hearingService.getHearingById(1);

        assertFalse(result.isPresent());
        verify(hearingRepository).findById(1);
    }

    @Test
    public void testDeleteHearingById_Success() {
        Hearing hearing = new Hearing();
        Lawsuit lawsuit = new Lawsuit();
        hearing.setLawsuit(lawsuit);
        lawsuit.setLawsuitId(1);

        when(hearingRepository.findById(1)).thenReturn(Optional.of(hearing));
        when(lawsuitRepository.save(lawsuit)).thenReturn(lawsuit);

        hearingService.deleteHearingById(1);

        assertNull(lawsuit.getHearing());
        verify(hearingRepository).delete(hearing);
        verify(lawsuitRepository).save(lawsuit);
    }

    @Test
    public void testDeleteHearingById_NotFound() {
        when(hearingRepository.findById(1)).thenReturn(Optional.empty());

        hearingService.deleteHearingById(1);

        verify(hearingRepository, never()).delete(any(Hearing.class));
        verify(lawsuitRepository, never()).save(any(Lawsuit.class));
    }
}

