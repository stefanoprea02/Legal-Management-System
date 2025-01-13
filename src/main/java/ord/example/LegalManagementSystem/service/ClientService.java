package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Client;
import ord.example.LegalManagementSystem.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final GlobalMapper globalMapper;

    public ClientService(ClientRepository clientRepository, GlobalMapper globalMapper) {
        this.clientRepository = clientRepository;
        this.globalMapper = globalMapper;
    }

    public ClientReadDTO saveClient(ClientCUDTO clientCUDTO) {
        Client client = new Client();
        client.setFirstName(clientCUDTO.getFirstName());
        client.setLastName(clientCUDTO.getLastName());
        client.setClientAddress(clientCUDTO.getClientAddress());

        return globalMapper.toClientReadDto(clientRepository.save(client), true, true);
    }

    public Optional<ClientReadDTO> getClientById(Integer clientId) {
        return clientRepository.findById(clientId).map(client -> globalMapper.toClientReadDto(client, true, true));
    }

    public List<ClientReadDTO> getClients() {
        return clientRepository.findAll().stream()
                .map(c -> globalMapper.toClientReadDto(c, true, true))
                .collect(Collectors.toList());
    }

    public Optional<ClientReadDTO> updateClientById(Integer clientId, ClientCUDTO clientCUDTO) {
        return clientRepository.findById(clientId).map(existingClient -> {
            existingClient.setClientAddress(clientCUDTO.getClientAddress());
            existingClient.setLastName(clientCUDTO.getLastName());
            existingClient.setFirstName(clientCUDTO.getFirstName());

            return globalMapper.toClientReadDto(clientRepository.save(existingClient), true, true);
        });
    }

    public void deleteClientById(Integer clientId) {
        clientRepository.deleteById(clientId);
    }
}
