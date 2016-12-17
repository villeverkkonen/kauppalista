package kauppalista.controller;

import javax.validation.Valid;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import kauppalista.service.KayttajaService;
import kauppalista.service.LoggedInAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Profile("default")
@Controller
public class KayttajaController {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KayttajaService kayttajaService;

    @Autowired
    LoggedInAccountService kirjautuneetService;

    // Listaa kaikki tunnuksen luoneet käyttäjät etusivulle.
    // Kayttaja on parametrissa Hibernaten validointia varten.
    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public String etusivu(Model model, @ModelAttribute Kayttaja kayttaja) {
        model.addAttribute("kayttajat", kayttajaRepository.findAll());
        Kayttaja kirjautunutKayttaja = kirjautuneetService.getAuthenticatedAccount();
        if (kirjautunutKayttaja == null) {
            return "etusivu";
        } else {
            model.addAttribute("kirjautunutKayttaja", kirjautunutKayttaja);
            return "etusivu";
        }

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
