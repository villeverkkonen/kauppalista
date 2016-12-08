package kauppalista.domain;

import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import org.springframework.data.jpa.domain.AbstractPersistable;

//Tietokantaan tallennettava kauppalista, joka on jonkun yhden käyttäjän (Kayttaja) luoma
//ja johon voi kuulua useita muita käyttäjiä.
//Listan luojan rooli on admin, muiden user
//Sisältää tuotteita (Tuote)
@Entity
public class Kauppalista extends AbstractPersistable<Long> {

    @ManyToMany(mappedBy = "kauppalistat", fetch = FetchType.EAGER)
    private List<Kayttaja> kayttajat;

    @OneToMany
    private List<Tuote> ostettavatTuotteet;

    public List<Kayttaja> getKayttajat() {
        return kayttajat;
    }

    public void setKayttajat(List<Kayttaja> kayttajat) {
        this.kayttajat = kayttajat;
    }

//    public void merkkaaOstetuksi() {
//        // ostettiin kaikki.
//        this.ostettujenLkm += this.ostettavienLkm;
//        this.ostettavienLkm = 0;
//    }
//
//    public void merkkaaOstetuksi(double n) {
//        // ostettiin n.
//        this.ostettujenLkm += n;
//        this.ostettavienLkm = Math.max(0, this.ostettavienLkm - n);
//    }
}
