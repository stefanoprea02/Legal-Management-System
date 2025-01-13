package ord.example.LegalManagementSystem.repository;

import ord.example.LegalManagementSystem.model.Lawsuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LawsuitRepository extends JpaRepository<Lawsuit, Integer> {
}
