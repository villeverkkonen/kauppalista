package kauppalista.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.jpa.domain.AbstractPersistable;

//Tietokantaan tallennettava käyttäjä,
//joka voi luoda kauppalistoja (Kauppalista)
//rooliksi määritellään ADMIN ja salasana kryptataan KayttajaControllerissa
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
        this.rooli = "ADMIN";
    }

    public Kayttaja(String kayttajanimi, String salasana) {
        this.kayttajanimi = kayttajanimi;
        this.salasana = salasana;
        this.rooli = "ADMIN";
    }

    public String getKayttajanimi() {
        return kayttajanimi;
    }
        

    public List<Kauppalista> getKauppalista() {
        return kauppalistat;
    }

    public void setKauppalistat(List<Kauppalista> kauppalistat) {
        this.kauppalistat = kauppalistat;
    }

    public String getNimi() {
        return kayttajanimi;
    }

    public void setKayttajanimi(String nimi) {
        this.kayttajanimi = nimi;
    }

    public String getSalasana() {
        return salasana;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }

    public String getRooli() {
        return rooli;
    }

    public void setRooli(String rooli) {
        this.rooli = rooli;
    }

}
