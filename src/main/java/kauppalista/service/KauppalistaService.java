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
        List<Tuote> listanTuotteet = kauppalistaRepository.findOne(kauppalistaId).getOstettavatTuotteet();
        List<Tuote> palautettavatTuotteet = new ArrayList();
        for (Tuote t : listanTuotteet) {
            if (t.getOstettu() == b) {
                palautettavatTuotteet.add(t);
            }
        }
        return palautettavatTuotteet;
    }

    public void lisaaTuoteKauppalistalle(Tuote tuote, Kauppalista kauppalista) {
        tuoteRepository.save(tuote);
        kauppalista.lisaaTuote(tuote);
        this.kauppalistaRepository.save(kauppalista);
    }

    public void lisaaKayttajaKauppalistalle(Kayttaja k, Kauppalista kl) {

        kl.lisaaKayttaja(k);
        k.lisaaKauppalista(kl);

        this.kauppalistaRepository.save(kl);
        this.kayttajaRepository.save(k);
    }

    public List<Kayttaja> haeKauppalistanKayttajat(Long kauppalistaId) {
        Kauppalista kauppalista = kauppalistaRepository.findOne(kauppalistaId);
        List<Kayttaja> kauppalistanKayttajat = kauppalista.getKayttajat();

        return kauppalistanKayttajat;
    }

    public boolean kayttajallaOikeudet(Long kauppalistaId) {
        Kayttaja kayttaja = this.kirjautuneetService.getAuthenticatedAccount();
        Kauppalista kauppalista = this.kauppalistaRepository.findOne(kauppalistaId);

        return kayttaja.getKauppalista().contains(kauppalista);
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

    public String kayttajanKauppalistaSivu(Model model, Long kayttajaId, String salasanatiiviste, String kayttajarooli) {
        Kayttaja kayttaja = this.kayttajaRepository.findOne(kayttajaId);
        List<Kauppalista> kauppalistat = kayttaja.getKauppalista();
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
        Tuote tuote = tuoteRepository.findOne(tuoteId);
        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);
        kl.poistaTuote(tuote);
        tuoteRepository.delete(tuoteId);
        kauppalistaRepository.save(kl);

        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String poistaKauppalista(Long kayttajaId, Long kauppalistaId) {
        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);
        for (Kayttaja kayttaja : kl.getKayttajat()) {
            kl.poistaKayttaja(kayttaja);
            kayttajaRepository.save(kayttaja);
            kauppalistaRepository.save(kl);
        }
        for (Tuote tuote : kl.getOstettavatTuotteet()) {
            kl.poistaTuote(tuote);
            tuoteRepository.delete(tuote.getId());
            kauppalistaRepository.save(kl);
        }
        kauppalistaRepository.save(kl);

        kauppalistaRepository.delete(kauppalistaId);

        return "redirect:/kayttajat/{kayttajaId}/kauppalistat/";
    }
}
