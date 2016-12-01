
package kauppalista.application;

import kauppalista.application.Tuote;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KauppalistaRepository extends JpaRepository<Tuote, Long> {
    
}
