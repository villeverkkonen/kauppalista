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

        // Luodaan valmiiksi USER-oikeuksinen testikäyttäjä.
        Kayttaja user1 = new Kayttaja();
        user1.setKayttajanimi("user1");
        user1.setSalasana(passwordEncoder.encode("abc123!!"));
        user1.setRooli("USER");
        kayttajaRepository.save(user1);

        // Luodaan toinenkin USER-oikeuksinen testikäyttäjä.
        Kayttaja user2 = new Kayttaja();
        user2.setKayttajanimi("user2");
        user2.setSalasana(passwordEncoder.encode("abc123!!"));
        user2.setRooli("USER");
        kayttajaRepository.save(user2);

        // Luodaan adminille pari testikauppalistaa.
        Kauppalista kl = new Kauppalista();
        kl.setListanimi("lista 1");
        this.kauppalistaService.lisaaKayttajaKauppalistalle(user1, kl);

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
        this.kauppalistaService.lisaaKayttajaKauppalistalle(user1, kl2);
    }

    // Pyyntö juuripolkuun ohjaa etusivulle.
    @RequestMapping("*")
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
