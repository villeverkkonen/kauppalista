
package kauppalista.controller;

import java.util.ArrayList;
import java.util.List;
import kauppalista.domain.OstettuTuote;
import kauppalista.domain.Tuote;
import kauppalista.repository.OstettuTuoteRepository;
import kauppalista.repository.TuoteRepository;
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
    private TuoteRepository tuoteRepository;
    
    @Autowired
    private OstettuTuoteRepository ostettuTuoteRepository;
    
    private OstettuTuote ostettuTuote;
    
    private List<OstettuTuote> ostetutTuotteet;
    

    public KauppalistaController() {
        ostetutTuotteet = new ArrayList<>();
    }
    
    @RequestMapping(value = "/", method=RequestMethod.GET)
    public String etusivu(Model model) {
        model.addAttribute("tuotteet", tuoteRepository.findAll());
        model.addAttribute("ostetutTuotteet", ostetutTuotteet);
        return "etusivu";
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.GET)
    public String tuoteSivu(Model model, @PathVariable Long id) {
        model.addAttribute("tuote", tuoteRepository.findOne(id));
        return "tuote";
    }
    
    @RequestMapping(value="/{id}", method=RequestMethod.DELETE)
    public String poistaTuote(@PathVariable Long id) {
        Tuote tuote = tuoteRepository.findOne(id);
        ostetutTuotteet.add(new OstettuTuote(tuote));
        tuoteRepository.delete(tuote);
        return "redirect:/";
    }
    
    @RequestMapping(value = "/", method=RequestMethod.POST)
    public String lisaaTuote(@RequestParam(required=false) String nimi) {
        tuoteRepository.save(new Tuote(nimi.trim()));
        return "redirect:/";
    }

}
