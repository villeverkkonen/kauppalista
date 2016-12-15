package kauppalista.repository;

import kauppalista.domain.Kauppalista;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KauppalistaRepository extends JpaRepository<Kauppalista, Long> {
//    List<Kauppalista> findByKayttajat(String kayttajanimi);
}
