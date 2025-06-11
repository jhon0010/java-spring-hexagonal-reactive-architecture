package com.crm.validation.lead.cli;

import com.crm.validation.lead.application.services.LeadValidatorUseCase;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LeadCrmValidatorCli implements CommandLineRunner {

    private final LeadValidatorUseCase validator;
    private final Scanner scanner = new Scanner(System.in);


    @Override
    public void run(String... args) {
        System.out.println("🧪 Lead CRM Validator CLI");

        while (true) {

            System.out.print("\nEnter lead name (or 'exit'): ");
            String name = scanner.nextLine();
            if ("exit".equalsIgnoreCase(name)) break;

            try {
                // LeadDto leadDto = getLeadFromConsole();
                LeadDto leadDto = getDefaultLead(); // TODO Delete after testing
                System.out.println("Validating lead..." + leadDto.toString());

                validator.promoteLeadToProspect(Lead.fromDto(leadDto))
                        .doOnError(e -> System.out.println("❌ Error: " + e.getMessage()))
                        .subscribe(result -> System.out.println("✅ Result: " + result));
            } catch (Exception e) {
                System.out.println("⚠️ Invalid input: " + e.getMessage());
            }
        }
        System.out.println("👋 Exiting CLI");
        System.exit(1);
    }

    private LeadDto getLeadFromConsole() {
        System.out.print("\nEnter lead name (or 'exit'): ");
        String name = scanner.nextLine();

        System.out.print("Enter birthdate (yyyy-MM-dd): ");
        String birthdateStr = scanner.nextLine();

        System.out.println("Enter your email : ");
        String email = scanner.nextLine();

        System.out.println("Enter your phone number with country code +1 232424 for example : ");
        String phone = scanner.nextLine();

        return LeadDto
                .builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .birthdate(LocalDate.parse(birthdateStr))
                .phoneNumber(phone)
                .build();
    }

    private static LeadDto getDefaultLead() {
        return LeadDto
                .builder()
                .id(UUID.randomUUID().toString())
                .name("Jhon")
                .email("jhon@gmail.com")
                .birthdate(LocalDate.parse("1990-11-16"))
                .phoneNumber("+1234567890") // Default phone number for simplicity
                .build();
    }

}