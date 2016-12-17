package kauppalista.controller;

import javax.annotation.PostConstruct;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.domain.Tuote;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import kauppalista.service.KauppalistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("default")
@Controller
public class DefaultController {

    @Autowired
    private KauppalistaService kauppalistaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private TuoteRepository tuoteRepository;

    @PostConstruct
    public void init() {

        // Luodaan valmiiksi ADMIN-oikeuksinen testikäyttäjä.
        Kayttaja admin = new Kayttaja();
        admin.setKayttajanimi("admin");
        admin.setSalasana(passwordEncoder.encode("abc123!!"));
        admin.setRooli("ADMIN");
        kayttajaRepository.save(admin);

        Kayttaja admin2 = new Kayttaja();
        admin2.setKayttajanimi("admin2");
        admin2.setSalasana(passwordEncoder.encode("abc123!!"));
        admin2.setRooli("ADMIN");
        kayttajaRepository.save(admin2);

        // Luodaan adminille pari testikauppalistaa.
        Kauppalista kl = new Kauppalista();
        kl.setListanimi("lista 1");
        this.kauppalistaService.lisaaKayttajaKauppalistalle(admin, kl);

        // Lisätään kauppalistalle banaani.
        Tuote t = new Tuote("banaani");
        tuoteRepository.save(t);

        this.kauppalistaService.lisaaTuoteKauppalistalle(t, kl);

        // Lisätään kauppalistalle maito.
        t = new Tuote("maito");
        tuoteRepository.save(t);
        this.kauppalistaService.lisaaTuoteKauppalistalle(t, kl);

        // Luodaan toinenkin kauppalista ja jätetään se tyhjäksi.
        Kauppalista kl2 = new Kauppalista();
        kl2.setListanimi("lista 2");
        this.kauppalistaService.lisaaKayttajaKauppalistalle(admin, kl2);
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

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginOhjaaKirjautumiseen() {
        return "kirjautuminen";
    }
}
