package ord.example.LegalManagementSystem.service;

import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceCreateDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceReadDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceUpdateDTO;
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
    public InvoiceReadDTO saveInvoice(InvoiceCreateDTO invoiceCreateDTO) {
        Invoice invoice = new Invoice();
        invoice.setDueDate(invoiceCreateDTO.getDueDate());
        invoice.setAmount(invoiceCreateDTO.getAmount());

        Client client = clientRepository.findById(invoiceCreateDTO.getClientId())
                .orElseThrow(() -> new ClientNotFoundException("Client with ID " + invoiceCreateDTO.getClientId() + " not found"));

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

    public Optional<InvoiceReadDTO> updateInvoiceById(Integer invoiceId, InvoiceUpdateDTO invoiceUpdateDTO) {
        return invoiceRepository.findById(invoiceId).map(existingInvoice -> {
            existingInvoice.setAmount(invoiceUpdateDTO.getAmount());
            existingInvoice.setDueDate(invoiceUpdateDTO.getDueDate());

            return globalMapper.toInvoiceReadDto(invoiceRepository.save(existingInvoice), true);
        });
    }

    public void deleteInvoiceById(Integer invoiceId) {
        invoiceRepository.deleteById(invoiceId);
    }
}
