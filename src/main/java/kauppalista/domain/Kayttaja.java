package kauppalista.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

// Tietokantaan tallennettava käyttäjä,
// joka voi luoda kauppalistoja (Kauppalista).
// Rooliksi määritellään "USER" ja salasana kryptataan KayttajaControllerissa.
@Entity
public class Kayttaja extends AbstractPersistable<Long> {

    @NotBlank(message = "Anna käyttäjänimi")
    private String kayttajanimi;

    @NotBlank(message = "Anna salasana")
    private String salasana;

    private String rooli;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Kauppalista> kauppalistat;

    public Kayttaja() {
    }

    public Kayttaja(String kayttajanimi) {
        this.kayttajanimi = kayttajanimi;
        this.rooli = "USER";
    }

    public Kayttaja(String kayttajanimi, String salasana) {
        this.kayttajanimi = kayttajanimi;
        this.salasana = salasana;
        this.rooli = "USER";
    }

    public String getKayttajanimi() {
        if (this.kayttajanimi == null) {
            this.kayttajanimi = "";
        }
        return this.kayttajanimi;
    }

    public List<Kauppalista> getKauppalista() {
        if (this.kauppalistat == null) {
            this.kauppalistat = new ArrayList<>();
        }
        return this.kauppalistat;
    }

    public void setKauppalistat(List<Kauppalista> kauppalistat) {
        this.kauppalistat = kauppalistat;
    }

    public void lisaaKauppalista(Kauppalista kauppalista) {
        if (this.kauppalistat == null) {
            this.kauppalistat = new ArrayList<>();
        }
        this.kauppalistat.add(kauppalista);
    }

    public String getNimi() {
        if (this.kayttajanimi == null) {
            this.kayttajanimi = "";
        }
        return this.kayttajanimi;
    }

    public void setKayttajanimi(String nimi) {
        this.kayttajanimi = nimi;
    }

    public String getSalasana() {
        if (this.salasana == null) {
            this.salasana = "";
        }
        return salasana;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }

    public String getRooli() {
        if (this.rooli == null) {
            this.rooli = "";
        }
        return this.rooli;
    }

    public void setRooli(String rooli) {
        this.rooli = rooli;
    }
}
