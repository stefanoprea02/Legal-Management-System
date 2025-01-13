package ord.example.LegalManagementSystem.controller;

import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.model.Client;
import ord.example.LegalManagementSystem.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientReadDTO> createClient(@RequestBody ClientCUDTO clientCUDTO) {
        ClientReadDTO savedClient = clientService.saveClient(clientCUDTO);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientReadDTO> getClientById(@PathVariable Integer clientId) {
        Optional<ClientReadDTO> client = clientService.getClientById(clientId);
        return client.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ClientReadDTO>> getAllClients() {
        List<ClientReadDTO> clients = clientService.getClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientReadDTO> updateClient(@PathVariable Integer clientId, @RequestBody ClientCUDTO clientCUDTO) {
        Optional<ClientReadDTO> updatedClient = clientService.updateClientById(clientId, clientCUDTO);
        return updatedClient.map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Integer clientId) {
        try {
            clientService.deleteClientById(clientId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}