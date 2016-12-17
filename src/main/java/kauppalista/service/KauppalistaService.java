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

    public String kauppalistaSivu(Model model,
            Long kauppalistaId, Long kayttajaId) {
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

    public String merkkaaOstetuksi(Long tuoteId) {
        Tuote tuote = tuoteRepository.findById(tuoteId);
        tuote.setOstettu(true);
        tuoteRepository.save(tuote);
        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }

    public String kayttajanKauppalistaSivu(Model model, Long kayttajaId) {
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

    public String lisaaKayttajaKauppalistalle(Long kauppalistaId, Long kayttajaId, String kayttajatunnus) {
        if (kayttajatunnus.trim().isEmpty() || kayttajaRepository.findByKayttajanimi(kayttajatunnus) == null) {
            return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
        }
        Kayttaja kayttaja = this.kayttajaRepository.findByKayttajanimi(kayttajatunnus);

        this.lisaaKayttajaKauppalistalle(kayttaja, this.kauppalistaRepository.findOne(kauppalistaId));
        return "redirect:/kayttajat/{kayttajaId}/kauppalista/{kauppalistaId}";
    }
}
