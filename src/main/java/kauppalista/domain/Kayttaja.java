package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

//Tietokantaan tallennettava käyttäjä,
//joka voi luoda kauppalistoja (Kauppalista)
//rooliksi määritellään ADMIN ja salasana kryptataan AccounControllerissa
@Entity
public class Kayttaja extends AbstractPersistable<Long> {

    private String kayttajanimi;
    private String salasana;
    private String rooli;

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
