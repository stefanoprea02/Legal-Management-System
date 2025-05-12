package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ord.example.LegalManagementSystem.dtos.Client.ClientCUDTO;
import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.exceptions.ClientNotFoundException;
import ord.example.LegalManagementSystem.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/create")
    public String getCreteClientForm(Model model) {
        model.addAttribute("client", new ClientCUDTO());
        model.addAttribute("formAction", "/clients");
        return "client/form";
    }

    @PostMapping
    public String createClient(@Valid @ModelAttribute("client") ClientCUDTO clientCUDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "client/form";
        }

        ClientReadDTO savedClient = clientService.saveClient(clientCUDTO);

        return "redirect:/clients/" + savedClient.getClientId();
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
    public String getAllClients(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "clientId") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            Model model) {
        Page<ClientReadDTO> clients = clientService.getClientsPage(page, sortField, sortOrder);

        model.addAttribute("clients", clients);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "client/clients";
    }

    @GetMapping("/edit/{clientId}")
    public String getEditClientForm(@PathVariable Integer clientId, Model model) {
        Optional<ClientReadDTO> client = clientService.getClientById(clientId);

        if (client.isEmpty()) {
            throw new ClientNotFoundException("Client with ID " + clientId + " not found");
        } else {
            model.addAttribute("client", client.get());
            model.addAttribute("formAction", "/clients/" + clientId);
            return "client/form";
        }
    }

    @PostMapping("/{clientId}")
    public String updateClient(@PathVariable Integer clientId, @Valid @ModelAttribute("client") ClientCUDTO clientCUDTO,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "client/form";
        }

        Optional<ClientReadDTO> updatedClient = clientService.updateClientById(clientId, clientCUDTO);

        if (updatedClient.isEmpty()) {
            throw new ClientNotFoundException("Client with ID " + clientId + " not found");
        }

        return "redirect:/clients/" + updatedClient.get().getClientId();
    }


    @PostMapping("/delete/{clientId}")
    public String deleteClient(@PathVariable Integer clientId) {
        Optional<ClientReadDTO> client = clientService.getClientById(clientId);

        if (client.isPresent()) {
            clientService.deleteClientById(clientId);
            return "redirect:/clients";
        } else {
            throw new ClientNotFoundException("Client with ID " + clientId + " not found");
        }
    }
}