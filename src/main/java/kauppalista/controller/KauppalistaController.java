package kauppalista.controller;

import kauppalista.domain.Tuote;
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String etusivu(Model model) {
        model.addAttribute("tuotteet", tuoteRepository.findOstettavat());
        model.addAttribute("ostetutTuotteet", tuoteRepository.findOstetut());
        return "etusivu";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String tuoteSivu(Model model, @PathVariable Long id) {
        model.addAttribute("tuote", tuoteRepository.findOne(id));
        return "tuote";
    }

    @RequestMapping(value = "/ostettu/{id}", method = RequestMethod.POST)
    public String merkkaaOstetuksi(@PathVariable Long id) {
        Tuote tuote = tuoteRepository.findById(id);
        tuote.merkkaaOstetuksi(); // ostettiin kaikki (ostettiin n ei vielä toteutettu).
        tuoteRepository.save(tuote);
        return "redirect:/";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String lisaaTuote(@RequestParam(required = false) String nimi) {
        tuoteRepository.save(new Tuote(nimi.trim()));
        return "redirect:/";
    }

}
