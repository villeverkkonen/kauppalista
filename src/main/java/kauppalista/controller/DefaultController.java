package kauppalista.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    //luodaan valmiiksi ADMIN-oikeuksinen testikäyttäjä
    @PostConstruct
    public void init() {
        Kayttaja admin = new Kayttaja();
        admin.setKayttajanimi("admin");
        admin.setSalasana(passwordEncoder.encode("admin"));
        admin.setRooli("ADMIN");
        kayttajaRepository.save(admin);
    }

    //pyyntö juuripolkuun ohjaa etusivulle
    @RequestMapping("/")
    public String ohjaaEtusivulle() {
        return "redirect:/etusivu";
    }

    //Uloskirjautumisen käsittely, ei tee mitään vielä
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/etusivu";
    }
    
    //Sisäänkirjautumisen käsittely ja login.html sivulle ohjaaminen
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

}
