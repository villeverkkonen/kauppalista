package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

//Tietokantaan tallennettava kauppalista, joka on jonkun yhden käyttäjän (Kayttaja) luoma
//ja johon voi kuulua useita muita käyttäjiä.
//Listan luojan rooli on admin, muiden user
//Sisältää tuotteita (Tuote)
@Entity
public class Kauppalista extends AbstractPersistable<Long> {

}
