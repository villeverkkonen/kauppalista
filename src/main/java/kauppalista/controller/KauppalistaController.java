package kauppalista.controller;

import kauppalista.domain.Kauppalista;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
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
    private KauppalistaRepository kauppalistaRepository;
    
    //Listaa kaikki kauppalistan tuotteet ja ostetuiksi merkatutu tuotteet
    @RequestMapping(value = "/kauppalista", method = RequestMethod.GET)
    public String etusivu(Model model) {
        model.addAttribute("tuotteet", tuoteRepository.findOstettavat());
        model.addAttribute("ostetutTuotteet", tuoteRepository.findOstetut());
        return "kauppalista";
    }

    @RequestMapping(value = "/{kayttajaId}/kauppalista/{kauppalistaId}", method = RequestMethod.GET)
    public String kauppalistaSivu(Model model, @PathVariable Long kauppalistaId) {
        Kauppalista kl = kauppalistaRepository.findOne(kauppalistaId);
        model.addAttribute("kauppalista", kl);
//        model.addAttribute("ostetutTuotteet", kl.);
        return "kauppalista";
    }
    
    //Näyttää tuotteen sivun, jossa voi olla tarkempia tuotekuvauksia
    //tai jopa kuva tuotteesta, helpottaakseen löytämään juuri oikean tuotteen kaupasta
    @RequestMapping(value = "/kauppalista/{tuoteId}", method = RequestMethod.GET)
    public String tuoteSivu(Model model, @PathVariable Long tuoteId) {
        model.addAttribute("tuote", tuoteRepository.findOne(tuoteId));
        return "tuote";
    }

    //Merkataan kauppalistalla oleva tuote ostetuksi
    @RequestMapping(value = "/kauppalista/ostettu/{tuoteId}", method = RequestMethod.POST)
    public String merkkaaOstetuksi(@PathVariable Long tuoteId) {
        Tuote tuote = tuoteRepository.findById(tuoteId);
        tuote.merkkaaOstetuksi(); // ostettiin kaikki (ostettiin n ei vielä toteutettu).
        tuoteRepository.save(tuote);
        return "redirect:/kauppalista";
    }

    //Lisää tuotteen kauppalistalle
    @RequestMapping(method = RequestMethod.POST)
    public String lisaaTuote(@RequestParam(required = false) String tuotenimi) {
        tuoteRepository.save(new Tuote(tuotenimi.trim()));
        return "redirect:/kauppalista";
    }
}
