package kauppalista.controller;

import java.util.List;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import kauppalista.service.KauppalistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    //Listaa kaikki käyttäjän kauppalistat
    //TÄSSÄ ON ONGELMA: miten saadaan String kayttajanimi käytettäväksi?
    @RequestMapping(value = "/kauppalistat", method = RequestMethod.GET)
    public String etusivu(Model model, @RequestParam String kayttajanimi, @PathVariable Long kauppalistaId) {
        List<Kauppalista> kayttajanListat = kayttajaRepository.findByKayttajanimi(kayttajanimi).getKauppalista();
        model.addAttribute("kauppalistat", kayttajanListat);
        return "kauppalistat";
    }

    //Listaa yhden kauppalistan tuotteet
    @RequestMapping(value = "/kauppalista/{kauppalistaId}", method = RequestMethod.GET)
    public String kauppalistaSivu(Model model, @PathVariable Long kauppalistaId) {
        List<Tuote> tuotteet = this.kauppalistaService.haeBooleanillaTuotteetKauppalistalta(kauppalistaId, false);
        List<Tuote> ostetutTuotteet = this.kauppalistaService.haeBooleanillaTuotteetKauppalistalta(kauppalistaId, true);

        model.addAttribute("kauppalista", kauppalistaRepository.findOne(kauppalistaId));
        model.addAttribute("tuotteet", tuotteet);
        model.addAttribute("ostetutTuotteet", ostetutTuotteet);

        return "kauppalista";
    }

    //Lisää tuotteen kauppalistalle
    @RequestMapping(value = "/kauppalista/{kauppalistaId}", method = RequestMethod.POST)
    public String lisaaTuote(@PathVariable Long kauppalistaId, @RequestParam(required = false) String tuotenimi) {
        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);

        Tuote t = new Tuote(tuotenimi.trim());
        kl.lisaaTuote(t);

        tuoteRepository.save(t);
        kauppalistaRepository.save(kl);

        String listaId = kauppalistaId.toString();
        return "redirect:/kauppalista/" + listaId;
    }

    //Merkataan kauppalistalla oleva tuote ostetuksi
    //KauppalistaId tarvitaan, jotta osataan redirectata oikealle sivulle. En tiedä onko toteutus oikea
    @RequestMapping(value = "/kauppalista/{kauppalistaId}/ostettu/{tuoteId}", method = RequestMethod.POST)
    public String merkkaaOstetuksi(@PathVariable Long tuoteId) {

        Tuote tuote = tuoteRepository.findById(tuoteId);
        tuote.setOstettu(true);
        tuoteRepository.save(tuote);
        return "redirect:/kauppalista/{kauppalistaId}";
    }

    @RequestMapping(value = "/etusivu/{kayttajaId}/kauppalistat", method = RequestMethod.POST)
    public String luoKauppalista(@PathVariable Long kayttajaId,
            @RequestParam(required = false) String kauppalistaNimi) {
        Kauppalista kl = new Kauppalista();
        kl.setListanimi(kauppalistaNimi);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
        this.kauppalistaService.lisaaKayttajaKauppalistalle(kayttaja, kl);
        Long kauppalistaId = kl.getId();

        return "redirect:/kauppalista/" + kauppalistaId;
    }

}
