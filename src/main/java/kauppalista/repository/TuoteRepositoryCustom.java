package kauppalista.repository;

import kauppalista.domain.Tuote;

public interface TuoteRepositoryCustom {

    public Tuote findById(Long id);

    public Tuote findByTuotenimi(String tuotename);

}
