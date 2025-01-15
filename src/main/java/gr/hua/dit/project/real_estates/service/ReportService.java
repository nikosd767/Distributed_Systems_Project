package gr.hua.dit.project.real_estates.service;

import gr.hua.dit.project.real_estates.entities.Report;
import gr.hua.dit.project.real_estates.entities.OtherStatus;
import gr.hua.dit.project.real_estates.repositories.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    // Create new report
    public Report createReport(Report report) {
        return reportRepository.save(report);
    }

    // Delete report by ID
    public void deleteReport(Integer reportId) {
        reportRepository.deleteById(reportId);
    }

    // Update existing report
    public Report updateReport(Report report) {
        return reportRepository.save(report);
    }

    // Get all reports
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // Get reports by user email
    public List<Report> getReportsByUserEmail(String email) {
        return reportRepository.findByUserEmail(email);
    }

    // Get report by ID
    public Optional<Report> getReportById(Integer reportId) {
        return reportRepository.findById(reportId);
    }

    // Get reports by status
    public List<Report> getReportsByStatus(OtherStatus status) {
        return reportRepository.findByStatus(status);
    }
}