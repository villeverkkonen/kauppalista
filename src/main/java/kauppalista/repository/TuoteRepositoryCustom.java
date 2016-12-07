package kauppalista.repository;

import java.util.List;
import kauppalista.domain.Tuote;

public interface TuoteRepositoryCustom {

    public Tuote findById(Long id);

    public Tuote findByTuotenimi(String tuotename);

    public List<Tuote> findOstettavat();

    public List<Tuote> findOstetut();
}
