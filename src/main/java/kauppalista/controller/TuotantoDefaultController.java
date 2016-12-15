package kauppalista.controller;

import javax.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("production")
@Controller
public class TuotantoDefaultController {

    @PostConstruct
    public void init() {
    }

    // Pyyntö juuripolkuun ohjaa etusivulle.
    @RequestMapping("/")
    public String ohjaaEtusivulle() {
        return "redirect:/etusivu";
    }

    @RequestMapping(value = "/kirjautuminen", method = RequestMethod.GET)
    public String kirjautumisSivu() {
        return "kirjautuminen";
    }
}
