package ord.example.LegalManagementSystem.repository;

import ord.example.LegalManagementSystem.model.Lawsuit;
import ord.example.LegalManagementSystem.model.Lawyer;
import ord.example.LegalManagementSystem.model.LawyerLawsuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LawyerLawsuitRepository extends JpaRepository<LawyerLawsuit, Integer> {
    List<LawyerLawsuit> findByLawsuit(Lawsuit existingLawsuit);
}
