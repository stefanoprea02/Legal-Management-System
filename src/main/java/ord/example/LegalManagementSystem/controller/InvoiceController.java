package ord.example.LegalManagementSystem.controller;

import jakarta.validation.Valid;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceCreateDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceReadDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceUpdateDTO;
import ord.example.LegalManagementSystem.exceptions.InvoiceNotFoundException;
import ord.example.LegalManagementSystem.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceReadDTO> createInvoice(@RequestBody @Valid InvoiceCreateDTO invoiceCreateDTO) {
        InvoiceReadDTO savedInvoice = invoiceService.saveInvoice(invoiceCreateDTO);
        return new ResponseEntity<>(savedInvoice, HttpStatus.CREATED);
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
    public String getAllInvoices(Model model) {
        List<InvoiceReadDTO> invoices = invoiceService.getInvoices();

        model.addAttribute("invoices", invoices);
        return "invoice/invoices";
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceReadDTO> updateInvoice(@PathVariable Integer invoiceId, @Valid @RequestBody InvoiceUpdateDTO invoiceUpdateDTO) {
        Optional<InvoiceReadDTO> updatedInvoice = invoiceService.updateInvoiceById(invoiceId, invoiceUpdateDTO);
        return updatedInvoice.map(i -> new ResponseEntity<>(i, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Integer invoiceId) {
        try {
            invoiceService.deleteInvoiceById(invoiceId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}