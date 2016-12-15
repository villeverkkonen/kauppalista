package kauppalista.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("production")
@Controller
public class TuotantoKayttajaController {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    // Tuotannossa ei listata tunnuksen luoneita käyttäjiä etusivulle.
    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public String etusivu(Model model, @ModelAttribute Kayttaja kayttaja) {
        model.addAttribute("kayttajat", null);
        return "etusivu";
    }

    // Lomakkeen kautta luodaan uusi käyttäjätunnus.
    // Jos lomakkeen kentät ei vastaa sitä, mitä Kayttaja-luokassa
    // oliomuuttujilta vaaditaan, ohjataan erilliselle
    // tunnuksenluontisivulle, missä virheilmoitus virheellisen kentän kohdalla.
    @RequestMapping(value = "/etusivu", method = RequestMethod.POST)
    public String lisaaKayttaja(Model model, @Valid @ModelAttribute Kayttaja kayttaja, BindingResult bindingResult) {

        boolean onkoVirheita = false;
        List<String> virheet = new ArrayList();

        if (bindingResult.hasErrors()) {
            virheet.add("Virhe kirjautumisessa.");
            onkoVirheita = true;
        }

        if (kayttajaRepository.findByKayttajanimi(kayttaja.getKayttajanimi()) != null) {
            virheet.add("Käyttäjänimi on varattu.");
            onkoVirheita = true;
        }

        if (kayttaja.getSalasana().equals(kayttaja.getKayttajanimi())) {
            virheet.add("Salasana ei saa olla sama kuin käyttäjänimi.");
            onkoVirheita = true;
        }

        if (kayttaja.getSalasana().length() < 8) {
            virheet.add("Salasanan vähimmäispituus on 8.");
            onkoVirheita = true;
        }

        if (!kayttaja.getSalasana().matches(".*[a-zA-Z]++.*")) {
            virheet.add("Salasanan tulee sisältää ainakin yksi kirjain väliltä a ... z tai A ... Z.");
            onkoVirheita = true;
        }

        if (!kayttaja.getSalasana().matches(".*[0-9]++.*")) {
            virheet.add("Salasanan tulee sisältää ainakin yksi numero.");
            onkoVirheita = true;
        }

        if (!kayttaja.getSalasana().matches(".*[^0-9a-zA-Z]++.*")) {
            virheet.add("Salasanan tulee sisältää ainakin yksi merkki, joka ei ole kirjain a ... z eikä numero.");
            onkoVirheita = true;
        }

        String kayttajatunnusTakaperin = new StringBuilder(kayttaja.getKayttajanimi()).reverse().toString();

        if (kayttaja.getSalasana().equals(kayttajatunnusTakaperin)) {
            virheet.add("Salasana ei saa olla käyttäjänimi takaperin.");
            onkoVirheita = true;
        }

        if (kayttaja.getKayttajanimi().contains(kayttaja.getSalasana())) {
            virheet.add("Salasana ei saa olla käyttäjätunnuksen osa.");
            onkoVirheita = true;
        }

        if (onkoVirheita) {
            model.addAttribute("virheet", virheet);
            return "tunnuksenluonti";
        }

        // Otetaan tokeniin muistiin uuden käyttäjän tiedot.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(kayttaja.getKayttajanimi(), kayttaja.getSalasana());

        // Asetetaan kryptattu salasana.
        kayttaja.setSalasana(passwordEncoder.encode(kayttaja.getSalasana()));
        kayttaja.setRooli("ADMIN");

        kayttajaRepository.save(kayttaja);

        // Autentikoidaan uusi luotu käyttäjä.
        Authentication auth = authManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/etusivu";
    }
}
