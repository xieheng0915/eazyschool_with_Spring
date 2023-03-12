package com.eazybytes.eazyschool.controller;

import com.eazybytes.eazyschool.model.Person;
import com.eazybytes.eazyschool.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class DashboardController {

    @Autowired
    PersonRepository personRepository;

    @Value("${eazyschool.pageSize}")
    private int defaultPageSize;

    @Value("${eazyschool.contact.successMsg}")
    private String message;

    @Autowired
    private Environment environment;

    @RequestMapping("/dashboard")
    public String displayDashboard(Model model, Authentication authentication, HttpSession session){
        Person person = personRepository.readByEmail(authentication.getName());
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", authentication.getAuthorities().toString());
        session.setAttribute("loggedInPerson", person);
        logMessage();
        return "dashboard.html";
    }

    private void logMessage() {
        log.error("Error message from the Dashboard page");
        log.warn("Warning message from Dashboard page");
        log.info("Info message from Dashboard page");
        log.debug("Debug message from Dashboard page");
        log.trace("Trace message from Dashboard page");

        log.error("Default Page Size value with @value annotation is: " + defaultPageSize);
        log.error("SuccessMsg value with @value annotation is: " + message);

        log.error("default page size value with Environment is: " + environment.getProperty("eazyschool.pageSize"));
        log.error("SuccessMsg value with Environment is: " + environment.getProperty("eazyschool.contact.successMsg"));
        log.error("Java Home environment value with Environment is: " + environment.getProperty("JAVA_HOME"));
    }

}
