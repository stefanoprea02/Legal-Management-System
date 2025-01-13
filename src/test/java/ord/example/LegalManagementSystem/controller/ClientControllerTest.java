package ord.example.LegalManagementSystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.service.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Test
    public void testCreateClient_success() throws Exception {
        ClientCUDTO request = new ClientCUDTO();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setClientAddress("123 Main Street");

        ClientReadDTO response = new ClientReadDTO();
        response.setClientId(1);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setClientAddress("123 Main Street");

        Mockito.when(clientService.saveClient(any(ClientCUDTO.class))).thenReturn(response);

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.clientAddress").value("123 Main Street"));

        Mockito.verify(clientService, times(1)).saveClient(any(ClientCUDTO.class));
    }

    @Test
    public void testCreateClient_validationError() throws Exception {
        ClientCUDTO request = new ClientCUDTO();
        request.setFirstName("");
        request.setLastName("Doe");
        request.setClientAddress("123 Main Street");

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                        .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(clientService);
    }

    @Test
    public void testGetClientById_success() throws Exception {
        ClientReadDTO response = new ClientReadDTO();
        response.setClientId(1);
        response.setFirstName("John");
        response.setLastName("Doe");
        response.setClientAddress("123 Main Street");

        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.clientAddress").value("123 Main Street"));

        Mockito.verify(clientService, times(1)).getClientById(1);
    }

    @Test
    public void testGetClientById_notFound() throws Exception {
        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(clientService, times(1)).getClientById(1);
    }

    @Test
    public void testGetAllClients_success() throws Exception {
        ClientReadDTO client1 = new ClientReadDTO();
        client1.setClientId(1);
        client1.setFirstName("John");
        client1.setLastName("Doe");

        ClientReadDTO client2 = new ClientReadDTO();
        client2.setClientId(2);
        client2.setFirstName("Jane");
        client2.setLastName("Smith");

        List<ClientReadDTO> clients = Arrays.asList(client1, client2);

        Mockito.when(clientService.getClients()).thenReturn(clients);

        mockMvc.perform(get("/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].clientId").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].clientId").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));

        Mockito.verify(clientService, times(1)).getClients();
    }

    @Test
    public void testUpdateClient_success() throws Exception {
        ClientCUDTO request = new ClientCUDTO();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClientAddress("456 Elm Street");

        ClientReadDTO response = new ClientReadDTO();
        response.setClientId(1);
        response.setFirstName("Jane");
        response.setLastName("Doe");
        response.setClientAddress("456 Elm Street");

        Mockito.when(clientService.updateClientById(eq(1), any(ClientCUDTO.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.clientAddress").value("456 Elm Street"));

        Mockito.verify(clientService, times(1)).updateClientById(eq(1), any(ClientCUDTO.class));
    }

    @Test
    public void testUpdateClient_notFound() throws Exception {
        ClientCUDTO request = new ClientCUDTO();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setClientAddress("456 Elm Street");

        Mockito.when(clientService.updateClientById(eq(1), any(ClientCUDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isNotFound());

        Mockito.verify(clientService, times(1)).updateClientById(eq(1), any(ClientCUDTO.class));
    }

    @Test
    public void testDeleteClient_success() throws Exception {
        Mockito.doNothing().when(clientService).deleteClientById(1);

        mockMvc.perform(delete("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(clientService, times(1)).deleteClientById(1);
    }

    @Test
    public void testDeleteClient_notFound() throws Exception {
        Mockito.doThrow(new RuntimeException("Not Found")).when(clientService).deleteClientById(1);

        mockMvc.perform(delete("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(clientService, times(1)).deleteClientById(1);
    }
}

