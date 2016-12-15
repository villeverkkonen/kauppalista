package kauppalista.repository;

import java.util.List;
import kauppalista.domain.Tuote;
import org.springframework.beans.factory.annotation.Autowired;

public class TuoteRepositoryImpl implements TuoteRepositoryCustom {

    @Autowired
    TuoteRepository tuoteRepository;

    @Override
    public Tuote findById(Long id) {
        List<Tuote> tuotteet = this.tuoteRepository.findAll();
        return tuotteet.stream().filter(tuote -> tuote.getId().equals(id)).findAny().orElse(null);
    }

    @Override
    public Tuote findByTuotenimi(String tuotenimi) {
        List<Tuote> tuotteet = this.tuoteRepository.findAll();
        return tuotteet.stream().filter(tuote -> tuote.getTuotenimi().equals(tuotenimi)).findAny().orElse(null);
    }
}
