package kauppalista.controller;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DefaultController {

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
        // Tarvitaan KauppalistaService luokka joka hoitaa kauppalistan ja käyttäjän lisäyksen toisiinsa
        Kauppalista kl = new Kauppalista();
        kl.setListanimi("lista 1");
        kl.setKayttaja(admin);
//        Tuote t = new Tuote("banaani");
//        tuoteRepository.save(t);
//        kl.getOstettavatTuotteet().add(t);
        kauppalistaRepository.save(kl);

        Kauppalista kl2 = new Kauppalista();
        kl2.setListanimi("lista 2");
        kl2.setKayttaja(admin);
        kauppalistaRepository.save(kl2);

        ArrayList<Kauppalista> listat = new ArrayList();
        listat.add(kl);
        listat.add(kl2);
        admin.setKauppalistat(listat);

        kayttajaRepository.save(admin);
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
