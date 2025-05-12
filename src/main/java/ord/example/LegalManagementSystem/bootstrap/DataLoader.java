package ord.example.LegalManagementSystem.bootstrap;

import lombok.AllArgsConstructor;
import ord.example.LegalManagementSystem.model.security.Authority;
import ord.example.LegalManagementSystem.model.security.User;
import ord.example.LegalManagementSystem.repository.security.AuthorityRepository;
import ord.example.LegalManagementSystem.repository.security.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Profile("mysql")
public class DataLoader implements CommandLineRunner {
    private AuthorityRepository authorityRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private void loadUserData() {
        if (userRepository.count() == 0){
            Authority adminRole = authorityRepository.save(Authority.builder().role("ROLE_ADMIN").build());
            Authority guestRole = authorityRepository.save(Authority.builder().role("ROLE_GUEST").build());

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("12345"))
                    .authority(adminRole)
                    .build();

            User guest = User.builder()
                    .username("guest")
                    .password(passwordEncoder.encode("12345"))
                    .authority(guestRole)
                    .build();

            userRepository.save(admin);
            userRepository.save(guest);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
    }
}