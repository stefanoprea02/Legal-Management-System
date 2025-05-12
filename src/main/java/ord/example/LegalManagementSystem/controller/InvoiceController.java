package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceCreateDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceReadDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.InvoiceNotFoundException;
import ord.example.LegalManagementSystem.service.ClientService;
import ord.example.LegalManagementSystem.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;
    private final ClientService clientService;

    public InvoiceController(InvoiceService invoiceService, ClientService clientService) {
        this.invoiceService = invoiceService;
        this.clientService = clientService;
    }

    @GetMapping("/create")
    public String showCreateInvoiceForm(Model model) {
        model.addAttribute("invoice", new InvoiceCreateDTO());
        model.addAttribute("clients", clientService.getClients());
        model.addAttribute("formAction", "/invoices");
        return "invoice/form";
    }

    @PostMapping
    public String createInvoice(@Valid @ModelAttribute("invoice") InvoiceCreateDTO invoiceCreateDTO,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "invoice/form";
        }

        InvoiceReadDTO savedInvoice = invoiceService.saveInvoice(invoiceCreateDTO);

        return "redirect:/invoices/" + savedInvoice.getInvoiceId();
    }

    @GetMapping("/{invoiceId}")
    public String getInvoiceById(@PathVariable Integer invoiceId, Model model) {
        Optional<InvoiceReadDTO> invoice = invoiceService.getInvoiceById(invoiceId);

        if (invoice.isEmpty()) {
           throw new InvoiceNotFoundException("Invoice with ID " + invoiceId + " not found");
        } else {
            model.addAttribute("invoice", invoice.get());
            return "invoice/invoice";
        }
    }

    @GetMapping
    public String getAllInvoices(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortField", defaultValue = "invoiceId") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder,
            Model model) {
        Page<InvoiceReadDTO> invoices = invoiceService.getInvoicesPage(page, sortField, sortOrder);

        model.addAttribute("invoices", invoices);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        return "invoice/invoices";
    }

    @GetMapping("/edit/{invoiceId}")
    public String showEditInvoiceForm(@PathVariable Integer invoiceId, Model model) {
        Optional<InvoiceReadDTO> invoice = invoiceService.getInvoiceById(invoiceId);

        if (invoice.isEmpty()) {
            throw new InvoiceNotFoundException("Invoice with ID " + invoiceId + " not found");
        } else {
            model.addAttribute("invoice", invoice.get());
            model.addAttribute("clients", clientService.getClients());
            model.addAttribute("formAction", "/invoices/" + invoiceId);
            return "invoice/form";
        }
    }

    @PostMapping("/{invoiceId}")
    public String updateInvoice(@PathVariable Integer invoiceId,
                                @Valid @ModelAttribute("invoice") InvoiceUpdateDTO invoiceUpdateDTO,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "invoice/form";
        }

        Optional<InvoiceReadDTO> updatedInvoice = invoiceService.updateInvoiceById(invoiceId, invoiceUpdateDTO);

        if (updatedInvoice.isEmpty()) {
            throw new InvoiceNotFoundException("Invoice with ID " + invoiceId + " not found");
        } else {
            return "redirect:/invoices/" + updatedInvoice.get().getInvoiceId();
        }
    }

    @PostMapping("/delete/{invoiceId}")
    public String deleteInvoice(@PathVariable Integer invoiceId) {
        Optional<InvoiceReadDTO> invoice = invoiceService.getInvoiceById(invoiceId);

        if (invoice.isEmpty()) {
            throw new InvoiceNotFoundException("Invoice with ID " + invoiceId + " not found");
        } else {
            invoiceService.deleteInvoiceById(invoiceId);
            return "redirect:/invoices";
        }
    }
}