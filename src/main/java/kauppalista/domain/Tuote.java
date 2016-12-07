package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Tuote extends AbstractPersistable<Long> {

    private String tuotenimi;

    private double ostettavienLkm;

    private double ostettujenLkm;

    public Tuote() {
        this.tuotenimi = "";
    }

    public Tuote(String nimi) {
        this.ostettavienLkm = 1;
        this.ostettujenLkm = 0;
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

    public double getOstettavienLkm() {
        return ostettavienLkm;
    }

    public void setOstettavienLkm(double ostettavienLkm) {
        this.ostettavienLkm = ostettavienLkm;
    }

    public double getOstettujenLkm() {
        return ostettujenLkm;
    }

    public void setOstettujenLkm(double ostettujenLkm) {
        this.ostettujenLkm = ostettujenLkm;
    }

    public void merkkaaOstetuksi() {
        // ostettiin kaikki.
        this.ostettujenLkm += this.ostettavienLkm;
        this.ostettavienLkm = 0;
    }

    public void merkkaaOstetuksi(double n) {
        // ostettiin n.
        this.ostettujenLkm += n;
        this.ostettavienLkm = Math.max(0, this.ostettavienLkm - n);
    }
}
