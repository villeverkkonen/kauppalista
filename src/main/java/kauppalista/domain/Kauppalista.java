package kauppalista.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

// Tietokantaan tallennettava kauppalista, joka on jonkun yhden
// käyttäjän (Kayttaja) luoma ja johon voi kuulua useita muita käyttäjiä.
// Kaikilla kauppalistan käyttäjillä on listaan user-oikeudet.
// Sisältää tuotteita (Tuote).
@Entity
public class Kauppalista extends AbstractPersistable<Long> {

    @ManyToMany(mappedBy = "kauppalistat", fetch = FetchType.EAGER)
    private List<Kayttaja> kayttajat;

    @OneToMany
    private List<Tuote> ostettavatTuotteet;

    private String listanimi;

    public Kauppalista() {

    }

    public String getListanimi() {
        if (this.listanimi == null) {
            this.listanimi = "";
        }
        return listanimi;
    }

    public void setListanimi(String nimi) {
        this.listanimi = nimi;
    }

    public List<Kayttaja> getKayttajat() {
        if (this.kayttajat == null) {
            this.kayttajat = new ArrayList<>();
        }
        return kayttajat;
    }

    public void setKayttajat(List<Kayttaja> kayttajat) {
        this.kayttajat = kayttajat;
    }

    public void lisaaKayttaja(Kayttaja kayttaja) {
        if (this.kayttajat == null) {
            this.kayttajat = new ArrayList<>();
        }
        this.kayttajat.add(kayttaja);
    }

    public void poistaKayttaja(Long kayttajaId) {
        for (int i = 0; i < this.kayttajat.size(); i++) {
            Kayttaja poistettavaKayttaja = this.kayttajat.get(i);
            if (poistettavaKayttaja.getId().equals(kayttajaId)) {
                this.kayttajat.remove(i);
            }
        }
    }

    public void lisaaTuote(Tuote tuote) {
        if (this.ostettavatTuotteet == null) {
            this.ostettavatTuotteet = new ArrayList<>();
        }

        this.ostettavatTuotteet.add(tuote);
    }

    public void poistaTuote(Long tuoteId) {
        for (int i = 0; i < this.ostettavatTuotteet.size(); i++) {
            Tuote poistettavaTuote = ostettavatTuotteet.get(i);
            if (poistettavaTuote.getId().equals(tuoteId)) {
                this.ostettavatTuotteet.remove(i);
            }
        }
    }

    public List<Tuote> getOstettavatTuotteet() {
        return ostettavatTuotteet;
    }

    public void setOstettavatTuotteet(List<Tuote> ostettavatTuotteet) {
        this.ostettavatTuotteet = ostettavatTuotteet;
    }
}
