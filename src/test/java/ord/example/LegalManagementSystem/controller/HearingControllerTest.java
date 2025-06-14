package ord.example.LegalManagementSystem.controller;

import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingUpdateDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.service.HearingService;
import ord.example.LegalManagementSystem.service.LawsuitService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HearingController.class)
@ActiveProfiles("h2")
@AutoConfigureMockMvc(addFilters = false)
class HearingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HearingService hearingService;

    @MockitoBean
    private LawsuitService lawsuitService;

    @Test
    void testGetCreateHearingForm_returnsFormView() throws Exception {
        Mockito.when(lawsuitService.getLawsuits()).thenReturn(List.of());

        mockMvc.perform(get("/hearings/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("hearing/form"))
                .andExpect(model().attributeExists("hearing"))
                .andExpect(model().attributeExists("lawsuits"))
                .andExpect(model().attribute("formAction", "/hearings"));
    }

    @Test
    void testCreateHearing_successRedirects() throws Exception {
        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(1);

        Mockito.when(hearingService.saveHearing(any(HearingCreateDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/hearings")
                        .param("dateTime", "2023-01-01T12:00")
                        .param("appointmentAddress", "Courtroom A")
                        .param("lawsuitId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/hearings/1"))
                .andExpect(redirectedUrl("/hearings/1"));
    }

    @Test
    void testCreateHearing_validationError() throws Exception {
        Mockito.when(lawsuitService.getLawsuits()).thenReturn(List.of());

        mockMvc.perform(post("/hearings")
                        .param("appointmentAddress", "Courtroom A"))
                .andExpect(status().isOk())
                .andExpect(view().name("hearing/form"))
                .andExpect(model().attribute("formAction", "/hearings"))
                .andExpect(model().attributeExists("lawsuits"));
    }

    @Test
    void testGetHearingById_found_returnsView() throws Exception {
        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(1);

        LawsuitReadDTO lawsuitReadDTO = new LawsuitReadDTO();
        lawsuitReadDTO.setLawsuitId(1);
        lawsuitReadDTO.setReason("12345");
        dto.setLawsuit(lawsuitReadDTO);

        Mockito.when(hearingService.getHearingById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/hearings/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hearing/hearing"))
                .andExpect(model().attribute("hearing", dto));
    }

    @Test
    void testGetHearingById_notFound_throwsException() throws Exception {
        Mockito.when(hearingService.getHearingById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/hearings/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("notFoundException"));
    }

    @Test
    void testGetAllHearings_returnsHearingsView() throws Exception {
        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(1);

        LawsuitReadDTO lawsuitReadDTO = new LawsuitReadDTO();
        lawsuitReadDTO.setLawsuitId(1);
        lawsuitReadDTO.setReason("12345");
        dto.setLawsuit(lawsuitReadDTO);

        Page<HearingReadDTO> page = new PageImpl<>(List.of(dto));

        Mockito.when(hearingService.getHearingsPage(0, "hearingId", "asc")).thenReturn(page);

        mockMvc.perform(get("/hearings"))
                .andExpect(status().isOk())
                .andExpect(view().name("hearing/hearings"))
                .andExpect(model().attribute("hearings", page))
                .andExpect(model().attribute("sortField", "hearingId"))
                .andExpect(model().attribute("sortOrder", "asc"));
    }

    @Test
    void testGetEditHearingForm_found_returnsFormView() throws Exception {
        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(1);

        Mockito.when(hearingService.getHearingById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/hearings/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hearing/form"))
                .andExpect(model().attribute("hearing", dto))
                .andExpect(model().attribute("formAction", "/hearings/1"));
    }

    @Test
    void testGetEditHearingForm_notFound_throwsException() throws Exception {
        Mockito.when(hearingService.getHearingById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/hearings/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("notFoundException"));
    }

    @Test
    void testUpdateHearing_successRedirects() throws Exception {
        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(1);

        Mockito.when(hearingService.updateHearingById(eq(1), any(HearingUpdateDTO.class)))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(post("/hearings/1")
                        .param("dateTime", "2023-01-01T12:00")
                        .param("appointmentAddress", "Courtroom A"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/hearings/1"))
                .andExpect(redirectedUrl("/hearings/1"));
    }

    @Test
    void testUpdateHearing_validationError() throws Exception {
        mockMvc.perform(post("/hearings/1")
                        .param("appointmentAddress", "Courtroom A"))
                .andExpect(status().isOk())
                .andExpect(view().name("hearing/form"))
                .andExpect(model().attribute("formAction", "/hearings/1"));
    }

    @Test
    void testUpdateHearing_notFound_throwsException() throws Exception {
        Mockito.when(hearingService.updateHearingById(eq(1), any(HearingUpdateDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/hearings/1")
                        .param("dateTime", "2023-01-01T12:00")
                        .param("appointmentAddress", "Courtroom A"))
                .andExpect(status().isOk())
                .andExpect(view().name("notFoundException"));
    }

    @Test
    void testDeleteHearing_successRedirects() throws Exception {
        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(1);

        Mockito.when(hearingService.getHearingById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(post("/hearings/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hearings"));

        Mockito.verify(hearingService).deleteHearingById(1);
    }

    @Test
    void testDeleteHearing_notFound_throwsException() throws Exception {
        Mockito.when(hearingService.getHearingById(1)).thenReturn(Optional.empty());

        mockMvc.perform(post("/hearings/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("notFoundException"));
    }
}
