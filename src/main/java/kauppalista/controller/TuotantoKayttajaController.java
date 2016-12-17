package kauppalista.controller;

import javax.validation.Valid;
import kauppalista.domain.Kayttaja;
import kauppalista.service.KayttajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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

    // Tuotannossa ei listata tunnuksen luoneita käyttäjiä etusivulle.
    // Kayttaja on parametrissa Hibernaten validointia varten.
    //kirjautunut käyttäjä lisätään siksi, että saadaan kayttajaId käyttäjäsivulinkkiä varten.
    @RequestMapping(value = "/etusivu", method = RequestMethod.GET)
    public String etusivu(Model model, @ModelAttribute Kayttaja kayttaja) {
        model.addAttribute("kirjautunutKayttaja", kayttajaService.haeKirjautunutKayttaja());
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
