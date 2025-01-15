package gr.hua.dit.project.real_estates.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Controller for the home page
@Controller
@RequestMapping("/")
public class HomeController {
    
    // Returns the index page with title attribute
    @GetMapping
    public String home(Model model) {
        model.addAttribute("title", "Home");
        return "index";
    }
}