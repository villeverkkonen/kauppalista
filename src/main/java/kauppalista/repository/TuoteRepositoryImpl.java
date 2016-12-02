package kauppalista.repository;

import java.util.List;
import java.util.stream.Collectors;
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
    public Tuote findByNimi(String nimi) {
        List<Tuote> tuotteet = this.tuoteRepository.findAll();
        return tuotteet.stream().filter(tuote -> tuote.getNimi().equals(nimi)).findAny().orElse(null);
    }

    @Override
    public List<Tuote> findOstettavat() {
        List<Tuote> tuotteet = this.tuoteRepository.findAll();
        return tuotteet.stream().filter(tuote -> tuote.getOstettavienLkm() > 0).collect(Collectors.toList());
    }

    @Override
    public List<Tuote> findOstetut() {
        List<Tuote> tuotteet = this.tuoteRepository.findAll();
        return tuotteet.stream().filter(tuote -> tuote.getOstettujenLkm() > 0).collect(Collectors.toList());
    }
}
