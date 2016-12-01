
package wad.tododatabase;

import org.springframework.data.jpa.repository.JpaRepository;


public interface TodoDatabaseRepository extends JpaRepository<Item, Long> {
    
}
