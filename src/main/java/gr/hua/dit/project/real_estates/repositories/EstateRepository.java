package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.Estate;
import gr.hua.dit.project.real_estates.entities.EstateStatusForAdmin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EstateRepository extends JpaRepository<Estate, Integer> {
    // Find estates by their admin status
    List<Estate> findByEstateStatusForAdmin(@NotBlank EstateStatusForAdmin estateStatusForAdmin);

}