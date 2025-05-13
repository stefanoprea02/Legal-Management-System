package ord.example.LegalManagementSystem.mappers;

import ord.example.LegalManagementSystem.dtos.Client.ClientReadDTO;
import ord.example.LegalManagementSystem.dtos.Hearing.HearingReadDTO;
import ord.example.LegalManagementSystem.dtos.Invoice.InvoiceReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawsuit.LawsuitReadDTO;
import ord.example.LegalManagementSystem.dtos.Lawyer.LawyerReadDTO;
import ord.example.LegalManagementSystem.dtos.LawyerLawsuit.LawyerLawsuitReadDTO;
import ord.example.LegalManagementSystem.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class GlobalMapper {
    public ClientReadDTO toClientReadDto(Client client, boolean expandInvoices, boolean expandLawsuits) {
        if (client == null) {
            return null;
        }

        ClientReadDTO dto = new ClientReadDTO();
        dto.setClientId(client.getClientId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setClientAddress(client.getClientAddress());

        if (expandInvoices && client.getInvoices() != null) {
            List<InvoiceReadDTO> invoiceDtos = client.getInvoices().stream()
                    .map(invoice -> toInvoiceReadDto(invoice, false))
                    .collect(Collectors.toList());
            dto.setInvoices(invoiceDtos);
        }

        if (expandLawsuits && client.getLawsuits() != null) {
            List<LawsuitReadDTO> lawsuitDtos = client.getLawsuits().stream()
                    .map(lawsuit -> toLawsuitReadDto(lawsuit, false, false, false, false))
                    .collect(Collectors.toList());
            dto.setLawsuits(lawsuitDtos);
        }

        return dto;
    }

    public HearingReadDTO toHearingReadDto(Hearing hearing, boolean expandLawsuit) {
        if (hearing == null) {
            return null;
        }

        HearingReadDTO dto = new HearingReadDTO();
        dto.setHearingId(hearing.getHearingId());
        dto.setDateTime(hearing.getDateTime());
        dto.setAppointmentAddress(hearing.getAppointmentAddress());

        if (expandLawsuit && hearing.getLawsuit() != null) {
            LawsuitReadDTO lawsuitDto = toLawsuitReadDto(hearing.getLawsuit(), false, false, false, false);
            dto.setLawsuit(lawsuitDto);
        }

        return dto;
    }

    public InvoiceReadDTO toInvoiceReadDto(Invoice invoice, boolean expandClient) {
        if (invoice == null) {
            return null;
        }

        InvoiceReadDTO dto = new InvoiceReadDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setAmount(invoice.getAmount());
        dto.setDueDate(invoice.getDueDate());

        if (expandClient && invoice.getClient() != null) {
            ClientReadDTO clientDto = toClientReadDto(invoice.getClient(), false, false);
            dto.setClient(clientDto);
        }

        return dto;
    }

    public LawsuitReadDTO toLawsuitReadDto(Lawsuit lawsuit,
                                           boolean expandLawyerLawsuit,
                                           boolean expandHearing,
                                           boolean expandClient,
                                           boolean includeLawsuitData) {
        if (lawsuit == null) {
            return null;
        }

        LawsuitReadDTO dto = new LawsuitReadDTO();
        dto.setLawsuitId(lawsuit.getLawsuitId());
        dto.setReason(lawsuit.getReason());
        dto.setOpposingParty(lawsuit.getOpposingParty());

        if (expandLawyerLawsuit && lawsuit.getLawyerLawsuits() != null) {
            List<LawyerLawsuitReadDTO> lawyerLawsuitDtos = lawsuit.getLawyerLawsuits().stream()
                    .map(l -> toLawyerLawsuitReadDto(l, true, false))
                    .collect(Collectors.toList());
            dto.setLawyerLawsuits(lawyerLawsuitDtos);
        }

        if (expandHearing && lawsuit.getHearing() != null) {
            HearingReadDTO hearingReadDTO = toHearingReadDto(lawsuit.getHearing(), false);
            dto.setHearing(hearingReadDTO);
        }

        if (expandClient && lawsuit.getClient() != null) {
            ClientReadDTO clientReadDTO = toClientReadDto(lawsuit.getClient(), false, false);
            dto.setClient(clientReadDTO);
        }

        if (includeLawsuitData && lawsuit.getLawsuitData() != null) {
            dto.setLawsuitData(lawsuit.getLawsuitData());
        }

        return dto;
    }

    public LawyerLawsuitReadDTO toLawyerLawsuitReadDto(LawyerLawsuit lawyerLawsuit, boolean expandLawyer, boolean expandLawsuit) {
        if (lawyerLawsuit == null) {
            return null;
        }

        LawyerLawsuitReadDTO dto = new LawyerLawsuitReadDTO();
        dto.setId(lawyerLawsuit.getId());
        dto.setHoursWorked(lawyerLawsuit.getHoursWorked());

        if (expandLawyer && lawyerLawsuit.getLawyer() != null) {
            dto.setLawyer(toLawyerReadDto(lawyerLawsuit.getLawyer(), false));
        }

        if(expandLawsuit && lawyerLawsuit.getLawsuit() != null) {
            dto.setLawsuit(toLawsuitReadDto(lawyerLawsuit.getLawsuit(),false, true, true, false));
        }

        return dto;
    }

    public LawyerReadDTO toLawyerReadDto(Lawyer lawyer, boolean expandLawyerLawsuit) {
        if (lawyer == null) {
            return null;
        }

        LawyerReadDTO dto = new LawyerReadDTO();
        dto.setLawyerId(lawyer.getLawyerId());
        dto.setFirstName(lawyer.getFirstName());
        dto.setLastName(lawyer.getLastName());
        dto.setHireDate(lawyer.getHireDate());
        dto.setLawyerType(lawyer.getLawyerType());
        dto.setHourlyRate(lawyer.getHourlyRate());
        dto.setCommission(lawyer.getCommission());
        dto.setLawyerAddress(lawyer.getLawyerAddress());

        if(expandLawyerLawsuit && lawyer.getLawyerLawsuits() != null) {
            List<LawyerLawsuitReadDTO> lawyerLawsuitDtos = lawyer.getLawyerLawsuits().stream()
                    .map(l -> toLawyerLawsuitReadDto(l, false, true))
                    .collect(Collectors.toList());
            dto.setLawyerLawsuits(lawyerLawsuitDtos);
        }

        return dto;
    }
}
