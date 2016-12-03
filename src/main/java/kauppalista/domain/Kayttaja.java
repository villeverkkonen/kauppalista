
package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Kayttaja extends AbstractPersistable<Long> {
    
    private String nimi;
    private String salasana;
    
    public Kayttaja() {
        
    }
    
    public Kayttaja(String nimi, String salasana) {
        this.nimi = nimi;
        this.salasana = salasana;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getSalasana() {
        return salasana;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }
    
    
}
