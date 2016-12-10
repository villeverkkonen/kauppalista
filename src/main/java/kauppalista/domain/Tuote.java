package kauppalista.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Tuote extends AbstractPersistable<Long> {

    private String tuotenimi;
    private boolean ostettu;

    @ManyToOne
    private Kauppalista kauppalista;

    public Kauppalista getKauppalista() {
        return kauppalista;
    }

    public void setKauppalista(Kauppalista kauppalista) {
        this.kauppalista = kauppalista;
    }

    public Tuote() {
        this.tuotenimi = "";
    }

    public Tuote(String nimi) {
        this.ostettu = false;
        this.tuotenimi = nimi;
    }

    public String getTuotenimi() {
        if (this.tuotenimi == null) {
            this.tuotenimi = "";
        }
        return tuotenimi;
    }

    public void setTuotenimi(String nimi) {
        this.tuotenimi = nimi;
    }

    public boolean getOstettu() {
        return ostettu;
    }

    public void setOstettu(boolean b) {
        this.ostettu = b;
    }
}
