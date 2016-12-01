package wad.tododatabase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TodoDatabaseController {
    
    @Autowired
    private TodoDatabaseRepository todoDatabaseRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("items", todoDatabaseRepository.findAll());
        return "index";
    }
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String post(@RequestParam (required=false) String name) {
        todoDatabaseRepository.save(new Item(name.trim()));
        return "redirect:/";
    }
    
    @RequestMapping(value = "/{item}", method = RequestMethod.GET)
    public String itemPage(Model model, @PathVariable String item) {
        Long id = Long.parseLong(item.trim());
        
        Item tavara = todoDatabaseRepository.findOne(id);
        
        tavara.setChecked(tavara.getChecked() + 1);
        todoDatabaseRepository.save(tavara);
        model.addAttribute("item", tavara);
        
        return "item";
    }
    
    @RequestMapping(value = "/{item}", method = RequestMethod.DELETE)
    public String delete (@PathVariable String item) {
        Long id = Long.parseLong(item.trim());
        
        todoDatabaseRepository.delete(todoDatabaseRepository.findOne(id));
        
        return "redirect:/";
    }

}
