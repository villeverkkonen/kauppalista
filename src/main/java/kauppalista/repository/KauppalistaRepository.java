package kauppalista.repository;

import java.util.List;
import kauppalista.domain.Kauppalista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KauppalistaRepository extends JpaRepository<Kauppalista, Long> {
//    List<Kauppalista> findByKayttajat(String kayttajanimi);
}
