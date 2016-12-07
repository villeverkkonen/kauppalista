package kauppalista.controller;

import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Listaa kaikki tunnuksen luoneet käyttäjät etusivulle
    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public String etusivu(Model model) {
        model.addAttribute("kayttajat", kayttajaRepository.findAll());
        return "etusivu";
    }

    //Lomakkeen kautta luodaan uusi käyttäjätunnus
    @RequestMapping(value = "/etusivu", method = RequestMethod.POST)
    public String lisaaKayttaja(@RequestParam(required = false) String kayttajanimi, String salasana) {
        Kayttaja kayttaja = new Kayttaja(kayttajanimi); //salasana vielä tyhjä
        kayttaja.setSalasana(passwordEncoder.encode(salasana)); //asetetaan kryptattu salasana

        kayttajaRepository.save(kayttaja);
        return "redirect:/etusivu";
    }

    //Näyttää yhden käyttäjän käyttäjäsivun ja tiedot käyttäjästä
    @RequestMapping(value = "/etusivu/{kayttajaId}", method = RequestMethod.GET)
    public String kayttajaSivu(Model model, @PathVariable Long kayttajaId) {
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
        model.addAttribute("kayttaja", kayttaja);
        return "kayttaja";
    }
}
