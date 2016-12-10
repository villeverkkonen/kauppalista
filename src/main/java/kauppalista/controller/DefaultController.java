package kauppalista.controller;

import javax.annotation.PostConstruct;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import kauppalista.service.KauppalistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {

    @Autowired
    private KauppalistaService kauppalistaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private TuoteRepository tuoteRepository;

    //luodaan valmiiksi ADMIN-oikeuksinen testikäyttäjä
    @PostConstruct
    public void init() {
        Kayttaja admin = new Kayttaja();
        admin.setKayttajanimi("admin");
        admin.setSalasana(passwordEncoder.encode("admin"));
        admin.setRooli("ADMIN");
        kayttajaRepository.save(admin);

        //luodaan adminille pari testikauppalistaa
        Kauppalista kl = new Kauppalista();
        kl.setListanimi("lista 1");
        this.kauppalistaService.lisaaKayttajaKauppalistalle(admin, kl);

        Tuote t = new Tuote("banaani");
        tuoteRepository.save(t);

        this.kauppalistaService.lisaaTuoteKauppalistalle(t, kl);

        t = new Tuote("maito");
        tuoteRepository.save(t);
        this.kauppalistaService.lisaaTuoteKauppalistalle(t, kl);

        Kauppalista kl2 = new Kauppalista();
        kl2.setListanimi("lista 2");
        this.kauppalistaService.lisaaKayttajaKauppalistalle(admin, kl2);
//
//        admin.lisaaKauppalista(kl);
//        admin.lisaaKauppalista(kl2);
//
//        kayttajaRepository.save(admin);
    }

    //pyyntö juuripolkuun ohjaa etusivulle
    @RequestMapping("/")
    public String ohjaaEtusivulle() {
        return "redirect:/etusivu";
    }

    @RequestMapping(value = "/kirjautuminen", method = RequestMethod.GET)
    public String kirjautumisSivu() {
        return "kirjautuminen";
    }
}
