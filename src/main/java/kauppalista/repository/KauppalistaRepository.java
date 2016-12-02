
package kauppalista.repository;

import kauppalista.domain.Tuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KauppalistaRepository extends JpaRepository<Tuote, Long> {

}
