
package kauppalista.controller;

import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {
    
    @Autowired
    private KayttajaRepository kayttajaRepository;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String etusivu(Model model) {
        model.addAttribute("kayttajat", kayttajaRepository.findAll());
        return "etusivu";
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String lisaaKayttaja(@RequestParam(required=false)
            String kayttajanimi, String salasana) {
        kayttajaRepository.save(new Kayttaja(kayttajanimi, salasana));
        return "redirect:/";
    }
    
    @RequestMapping(value = "/{kayttajaId}", method = RequestMethod.GET)
    public String kayttajaSivu(Model model, @PathVariable Long kayttajaId) {
        Kayttaja kayttaja = kayttajaRepository.findOne(kayttajaId);
        model.addAttribute("kayttaja", kayttaja);
        return "kayttaja";
    }
}
