package ord.example.LegalManagementSystem.repository;

import ord.example.LegalManagementSystem.model.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, Integer> {
}
