package gr.hua.dit.project.real_estates.controllers;

import gr.hua.dit.project.real_estates.entities.*;
import gr.hua.dit.project.real_estates.service.ReportService;
import gr.hua.dit.project.real_estates.service.UserService;
import gr.hua.dit.project.real_estates.service.ApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

// Controller for managing property reports and report-related operations
@Controller
@RequestMapping("/reports")
public class ReportController {

   private final ReportService reportService;
   private final UserService userService;
   private final ApplicationService applicationService;

   public ReportController(ReportService reportService, UserService userService, ApplicationService applicationService) {
       this.reportService = reportService;
       this.userService = userService;
       this.applicationService = applicationService;
   }

   // Display all reports with debug logging
   @GetMapping("/list")
   public String viewAllReports(Model model, Principal principal) {
       try {
           if (principal == null) {
               return "redirect:/login";
           }

           List<Report> reports = reportService.getAllReports();
           // Debug logging for report details
           System.out.println("Debug - Number of reports found: " + reports.size());
           for (Report report : reports) {
               System.out.println("Debug - Report ID: " + report.getReportId());
               System.out.println("Debug - Report Title: " + report.getTitle());
               System.out.println("Debug - Report Application: " + (report.getApplication() != null ? "exists" : "null"));
               if (report.getApplication() != null) {
                   System.out.println("Debug - Application Estate: " + (report.getApplication().getEstate() != null ? "exists" : "null"));
               }
           }
           model.addAttribute("reports", reports);
           return "reports/reports";
       } catch (Exception e) {
           System.err.println("Error in viewAllReports: " + e.getMessage());
           e.printStackTrace();
           model.addAttribute("error", "An error occurred while fetching reports");
           return "reports/reports";
       }
   }

   // Display single report details
   @GetMapping("/view/{reportId}")
   public String viewReport(@PathVariable Integer reportId, Model model) {
       try {
           reportService.getReportById(reportId).ifPresent(report ->
                   model.addAttribute("report", report));
           return "reports/report-details";
       } catch (Exception e) {
           System.err.println("Error in viewReport: " + e.getMessage());
           return "redirect:/";
       }
   }

   // Display form for creating new report, showing only estates with approved applications
   @GetMapping("/create")
   public String showReportForm(Model model, Principal principal) {
       try {
           if (principal == null) {
               return "redirect:/login";
           }

           List<Estate> uniqueEstates = applicationService.getUniqueEstatesFromApprovedApplications(principal.getName());
           
           if (uniqueEstates.isEmpty()) {
               model.addAttribute("error", "No approved applications found.");
               return "reports/no-applications";
           }
           
           model.addAttribute("estates", uniqueEstates);
           model.addAttribute("report", new Report());
           return "reports/create-report";
           
       } catch (Exception e) {
           System.err.println("Error in showReportForm: " + e.getMessage());
           e.printStackTrace();
       }
       return "redirect:/";
   }

   // Handle report creation for approved applications
   @PostMapping("/create")
   public String createReport(@ModelAttribute Report report, @RequestParam("estateId") Integer estateId, Principal principal) {
       try {
           List<Application> applications = applicationService.getApprovedApplicationsByEmail(principal.getName());
           Optional<Application> application = applications.stream()
                   .filter(app -> app.getEstate().getId() == estateId)
                   .findFirst();

           if (application.isPresent()) {
               Optional<User> userOptional = userService.findByEmail(principal.getName());
               if (userOptional.isPresent()) {
                   report.setUser(userOptional.get());
                   report.setApplication(application.get());
                   report.setStatus(OtherStatus.Pending);
                   reportService.createReport(report);
                   return "redirect:/reports/my-reports";
               }
           }
           return "redirect:/reports/create?error=true";
       } catch (Exception e) {
           System.err.println("Error in createReport: " + e.getMessage());
           e.printStackTrace();
           return "redirect:/reports/create?error=true";
       }
   }

   // Handle report update
   @PostMapping("/update/{reportId}")
   public String updateReport(@PathVariable Integer reportId, @ModelAttribute Report report) {
       try {
           report.setReportId(reportId);
           reportService.updateReport(report);
           return "redirect:/reports/list";
       } catch (Exception e) {
           System.err.println("Error in updateReport: " + e.getMessage());
           return "redirect:/";
       }
   }

   // Handle report deletion
   @PostMapping("/delete/{reportId}")
   public String deleteReport(@PathVariable Integer reportId) {
       try {
           reportService.deleteReport(reportId);
           return "redirect:/reports/list";
       } catch (Exception e) {
           System.err.println("Error in deleteReport: " + e.getMessage());
           return "redirect:/";
       }
   }

   // Filter reports by status
   @GetMapping("/status/{status}")
   public String filterByStatus(@PathVariable String status, Model model, Principal principal) {
       try {
           if (principal == null) {
               return "redirect:/login";
           }

           OtherStatus reportStatus = OtherStatus.valueOf(status);
           List<Report> reports = reportService.getReportsByStatus(reportStatus);
           model.addAttribute("reports", reports);
           model.addAttribute("currentStatus", status);
           return "reports/reports";
       } catch (IllegalArgumentException e) {
           System.err.println("Invalid status: " + status);
           return "redirect:/reports/list";
       } catch (Exception e) {
           System.err.println("Error in filterByStatus: " + e.getMessage());
           model.addAttribute("error", "An error occurred while filtering reports");
           return "reports/reports";
       }
   }

   // Display reports for logged-in user
   @GetMapping("/my-reports")
   public String viewMyReports(Model model, Principal principal) {
       try {
           if (principal == null) {
               return "redirect:/login";
           }
           
           String userEmail = principal.getName();
           List<Report> reports = reportService.getReportsByUserEmail(userEmail);
           model.addAttribute("reports", reports);
           return "reports/my-reports";
       } catch (Exception e) {
           System.err.println("Error in viewMyReports: " + e.getMessage());
           model.addAttribute("error", "An error occurred while fetching your reports");
           return "reports/my-reports";
       }
   }

   // Handle report approval
   @PostMapping("/approve/{id}")
   public String approveReport(@PathVariable Integer id, Model model) {
       try {
           Optional<Report> reportOpt = reportService.getReportById(id);
           if (reportOpt.isPresent()) {
               Report report = reportOpt.get();
               report.setStatus(OtherStatus.Approved);
               reportService.updateReport(report);
               return "redirect:/reports/list";
           }
           model.addAttribute("error", "Report not found");
           return "reports/reports";
       } catch (Exception e) {
           System.err.println("Error in approveReport: " + e.getMessage());
           model.addAttribute("error", "Failed to approve report");
           return "reports/reports";
       }
   }

   // Redirect to reports list
   @GetMapping
   public String redirectToList() {
       return "redirect:/reports/list";
   }
}