package kauppalista.service;

import java.util.ArrayList;
import java.util.List;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class KauppalistaService {

    @Autowired
    private TuoteRepository tuoteRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private LoggedInAccountService kirjautuneetService;

    public KauppalistaService() {
    }

    public List<Tuote> haeBooleanillaTuotteetKauppalistalta(Long kauppalistaId, boolean b) {
        if (kauppalistaId == null) {
            return new ArrayList<>();
        }

        List<Tuote> listanTuotteet = kauppalistaRepository.findOne(kauppalistaId).getOstettavatTuotteet();
        List<Tuote> palautettavatTuotteet = new ArrayList<>();
        for (Tuote t : listanTuotteet) {
            if (t.getOstettu() == b) {
                palautettavatTuotteet.add(t);
            }
        }
        return palautettavatTuotteet;
    }

    public void lisaaTuoteKauppalistalle(Tuote tuote, Kauppalista kauppalista) {
        if (tuote == null || kauppalista == null) {
            return;
        }
        tuoteRepository.save(tuote);
        kauppalista.lisaaTuote(tuote);
        this.kauppalistaRepository.save(kauppalista);
    }

    public void lisaaKayttajaKauppalistalle(Kayttaja k, Kauppalista kl) {
        //Ei lisätä listalla jo valmiiksi olevaa käyttäjää uudestaan
        if (!kl.getKayttajat().contains(k)) {
            kl.lisaaKayttaja(k);
        }

        if (!k.getKauppalistat().contains(kl)) {
            k.lisaaKauppalista(kl);
        }

        this.kauppalistaRepository.save(kl);
        this.kayttajaRepository.save(k);
    }

    public List<Kayttaja> haeKauppalistanKayttajat(Long kauppalistaId) {
        if (kauppalistaId == null) {
            return new ArrayList<>();
        }

        Kauppalista kauppalista = kauppalistaRepository.findOne(kauppalistaId);

        if (kauppalista == null) {
            // Jos listaa ei ole, ei sillä ole käyttäjiäkään.
            return new ArrayList<>();
        }

        List<Kayttaja> kauppalistanKayttajat = kauppalista.getKayttajat();

        return kauppalistanKayttajat;
    }

    public boolean kayttajallaOikeudet(Long kauppalistaId) {
        if (kauppalistaId == null) {
            // jos kauppalistaId on null, niin ei ole oikeuksia.
            return false;
        }

        Kayttaja kayttaja = this.kirjautuneetService.getAuthenticatedAccount();
        if (kayttaja == null) {
            // jos käyttäjä ei ole kirjautunut, niin ei ole oikeuksia.
            return false;
        }

        Kauppalista kauppalista = this.kauppalistaRepository.findOne(kauppalistaId);

        if (kauppalista == null) {
            // jos kauppalistaa ei ole, niin ei ole oikeuksia.
            return false;
        }

        return kayttaja.getKauppalistat().contains(kauppalista);
    }

    public String kauppalistaSivu(Model model,
            Long kayttajaId, Long kauppalistaId) {
        if (!this.kayttajallaOikeudet(kauppalistaId)) {
            // TODO: tähän pitää vaihtaa tilalle asianmukainen poikkeus!
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!");
        }
        List<Tuote> tuotteet = this.haeBooleanillaTuotteetKauppalistalta(kauppalistaId, false);
        List<Tuote> ostetutTuotteet = this.haeBooleanillaTuotteetKauppalistalta(kauppalistaId, true);

        model.addAttribute("kauppalista", this.kauppalistaRepository.findOne(kauppalistaId));
        model.addAttribute("tuotteet", tuotteet);
        model.addAttribute("ostetutTuotteet", ostetutTuotteet);
        model.addAttribute("kayttaja", this.kayttajaRepository.findOne(kayttajaId));
        model.addAttribute("kayttajat", this.haeKauppalistanKayttajat(kauppalistaId));

        return "kauppalista";
    }

    public String lisaaTuote(Long kauppalistaId, String tuotenimi) {
        if (!this.kayttajallaOikeudet(kauppalistaId)) {
            // TODO: tähän pitää vaihtaa tilalle asianmukainen poikkeus!
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!");
        }

        Kauppalista kl = this.kauppalistaRepository.findOne(kauppalistaId);

        if (!tuotenimi.trim().isEmpty()) {
            Tuote t = new Tuote(tuotenimi.trim());
            this.tuoteRepository.save(t);

            kl.lisaaTuote(t);

            this.tuoteRepository.save(t);
            this.kauppalistaRepository.save(kl);
        }
        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String merkkaaOstetuksi(Long kayttajaId, Long kauppalistaId, Long tuoteId) {
        if (!kayttajallaOikeudet(kauppalistaId)) {
            // TODO: tähän pitää vaihtaa tilalle asianmukainen poikkeus!
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!");
        }
        Tuote tuote = this.tuoteRepository.findById(tuoteId);
        tuote.setOstettu(true);
        this.tuoteRepository.save(tuote);
        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String merkkaaOstettuTakaisinOstettavaksi(Long kayttajaId, Long kauppalistaId, Long tuoteId) {
        if (!kayttajallaOikeudet(kauppalistaId)) {
            // TODO: tähän pitää vaihtaa tilalle asianmukainen poikkeus!
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!");
        }
        Tuote tuote = this.tuoteRepository.findById(tuoteId);
        tuote.setOstettu(false);
        this.tuoteRepository.save(tuote);
        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String kayttajanKauppalistaSivu(Model model, Long kayttajaId, String salasanatiiviste, String kayttajarooli) {
        // Jos käyttäjä yrittää päästä toisen käyttäjän kauppalistasivulle,
        // esimerkiksi polun kayttajaId:tä vaihtamalla,
        // ohjataan hänet omalle kauppalistasivulleen.
        Kayttaja kirjautunutKayttaja = kirjautuneetService.getAuthenticatedAccount();

        if (kayttajaId == null || kirjautunutKayttaja == null) {
            return "redirect:/etusivu";
        }

        if (!kirjautunutKayttaja.getId().equals(kayttajaId)) {
            return "redirect:/kayttajat/" + kirjautunutKayttaja.getId() + "/kauppalistat";
        }

        Kayttaja kayttaja = this.kayttajaRepository.findOne(kayttajaId);
        List<Kauppalista> kauppalistat = kayttaja.getKauppalistat();
        model.addAttribute("kayttaja", kayttaja);
        model.addAttribute("kauppalistat", kauppalistat);

        // Näytetään testausprofiilissa myös salasanatiiviste (tätä ei ole tuotantoprofiilissa).
        model.addAttribute("salasanatiiviste", salasanatiiviste);

        // Näytetään testausprofiilissa myös käyttäjärooli (tätä ei ole tuotantoprofiilissa).
        model.addAttribute("kayttajarooli", kayttajarooli);
        return "kayttaja";
    }

    public String lisaaKayttajaKauppalistalle(Long kayttajaId, Long kauppalistaId, String kayttajatunnus) {
        if (!this.kayttajallaOikeudet(kauppalistaId)) {
            throw new UsernameNotFoundException("Ei käyttöoikeuksia!"); //Tähän pitää saada parempi exception!
        }
        if (kayttajatunnus.trim().isEmpty() || kayttajaRepository.findByKayttajanimi(kayttajatunnus) == null) {
            return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
        }

        Kayttaja kayttaja = this.kayttajaRepository.findByKayttajanimi(kayttajatunnus);
        this.lisaaKayttajaKauppalistalle(kayttaja, this.kauppalistaRepository.findOne(kauppalistaId));

        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String poistaTuote(Long kayttajaId, Long kauppalistaId, Long tuoteId) {
        if (kayttajaId == null || kauppalistaId == null || tuoteId == null) {
            return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
        }

        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);
        kl.poistaTuote(tuoteId);
        tuoteRepository.delete(tuoteId);
        kauppalistaRepository.save(kl);

        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String poistaKauppalista(Long kayttajaId, Long kauppalistaId) {

        if (kayttajaId == null || kauppalistaId == null) {
            return "redirect:/kayttajat/{kayttajaId}/kauppalistat/";
        }

        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);

        // Kun poistetaan kauppalista, poistetaan ensin kaikki tuotteet listalta.
        while (!kl.getOstettavatTuotteet().isEmpty()) {
            Tuote tuote = kl.getOstettavatTuotteet().get(0);
            kl.poistaTuote(tuote.getId());
            tuoteRepository.delete(tuote.getId());
            kauppalistaRepository.save(kl);
        }

        // Poistetaan listalta kaikki käyttäjät.
        while (!kl.getKayttajat().isEmpty()) {
            Kayttaja kayttaja = kl.getKayttajat().get(0);
            kl.poistaKayttaja(kayttaja.getId());
            kayttaja.poistaKauppalista(kauppalistaId);
            kayttajaRepository.save(kayttaja);
            kauppalistaRepository.save(kl);
        }

        kauppalistaRepository.delete(kauppalistaId);

        return "redirect:/kayttajat/{kayttajaId}/kauppalistat/";
    }
}
