
package kauppalista.controller;

import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KauppalistaController {
    
    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    
    @RequestMapping(value = "/", method=RequestMethod.GET)
    public String etusivu(Model model) {
        model.addAttribute("tuotteet", kauppalistaRepository.findAll());
        return "etusivu";
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String tuoteSivu(Model model, @PathVariable Long id) {
        model.addAttribute("tuote", kauppalistaRepository.findOne(id));
        return "tuote";
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public String poistaTuote(@PathVariable Long id) {
        Tuote tuote = kauppalistaRepository.findOne(id);
        kauppalistaRepository.delete(tuote);
        return "redirect:/";
    }
    
    @RequestMapping(value = "/", method=RequestMethod.POST)
    public String lisaaTuote(@RequestParam(required=false) String nimi) {
        kauppalistaRepository.save(new Tuote(nimi));
        return "redirect:/";
    }

}
