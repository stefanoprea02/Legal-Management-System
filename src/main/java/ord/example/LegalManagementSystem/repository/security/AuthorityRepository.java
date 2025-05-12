package ord.example.LegalManagementSystem.repository.security;

import ord.example.LegalManagementSystem.model.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
}