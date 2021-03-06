package kauppalista.service;

import java.util.ArrayList;
import java.util.List;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Service
public class KayttajaService {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    public String lisaaKayttaja(Model model, Kayttaja kayttaja, BindingResult bindingResult) {

        boolean onkoVirheita = false;
        List<String> virheet = new ArrayList();

        if (kayttaja == null || bindingResult == null || bindingResult.hasErrors()) {
            virheet.add("Virhe kirjautumisessa.");
            onkoVirheita = true;
        }

        if (this.kayttajaRepository.findByKayttajanimi(kayttaja.getKayttajanimi()) != null) {
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

        // Authmanager ominaisuus tesissä pois käytöstä
        // Otetaan tokeniin muistiin uuden käyttäjän tiedot.
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(kayttaja.getKayttajanimi(), kayttaja.getSalasana());

        // Asetetaan kryptattu salasana.
        kayttaja.setSalasana(this.passwordEncoder.encode(kayttaja.getSalasana()));
        kayttaja.setRooli("USER");

        this.kayttajaRepository.save(kayttaja);

        // Autentikoidaan uusi luotu käyttäjä.
        //Authmanager ominaisuus tesissä pois käytöstä
        Authentication auth = this.authManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/etusivu";
    }

    //haetaan kirjautunut käyttäjä, että saadaan kayttajaId käyttäjäsivulinkkiä varten
    public Kayttaja haeKirjautunutKayttaja() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Kayttaja kirjautunutKayttaja = kayttajaRepository.findByKayttajanimi(auth.getName());
            return kirjautunutKayttaja;
        }
        return null;
    }
}
