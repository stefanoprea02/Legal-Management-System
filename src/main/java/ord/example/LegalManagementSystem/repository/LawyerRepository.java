package ord.example.LegalManagementSystem.repository;

import ord.example.LegalManagementSystem.model.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawyerRepository extends JpaRepository<Lawyer, Integer> {
}
