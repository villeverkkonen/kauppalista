package kauppalista.service;

import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoggedInAccountService {

    @Autowired
    KayttajaRepository kayttajaRepository;
    
    public Kayttaja getAuthenticatedAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
 
        return kayttajaRepository.findByKayttajanimi(authentication.getName());
    }
}
