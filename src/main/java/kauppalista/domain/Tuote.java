package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Tuote extends AbstractPersistable<Long> {

    private String nimi;

    private double ostettavienLkm;

    private double ostettujenLkm;

    public Tuote() {
        this("");
    }

    public Tuote(String nimi) {
        this.ostettavienLkm = 1;
        this.ostettujenLkm = 0;
        this.nimi = nimi;
    }

    public String getNimi() {
        if (this.nimi == null) {
            this.nimi = "";
        }
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
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
