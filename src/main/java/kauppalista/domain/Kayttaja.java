package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.crypto.password.PasswordEncoder;

//Tietokantaan tallennettava käyttäjä,
//joka voi luoda kauppalistoja (Kauppalista) admin-roolissa,
//ja liittyä kutsusta toisen luomaan kauppalistaan user-roolissa
@Entity
public class Kayttaja extends AbstractPersistable<Long> {

    private String kayttajanimi;
    private String salasana;
    private String rooli;

    public Kayttaja() {

    }

    public Kayttaja(String kayttajanimi, String salasana) {
        this.kayttajanimi = kayttajanimi;
        this.salasana = salasana;
        this.rooli = "ADMIN";
    }

    public String getKayttajanimi() {
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
