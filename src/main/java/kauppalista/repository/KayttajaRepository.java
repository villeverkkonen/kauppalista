package kauppalista.repository;

import kauppalista.domain.Kayttaja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KayttajaRepository extends JpaRepository<Kayttaja, Long> {

    Kayttaja findByKayttajanimi(String kayttajanimi);
}
