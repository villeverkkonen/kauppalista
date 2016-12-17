package kauppalista.controller;

import javax.validation.Valid;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import kauppalista.service.KayttajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("production")
@Controller
public class TuotantoKayttajaController {

    @Autowired
    private KayttajaService kayttajaService;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    // Tuotannossa ei listata tunnuksen luoneita käyttäjiä etusivulle.
    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public String etusivu(Model model, @ModelAttribute Kayttaja kayttaja) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Kayttaja kirjautunutKayttaja = kayttajaRepository.findByKayttajanimi(auth.getName());
            model.addAttribute("kirjautunutKayttaja", kirjautunutKayttaja);
        }
        model.addAttribute("kayttajat", null);
        return "etusivu";
    }

    // Lomakkeen kautta luodaan uusi käyttäjätunnus.
    // Jos lomakkeen kentät ei vastaa sitä, mitä Kayttaja-luokassa
    // oliomuuttujilta vaaditaan, ohjataan erilliselle
    // tunnuksenluontisivulle, missä virheilmoitus virheellisen kentän kohdalla.
    @RequestMapping(value = "/etusivu", method = RequestMethod.POST)
    public String lisaaKayttaja(Model model, @Valid @ModelAttribute Kayttaja kayttaja, BindingResult bindingResult) {
        return this.kayttajaService.lisaaKayttaja(model, kayttaja, bindingResult);
    }
}
