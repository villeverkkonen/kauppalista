package kauppalista.application;

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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    
    public String etusivu(Model model) {
        model.addAttribute("tuotteet", kauppalistaRepository.findAll());
        return "index";
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String lisaaTuote(@RequestParam (required=false) String tuoteNimi) {
        kauppalistaRepository.save(new Tuote(tuoteNimi.trim()));
        return "redirect:/";
    }
    
    @RequestMapping(value = "/{tuoteNimi}", method = RequestMethod.GET)
    public String tuoteSivu(Model model, @PathVariable String tuoteNimi) {
        Long id = Long.parseLong(tuoteNimi.trim());
        
        Tuote tuote = kauppalistaRepository.findOne(id);
        
        tuote.setChecked(tuote.getChecked() + 1);
        kauppalistaRepository.save(tuote);
        model.addAttribute("tuote", tuote);
        
        return "tuote";
    }
    
    @RequestMapping(value = "/{tuoteNimi}", method = RequestMethod.DELETE)
    public String poista(@PathVariable String tuoteNimi) {
        Long id = Long.parseLong(tuoteNimi.trim());
        
        kauppalistaRepository.delete(kauppalistaRepository.findOne(id));
        
        return "redirect:/";
    }

}

