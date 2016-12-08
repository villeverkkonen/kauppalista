package kauppalista.repository;

import java.util.List;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KayttajaRepository extends JpaRepository<Kayttaja, Long> {

    Kayttaja findByKayttajanimi(String kayttajanimi);
//    List<Kauppalista> findByKauppalista(Long kauppalista);
}
