package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.Application;
import gr.hua.dit.project.real_estates.entities.OtherStatus;
import gr.hua.dit.project.real_estates.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {
    // Find reports by their status
    List<Report> findByStatus(OtherStatus status);

    // Find reports by user's email
    @Query("SELECT r FROM Report r WHERE r.user.email = :email")
    List<Report> findByUserEmail(@Param("email") String email);
    
    // Find reports for a specific application
    List<Report> findByApplication(Application application);
}