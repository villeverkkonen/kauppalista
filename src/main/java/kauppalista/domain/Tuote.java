
package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Tuote extends AbstractPersistable<Long> {
    
    private String nimi;
    
    public Tuote() {
        
    }
    
    public Tuote(String nimi) {
        this.nimi = nimi;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
}
