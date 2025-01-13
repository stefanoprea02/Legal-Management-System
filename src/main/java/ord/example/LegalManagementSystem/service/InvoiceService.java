package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceCUDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceReadDTO;
import ord.example.LegalManagementSystem.exceptions.ClientNotFoundException;
import ord.example.LegalManagementSystem.mappers.GlobalMapper;
import ord.example.LegalManagementSystem.model.Invoice;
import ord.example.LegalManagementSystem.model.Client;
import ord.example.LegalManagementSystem.repository.InvoiceRepository;
import ord.example.LegalManagementSystem.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final GlobalMapper globalMapper;

    public InvoiceService(InvoiceRepository invoiceRepository, ClientRepository clientRepository, GlobalMapper globalMapper) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.globalMapper = globalMapper;
    }

    @Transactional
    public InvoiceReadDTO saveInvoice(InvoiceCUDTO invoiceCUDTO) {
        Invoice invoice = new Invoice();
        invoice.setDueDate(invoiceCUDTO.getDueDate());
        invoice.setAmount(invoice.getAmount());

        Client client = clientRepository.findById(invoiceCUDTO.getClientId())
                .orElseThrow(() -> new ClientNotFoundException("Client with ID " + invoiceCUDTO.getClientId() + " not found"));

        invoice.setClient(client);

        return globalMapper.toInvoiceReadDto(invoiceRepository.save(invoice), true);
    }

    public Optional<InvoiceReadDTO> getInvoiceById(Integer invoiceId) {
        return invoiceRepository.findById(invoiceId).map(i -> globalMapper.toInvoiceReadDto(i, true));
    }

    public List<InvoiceReadDTO> getInvoices() {
        return invoiceRepository.findAll().stream()
                .map(i -> globalMapper.toInvoiceReadDto(i, true))
                .collect(Collectors.toList());
    }

    public Optional<InvoiceReadDTO> updateInvoiceById(Integer invoiceId, InvoiceCUDTO invoiceCUDTO) {
        return invoiceRepository.findById(invoiceId).map(existingInvoice -> {
            existingInvoice.setAmount(invoiceCUDTO.getAmount());
            existingInvoice.setDueDate(invoiceCUDTO.getDueDate());

            return globalMapper.toInvoiceReadDto(invoiceRepository.save(existingInvoice), true);
        });
    }

    public void deleteInvoiceById(Integer invoiceId) {
        invoiceRepository.deleteById(invoiceId);
    }
}
