package kauppalista.application;

import javax.persistence.Entity;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Tuote extends AbstractPersistable<Long> {
    private String tuoteNimi;
    private int checked;
    
    public Tuote() {
        
    }
    
    public Tuote(String name) {
        this.tuoteNimi = name;
        this.checked = 0;
    }
    
    public String getTuoteNimi() {
        return tuoteNimi;
    }

    public void setTuoteNimi(String tuoteNimi) {
        this.tuoteNimi = tuoteNimi;
    }

    public int getChecked() {
        return checked;
    }
    
    public void setChecked(int checked) {
        this.checked = checked;
    }

    
}
