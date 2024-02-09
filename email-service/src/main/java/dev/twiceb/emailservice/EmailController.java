package dev.twiceb.emailservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EmailController {

    @GetMapping("/test")
    public ModelAndView test() {
        // You can add model attributes here if needed
        return new ModelAndView("deviceVerification-template");
    }
}
