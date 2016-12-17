package kauppalista.controller;

import java.util.Random;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.service.KauppalistaService;
import kauppalista.service.LoggedInAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Profile("default")
@Controller
public class KauppalistaController {

    @Autowired
    private KauppalistaService kauppalistaService;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private LoggedInAccountService kirjautuneetService;

    private final String[] noname = {"HoBo", "Pokemon Trainers", "Norjalainen hiihtäjä", "Jack Bauer", "Chuck Norris", "Al Pacino", "Arnold Schwarzenegger", "Denzel Washington", "Tauski", "Darth Vader", "Arto"};

    // Listaa yhden kauppalistan tuotteet.
    @RequestMapping(value = "kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}", method = RequestMethod.GET)
    public String kauppalistaSivu(Model model,
            @PathVariable Long kauppalistaId, @PathVariable Long kayttajaId) {
        if (!kayttajallaOikeudet(kauppalistaId)) {
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!"); //Tähän pitää saada parempi exception!
        }

        return this.kauppalistaService.kauppalistaSivu(model, kauppalistaId, kayttajaId);
    }

    public boolean kayttajallaOikeudet(Long kauppalistaId) {
        Kayttaja kayttaja = kirjautuneetService.getAuthenticatedAccount();
        Kauppalista kauppalista = kauppalistaRepository.findOne(kauppalistaId);

        if (kayttaja.getKauppalista().contains(kauppalista)) {
            return true;
        }
        return false;
    }

    // Lisää tuotteen kauppalistalle.
    @RequestMapping(value = "/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}", method = RequestMethod.POST)
    public String lisaaTuote(@PathVariable Long kauppalistaId,
            @RequestParam(required = false) String tuotenimi) {
        if (!kayttajallaOikeudet(kauppalistaId)) {
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!"); //Tähän pitää saada parempi exception!
        }
        return this.kauppalistaService.lisaaTuote(kauppalistaId, tuotenimi);
    }

    // Merkataan kauppalistalla oleva tuote ostetuksi.
    // kauppalistaId tarvitaan, jotta osataan redirectata oikealle sivulle.
    @RequestMapping(value = "/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}/ostettu/{tuoteId}", method = RequestMethod.POST)
    public String merkkaaOstetuksi(@PathVariable Long tuoteId, @PathVariable Long kauppalistaId) {
        if (!kayttajallaOikeudet(kauppalistaId)) {
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!"); //Tähän pitää saada parempi exception!
        }
        return this.kauppalistaService.merkkaaOstetuksi(tuoteId);
    }

    // Listaa tietyn käyttäjän kauppalistat.
    @RequestMapping(value = "/kayttajat/{kayttajaId}/kauppalistat", method = RequestMethod.GET)
    public String kayttajanKauppalistaSivu(Model model, @PathVariable Long kayttajaId) {
        return this.kauppalistaService.kayttajanKauppalistaSivu(model, kayttajaId);
    }

    // Lisää tietylle käyttäjälle kauppalistan.
    @RequestMapping(value = "/kayttajat/{kayttajaId}/kauppalistat", method = RequestMethod.POST)
    public String luoKauppalista(@PathVariable Long kayttajaId,
            @RequestParam(required = false) String kauppalistaNimi) {
        if (kauppalistaNimi.trim().isEmpty()) {
            kauppalistaNimi = "The " + noname[new Random().nextInt(noname.length)] + " diet";
        }
        Kauppalista kl = new Kauppalista();
        kl.setListanimi(kauppalistaNimi);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
        this.kauppalistaService.lisaaKayttajaKauppalistalle(kayttaja, kl);
        String kauppalistaId = kl.getId().toString();

        return "redirect:/kayttajat/{kayttajaId}/kauppalista/" + kauppalistaId;
    }

    // Valmiiseen kauppalistaan lisätään toinen/kolmas/jne. käyttäjä.
    @RequestMapping(value = "/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}/lisaaKayttaja", method = RequestMethod.POST)
    public String lisaaKayttajaKauppalistalle(@PathVariable Long kauppalistaId,
            @PathVariable Long kayttajaId,
            @RequestParam(required = false) String kayttajatunnus) {
        if (!kayttajallaOikeudet(kauppalistaId)) {
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!"); //Tähän pitää saada parempi exception!
        }
        return this.kauppalistaService.lisaaKayttajaKauppalistalle(kauppalistaId, kayttajaId, kayttajatunnus);
    }
}
