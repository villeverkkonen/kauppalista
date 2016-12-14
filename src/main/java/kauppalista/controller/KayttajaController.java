package kauppalista.controller;

import java.util.List;
import javax.validation.Valid;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.service.KauppalistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KayttajaController {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private KauppalistaService kauppalistaService;

    //Listaa kaikki tunnuksen luoneet käyttäjät etusivulle
    //Kayttaja on parametrissa Hibernaten validointia varten
    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public String etusivu(Model model, @ModelAttribute Kayttaja kayttaja) {
        model.addAttribute("kayttajat", kayttajaRepository.findAll());
        return "etusivu";
    }

    //Lomakkeen kautta luodaan uusi käyttäjätunnus
    //jos lomakkeen kentät ei vastaa sitä, mitä Kayttaja-luokassa
    //oliomuuttujilta vaaditaan, ohjataan erilliselle
    //tunnuksenluontisivulle, missä virheilmoitus virheellisen kentän kohdalla
    @RequestMapping(value = "/etusivu", method = RequestMethod.POST)
    public String lisaaKayttaja(@Valid @ModelAttribute Kayttaja kayttaja, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "tunnuksenluonti";
        }
        if (kayttajaRepository.findByKayttajanimi(kayttaja.getKayttajanimi()) != null) {
            return "tunnuksenluonti";
        }

        // Otetaan tokeniin muistiin uuden käyttäjän tiedot
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(kayttaja.getKayttajanimi(), kayttaja.getSalasana());

        //asetetaan kryptattu salasana
        kayttaja.setSalasana(passwordEncoder.encode(kayttaja.getSalasana()));
        kayttaja.setRooli("ADMIN");

        kayttajaRepository.save(kayttaja);

        // Authentikoidaan uusi luotu käyttäjä
        Authentication auth = authManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/etusivu";
    }

    @RequestMapping(value = "/etusivu/{kayttajaId}/kauppalistat", method = RequestMethod.GET)
    public String kayttajanKauppalistaSivu(Model model, @PathVariable Long kayttajaId) {
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
//        List<Kauppalista> kauppalistat = kauppalistaRepository.findAll();
        List<Kauppalista> kauppalistat = kayttaja.getKauppalista();
        model.addAttribute("kayttaja", kayttaja);
        model.addAttribute("kauppalistat", kauppalistat);
        return "kayttaja";
    }

    @RequestMapping(value = "/etusivu/{kayttajaId}/kauppalistat", method = RequestMethod.POST)
    public String luoKauppalista(@PathVariable Long kayttajaId,
            @RequestParam(required = false) String kauppalistaNimi) {
        Kauppalista kl = new Kauppalista();
        kl.setListanimi(kauppalistaNimi);
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
        this.kauppalistaService.lisaaKayttajaKauppalistalle(kayttaja, kl);
        Long kauppalistaId = kl.getId();

        return "redirect:/{kayttajaId}/kauppalista/" + kauppalistaId;
    }
}
