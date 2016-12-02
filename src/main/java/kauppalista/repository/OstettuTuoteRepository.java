
package kauppalista.repository;

import kauppalista.domain.OstettuTuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OstettuTuoteRepository extends JpaRepository<OstettuTuote, Long> {

}
