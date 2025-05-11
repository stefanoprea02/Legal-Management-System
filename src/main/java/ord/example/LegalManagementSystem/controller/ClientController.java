package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.exceptions.ClientNotFoundException;
import ord.example.LegalManagementSystem.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/create")
    public String getClientForm(Model model) {
        model.addAttribute("client", new ClientCUDTO());
        return "client/form";
    }

    @PostMapping
    public String createClient(@Valid @ModelAttribute("client") ClientCUDTO clientCUDTO, BindingResult bindingResult) {
        ClientReadDTO savedClient = clientService.saveClient(clientCUDTO);

        if (bindingResult.hasErrors()) {
            return "client/form";
        } else {
            return "redirect:/clients/" + savedClient.getClientId();
        }
    }

    @GetMapping("/{clientId}")
    public String getClientById(@PathVariable Integer clientId, Model model) {
        Optional<ClientReadDTO> client = clientService.getClientById(clientId);

        if (client.isEmpty()) {
            throw new ClientNotFoundException("Client with ID " + clientId + " not found");
        } else {
            model.addAttribute("client", client.get());
            return "client/client";
        }
    }

    @GetMapping
    public String getAllClients(Model model) {
        List<ClientReadDTO> clients = clientService.getClients();

        model.addAttribute("clients", clients);
        return "client/clients";
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientReadDTO> updateClient(@PathVariable Integer clientId, @Valid @RequestBody ClientCUDTO clientCUDTO) {
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