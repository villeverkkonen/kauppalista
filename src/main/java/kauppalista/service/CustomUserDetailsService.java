package kauppalista.service;

import java.util.Arrays;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Override
    public UserDetails loadUserByUsername(String kayttajanimi) throws UsernameNotFoundException {
        Kayttaja kayttaja = kayttajaRepository.findByKayttajanimi(kayttajanimi);
        String rooli = kayttaja.getRooli();
        String salasana = kayttaja.getSalasana();
        if (kayttaja == null) {
            throw new UsernameNotFoundException("Käyttäjätunnusta "
                    + kayttajanimi + "ei löydy");
        }
        if (kayttajanimi == null || kayttajanimi.equals("")) {
            throw new UsernameNotFoundException("Aseta käyttäjänimi!");
        }
        if (salasana == null || salasana.equals("")) {
            throw new UsernameNotFoundException("Aseta salasana!");
        }
        
        return new org.springframework.security.core.userdetails.User(
                kayttajanimi,
                salasana,
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(rooli)));
    }
}
