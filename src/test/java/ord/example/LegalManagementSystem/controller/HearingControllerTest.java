package ord.example.LegalManagementSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingCreateDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingUpdateDTO;
import ord.example.LegalManagementSystem.service.HearingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HearingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HearingService hearingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateHearing_success() throws Exception {
        HearingCreateDTO request = new HearingCreateDTO();
        request.setLawsuitId(1);
        request.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        request.setAppointmentAddress("123 Court St");

        HearingReadDTO response = new HearingReadDTO();
        response.setHearingId(1);
        response.setDateTime(Timestamp.valueOf("2025-01-01 08:00:00"));
        response.setAppointmentAddress("123 Court St");

        Mockito.when(hearingService.saveHearing(any(HearingCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/hearings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hearingId").value(1))
                .andExpect(jsonPath("$.appointmentAddress").value("123 Court St"))
                .andExpect(jsonPath("$.dateTime").value("2025-01-01T06:00:00.000+00:00"));

        Mockito.verify(hearingService, times(1)).saveHearing(any(HearingCreateDTO.class));
    }

    @Test
    void testGetHearingById_found() throws Exception {
        int hearingId = 1;

        HearingReadDTO response = new HearingReadDTO();
        response.setHearingId(hearingId);
        response.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        response.setAppointmentAddress("123 Court St");

        Mockito.when(hearingService.getHearingById(hearingId)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/hearings/{hearingId}", hearingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hearingId").value(1))
                .andExpect(jsonPath("$.appointmentAddress").value("123 Court St"))
                .andExpect(jsonPath("$.dateTime").value("2025-01-01T08:00:00.000+00:00"));

        Mockito.verify(hearingService, times(1)).getHearingById(hearingId);
    }

    @Test
    void testGetHearingById_notFound() throws Exception {
        int hearingId = 1;

        Mockito.when(hearingService.getHearingById(hearingId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/hearings/{hearingId}", hearingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(hearingService, times(1)).getHearingById(hearingId);
    }

    @Test
    void testGetAllHearings_success() throws Exception {
        HearingReadDTO hearing1 = new HearingReadDTO();
        hearing1.setHearingId(1);
        hearing1.setDateTime(Timestamp.valueOf("2025-01-01 10:00:00"));
        hearing1.setAppointmentAddress("123 Court St");

        HearingReadDTO hearing2 = new HearingReadDTO();
        hearing2.setHearingId(2);
        hearing2.setDateTime(Timestamp.valueOf("2025-02-01 11:00:00"));
        hearing2.setAppointmentAddress("456 Main St");

        List<HearingReadDTO> response = Arrays.asList(hearing1, hearing2);

        Mockito.when(hearingService.getHearings()).thenReturn(response);

        mockMvc.perform(get("/hearings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].hearingId").value(1))
                .andExpect(jsonPath("$[1].hearingId").value(2));

        Mockito.verify(hearingService, times(1)).getHearings();
    }

    @Test
    void testUpdateHearing_success() throws Exception {
        int hearingId = 1;

        HearingUpdateDTO request = new HearingUpdateDTO();
        request.setDateTime(Timestamp.valueOf("2025-01-01 11:00:00"));
        request.setAppointmentAddress("456 Main St");

        HearingReadDTO response = new HearingReadDTO();
        response.setHearingId(hearingId);
        response.setDateTime(Timestamp.valueOf("2025-01-01 11:00:00"));
        response.setAppointmentAddress("456 Main St");

        Mockito.when(hearingService.updateHearingById(eq(hearingId), any(HearingUpdateDTO.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/hearings/{hearingId}", hearingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hearingId").value(1))
                .andExpect(jsonPath("$.appointmentAddress").value("456 Main St"))
                .andExpect(jsonPath("$.dateTime").value("2025-01-01T09:00:00.000+00:00"));

        Mockito.verify(hearingService, times(1)).updateHearingById(eq(hearingId), any(HearingUpdateDTO.class));
    }

    @Test
    void testUpdateHearing_notFound() throws Exception {
        int hearingId = 1;

        HearingUpdateDTO request = new HearingUpdateDTO();
        request.setDateTime(Timestamp.valueOf("2025-01-01 11:00:00"));
        request.setAppointmentAddress("456 Main St");

        Mockito.when(hearingService.updateHearingById(eq(hearingId), any(HearingUpdateDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/hearings/{hearingId}", hearingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Mockito.verify(hearingService, times(1)).updateHearingById(eq(hearingId), any(HearingUpdateDTO.class));
    }

    @Test
    void testDeleteHearing_success() throws Exception {
        int hearingId = 1;

        Mockito.doNothing().when(hearingService).deleteHearingById(hearingId);

        mockMvc.perform(delete("/hearings/{hearingId}", hearingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(hearingService, times(1)).deleteHearingById(hearingId);
    }

    @Test
    void testDeleteHearing_notFound() throws Exception {
        int hearingId = 1;

        Mockito.doThrow(new RuntimeException("Hearing not found")).when(hearingService).deleteHearingById(hearingId);

        mockMvc.perform(delete("/hearings/{hearingId}", hearingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(hearingService, times(1)).deleteHearingById(hearingId);
    }
}
