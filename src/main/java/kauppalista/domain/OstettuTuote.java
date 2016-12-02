
package kauppalista.domain;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class OstettuTuote extends AbstractPersistable<Long> {
    private Tuote tuote;
    private String nimi;
    
    public OstettuTuote() {
        
    }
    
    public OstettuTuote(Tuote tuote) {
        this.tuote = tuote;
        this.nimi = tuote.getNimi();
    }

    public Tuote getTuote() {
        return tuote;
    }

    public void setTuote(Tuote tuote) {
        this.tuote = tuote;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
}
