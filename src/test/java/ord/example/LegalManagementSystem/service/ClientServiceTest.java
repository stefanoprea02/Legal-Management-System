package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Client;
import ord.example.LegalManagementSystem.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private GlobalMapper globalMapper;

    @InjectMocks
    private ClientService clientService;

    @Test
    void testSaveClient_success() {
        ClientCUDTO clientCUDTO = new ClientCUDTO();
        clientCUDTO.setFirstName("John");
        clientCUDTO.setLastName("Doe");
        clientCUDTO.setClientAddress("123 Main Street");

        Client savedClient = new Client();
        savedClient.setClientId(1);
        savedClient.setFirstName("John");
        savedClient.setLastName("Doe");
        savedClient.setClientAddress("123 Main Street");

        ClientReadDTO clientReadDTO = new ClientReadDTO();
        clientReadDTO.setClientId(1);
        clientReadDTO.setFirstName("John");
        clientReadDTO.setLastName("Doe");
        clientReadDTO.setClientAddress("123 Main Street");

        Mockito.when(clientRepository.save(any(Client.class))).thenReturn(savedClient);
        Mockito.when(globalMapper.toClientReadDto(savedClient, true, true)).thenReturn(clientReadDTO);

        ClientReadDTO result = clientService.saveClient(clientCUDTO);

        assertNotNull(result);
        assertEquals(1, result.getClientId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("123 Main Street", result.getClientAddress());

        Mockito.verify(clientRepository, times(1)).save(any(Client.class));
        Mockito.verify(globalMapper, times(1)).toClientReadDto(savedClient, true, true);
    }

    @Test
    void testGetClientById_found() {
        int clientId = 1;

        Client client = new Client();
        client.setClientId(clientId);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setClientAddress("123 Main Street");

        ClientReadDTO clientReadDTO = new ClientReadDTO();
        clientReadDTO.setClientId(clientId);
        clientReadDTO.setFirstName("John");
        clientReadDTO.setLastName("Doe");
        clientReadDTO.setClientAddress("123 Main Street");

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        Mockito.when(globalMapper.toClientReadDto(client, true, true)).thenReturn(clientReadDTO);

        Optional<ClientReadDTO> result = clientService.getClientById(clientId);

        assertTrue(result.isPresent());
        assertEquals(clientId, result.get().getClientId());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());

        Mockito.verify(clientRepository, times(1)).findById(clientId);
        Mockito.verify(globalMapper, times(1)).toClientReadDto(client, true, true);
    }

    @Test
    void testGetClientById_notFound() {
        int clientId = 1;

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        Optional<ClientReadDTO> result = clientService.getClientById(clientId);

        assertFalse(result.isPresent());
        Mockito.verify(clientRepository, times(1)).findById(clientId);
        Mockito.verifyNoInteractions(globalMapper);
    }

    @Test
    void testGetClients_success() {
        Client client1 = new Client();
        client1.setClientId(1);
        client1.setFirstName("John");
        client1.setLastName("Doe");

        Client client2 = new Client();
        client2.setClientId(2);
        client2.setFirstName("Jane");
        client2.setLastName("Smith");

        List<Client> clients = Arrays.asList(client1, client2);

        ClientReadDTO clientReadDTO1 = new ClientReadDTO();
        clientReadDTO1.setClientId(1);
        clientReadDTO1.setFirstName("John");
        clientReadDTO1.setLastName("Doe");

        ClientReadDTO clientReadDTO2 = new ClientReadDTO();
        clientReadDTO2.setClientId(2);
        clientReadDTO2.setFirstName("Jane");
        clientReadDTO2.setLastName("Smith");

        Mockito.when(clientRepository.findAll()).thenReturn(clients);
        Mockito.when(globalMapper.toClientReadDto(client1, true, true)).thenReturn(clientReadDTO1);
        Mockito.when(globalMapper.toClientReadDto(client2, true, true)).thenReturn(clientReadDTO2);

        List<ClientReadDTO> result = clientService.getClients();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());

        Mockito.verify(clientRepository, times(1)).findAll();
        Mockito.verify(globalMapper, times(2)).toClientReadDto(any(Client.class), eq(true), eq(true));
    }

    @Test
    void testUpdateClientById_success() {
        int clientId = 1;

        Client existingClient = new Client();
        existingClient.setClientId(clientId);
        existingClient.setFirstName("Old");
        existingClient.setLastName("Name");

        Client updatedClient = new Client();
        updatedClient.setClientId(clientId);
        updatedClient.setFirstName("New");
        updatedClient.setLastName("Name");

        ClientCUDTO clientCUDTO = new ClientCUDTO();
        clientCUDTO.setFirstName("New");
        clientCUDTO.setLastName("Name");

        ClientReadDTO updatedDTO = new ClientReadDTO();
        updatedDTO.setClientId(clientId);
        updatedDTO.setFirstName("New");
        updatedDTO.setLastName("Name");

        Mockito.when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        Mockito.when(clientRepository.save(existingClient)).thenReturn(updatedClient);
        Mockito.when(globalMapper.toClientReadDto(updatedClient, true, true)).thenReturn(updatedDTO);

        Optional<ClientReadDTO> result = clientService.updateClientById(clientId, clientCUDTO);

        assertTrue(result.isPresent());
        assertEquals("New", result.get().getFirstName());

        Mockito.verify(clientRepository, times(1)).findById(clientId);
        Mockito.verify(clientRepository, times(1)).save(existingClient);
        Mockito.verify(globalMapper, times(1)).toClientReadDto(updatedClient, true, true);
    }

    @Test
    void testDeleteClientById_success() {
        int clientId = 1;

        Mockito.doNothing().when(clientRepository).deleteById(clientId);

        clientService.deleteClientById(clientId);

        Mockito.verify(clientRepository, times(1)).deleteById(clientId);
    }
}
