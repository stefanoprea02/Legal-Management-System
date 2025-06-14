package ord.example.LegalManagementSystem.controller;

import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.service.ClientService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
@ActiveProfiles("h2")
@AutoConfigureMockMvc(addFilters = false)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Test
    void testGetCreateClientForm_returnsFormView() throws Exception {
        mockMvc.perform(get("/clients/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/form"))
                .andExpect(model().attributeExists("client"))
                .andExpect(model().attribute("formAction", "/clients"));
    }

    @Test
    void testCreateClient_successRedirects() throws Exception {
        ClientReadDTO savedClient = new ClientReadDTO();
        savedClient.setClientId(1);

        Mockito.when(clientService.saveClient(any(ClientCUDTO.class))).thenReturn(savedClient);

        mockMvc.perform(post("/clients")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("clientAddress", "123 Main Street"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/1"));
    }

    @Test
    void testCreateClient_validationError() throws Exception {
        ClientReadDTO savedClient = new ClientReadDTO();
        savedClient.setClientId(1);

        Mockito.when(clientService.saveClient(any(ClientCUDTO.class))).thenReturn(savedClient);

        mockMvc.perform(post("/clients")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("client/form"))
                .andExpect(model().attribute("formAction", "/clients"));
    }

    @Test
    void testGetClientById_found_returnsView() throws Exception {
        ClientReadDTO dto = new ClientReadDTO();
        dto.setClientId(1);
        dto.setFirstName("John");

        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/client"))
                .andExpect(model().attributeExists("client"));
    }

    @Test
    void testGetClientById_notFound_throwsException() throws Exception {
        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("notFoundException"));
    }

    @Test
    void testGetAllClients_returnsClientsView() throws Exception {
        ClientReadDTO dto = new ClientReadDTO();
        dto.setClientId(1);
        dto.setFirstName("John");
        dto.setInvoices(List.of());
        dto.setLawsuits(List.of());

        Page<ClientReadDTO> page = new PageImpl<>(List.of(dto));

        Mockito.when(clientService.getClientsPage(0, "clientId", "asc")).thenReturn(page);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/clients"))
                .andExpect(model().attributeExists("clients"))
                .andExpect(model().attribute("sortField", "clientId"))
                .andExpect(model().attribute("sortOrder", "asc"));
    }

    @Test
    void testGetEditClientForm_found_returnsFormView() throws Exception {
        ClientReadDTO dto = new ClientReadDTO();
        dto.setClientId(1);

        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/clients/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("client/form"))
                .andExpect(model().attribute("client", dto))
                .andExpect(model().attribute("formAction", "/clients/1"));
    }

    @Test
    void testGetEditClientForm_notFound_throwsException() throws Exception {
        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/clients/edit/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("notFoundException"));
    }

    @Test
    void testUpdateClient_successRedirects() throws Exception {
        ClientReadDTO updated = new ClientReadDTO();
        updated.setClientId(1);
        updated.setFirstName("Updated");
        updated.setLastName("Name");
        updated.setClientAddress("Updated Address");

        Mockito.when(clientService.updateClientById(eq(1), any(ClientCUDTO.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(post("/clients/1")
                        .param("firstName", "Updated")
                        .param("lastName", "Name")
                        .param("clientAddress", "Updated Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/1"));
    }

    @Test
    void testUpdateClient_validationError() throws Exception {
        mockMvc.perform(post("/clients/1")
                        .param("firstName", "Updated")
                        .param("lastName", "Name"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("client/form"))
                .andExpect(model().attribute("formAction", "/clients/1"));
    }

    @Test
    void testUpdateClient_clientNotFound() throws Exception {
        Mockito.when(clientService.updateClientById(eq(1), any(ClientCUDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/clients/1")
                        .param("firstName", "Updated")
                        .param("lastName", "Name")
                        .param("clientAddress", "Updated Address"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("notFoundException"));
    }

    @Test
    void testDeleteClient_successRedirects() throws Exception {
        ClientReadDTO dto = new ClientReadDTO();
        dto.setClientId(1);

        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.of(dto));

        mockMvc.perform(post("/clients/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients"));

        Mockito.verify(clientService).deleteClientById(1);
    }

    @Test
    void testDeleteClient_notFound_throwsException() throws Exception {
        Mockito.when(clientService.getClientById(1)).thenReturn(Optional.empty());

        mockMvc.perform(post("/clients/delete/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("notFoundException"));
    }
}
