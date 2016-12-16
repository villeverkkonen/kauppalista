package kauppalista.controller;

import java.util.List;
import java.util.Random;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import kauppalista.service.KauppalistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
    private TuoteRepository tuoteRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    private final String[] noname = {"HoBo", "Pokemon Trainers", "Norjalainen hiihtäjä", "Jack Bauer", "Chuck Norris", "Al Pacino", "Arnold Schwarzenegger", "Denzel Washington", "Tauski", "Darth Vader", "Arto"};

    // Listaa yhden kauppalistan tuotteet.
    @RequestMapping(value = "/{kayttajaId}/kauppalista/{kauppalistaId}", method = RequestMethod.GET)
    public String kauppalistaSivu(Model model,
            @PathVariable Long kauppalistaId, @PathVariable Long kayttajaId) {
        List<Tuote> tuotteet = this.kauppalistaService.haeBooleanillaTuotteetKauppalistalta(kauppalistaId, false);
        List<Tuote> ostetutTuotteet = this.kauppalistaService.haeBooleanillaTuotteetKauppalistalta(kauppalistaId, true);

        model.addAttribute("kauppalista", kauppalistaRepository.findOne(kauppalistaId));
        model.addAttribute("tuotteet", tuotteet);
        model.addAttribute("ostetutTuotteet", ostetutTuotteet);
        model.addAttribute("kayttaja", kayttajaRepository.findOne(kayttajaId));
        model.addAttribute("kayttajat", kauppalistaService.haeKauppalistanKayttajat(kauppalistaId));

        return "kauppalista";
    }

    // Lisää tuotteen kauppalistalle.
    @RequestMapping(value = "/{kayttajaId}/kauppalista/{kauppalistaId}", method = RequestMethod.POST)
    public String lisaaTuote(@PathVariable Long kauppalistaId,
            @RequestParam(required = false) String tuotenimi) {
        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);

        if (!tuotenimi.trim().isEmpty()) {
            Tuote t = new Tuote(tuotenimi.trim());
            tuoteRepository.save(t);

            kl.lisaaTuote(t);

            tuoteRepository.save(t);
            kauppalistaRepository.save(kl);
        }
        return "redirect:/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    // Merkataan kauppalistalla oleva tuote ostetuksi.
    // kauppalistaId tarvitaan, jotta osataan redirectata oikealle sivulle.
    @RequestMapping(value = "/{kayttajaId}/kauppalista/{kauppalistaId}/ostettu/{tuoteId}", method = RequestMethod.POST)
    public String merkkaaOstetuksi(@PathVariable Long tuoteId) {

        Tuote tuote = tuoteRepository.findById(tuoteId);
        tuote.setOstettu(true);
        tuoteRepository.save(tuote);
        return "redirect:/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    // Listaa tietyn käyttäjän kauppalistat.
    @RequestMapping(value = "/etusivu/{kayttajaId}/kauppalistat", method = RequestMethod.GET)
    public String kayttajanKauppalistaSivu(Model model, @PathVariable Long kayttajaId) {
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
        List<Kauppalista> kauppalistat = kayttaja.getKauppalista();
        model.addAttribute("kayttaja", kayttaja);
        model.addAttribute("kauppalistat", kauppalistat);

        // Näytetään testausprofiilissa myös salasanatiiviste (tätä ei ole tuotantoprofiilissa).
        model.addAttribute("salasanatiiviste", kayttajaRepository.findOne(kayttajaId).getSalasana());

        // Näytetään testausprofiilissa myös käyttäjärooli (tätä ei ole tuotantoprofiilissa).
        model.addAttribute("kayttajarooli", kayttajaRepository.findOne(kayttajaId).getRooli());
        return "kayttaja";
    }

    // Lisää tietylle käyttäjälle kauppalistan.
    @RequestMapping(value = "/etusivu/{kayttajaId}/kauppalistat", method = RequestMethod.POST)
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

        return "redirect:/{kayttajaId}/kauppalista/" + kauppalistaId;
    }

    // Valmiiseen kauppalistaan lisätään toinen/kolmas/jne. käyttäjä.
    @RequestMapping(value = "{kayttajaId}/kauppalista/{kauppalistaId}/lisaaKayttaja", method = RequestMethod.POST)
    public String lisaaKayttajaKauppalistalle(@PathVariable Long kauppalistaId,
            @PathVariable Long kayttajaId,
            @RequestParam(required = false) String kayttajatunnus) {

        if (kayttajatunnus.trim().isEmpty() || kayttajaRepository.findByKayttajanimi(kayttajatunnus) == null) {
            return "redirect:/{kayttajaId}/kauppalista/{kauppalistaId}";
        }
        Kayttaja kayttaja = kayttajaRepository.findByKayttajanimi(kayttajatunnus);

        this.kauppalistaService.lisaaKayttajaKauppalistalle(kayttaja, this.kauppalistaRepository.findOne(kauppalistaId));
        return "redirect:/{kayttajaId}/kauppalista/{kauppalistaId}";
    }
}
