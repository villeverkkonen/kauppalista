package kauppalista.repository;

import java.util.List;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KauppalistaRepository extends JpaRepository<Kauppalista, Long> {
    Kauppalista findByKayttajanimi(String kayttajanimi);
}
