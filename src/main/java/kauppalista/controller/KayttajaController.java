package kauppalista.controller;

import java.util.List;
import javax.validation.Valid;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class KayttajaController {

    @Autowired
    private KayttajaRepository kayttajaRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        //asetetaan kryptattu salasana
        kayttaja.setSalasana(passwordEncoder.encode(kayttaja.getSalasana()));
        kayttaja.setRooli("ADMIN");

        kayttajaRepository.save(kayttaja);

        return "redirect:/etusivu";
    }

    //Näyttää yhden käyttäjän käyttäjäsivun ja tiedot käyttäjästä
//    @RequestMapping(value = "/etusivu/{kayttajaId}", method = RequestMethod.GET)
//    public String kayttajaSivu(Model model, @PathVariable Long kayttajaId) {
//        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
//        model.addAttribute("kayttaja", kayttaja);
//        return "kayttaja";
//    }
    @RequestMapping(value = "/etusivu/{kayttajaId}/kauppalistat", method = RequestMethod.GET)
    public String kayttajanKauppalistaSivu(Model model, @PathVariable Long kayttajaId) {
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
//        List<Kauppalista> kauppalistat = kauppalistaRepository.findAll();
        List<Kauppalista> kauppalistat = kayttaja.getKauppalista();
        model.addAttribute("kayttaja", kayttaja);
        model.addAttribute("kauppalistat", kauppalistat);
        return "kayttaja";
    }
}
