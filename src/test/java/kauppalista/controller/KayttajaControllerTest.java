package kauppalista.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kauppalista.domain.Kayttaja;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KayttajaControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void tunnuksenLuontiToimiiOikein() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajat = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkm = kayttajat.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = "salasana" + UUID.randomUUID().toString();

        mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MvcResult uusiRes = this.mockMvc.perform(get("/etusivu"))
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> uudetKayttajat = new ArrayList((Collection<Kayttaja>) uusiRes.getModelAndView().getModel().get("kayttajat"));
        int uusiKayttajienLkm = uudetKayttajat.size();

        assertTrue("Kun on luotu uusi käyttäjä, käyttäjien määrän pitäisi kasvaa yhdellä.", uusiKayttajienLkm == kayttajienLkm + 1);

        List<Kayttaja> luodutKayttajat = uudetKayttajat.stream().filter(kayttaja -> kayttaja.getNimi().equals(kayttajanimi)).collect(Collectors.toList());
        assertTrue("Kun on luotu tietyn niminen käyttäjä, sen nimisiä käyttäjiä pitää löytyä tasan 1.", luodutKayttajat.size() == 1);

        Kayttaja luotuKayttaja = luodutKayttajat.get(0);
        assertTrue("Luodun käyttäjän käyttäjänimen pitää olla annettu käyttäjänimi.", luotuKayttaja.getNimi().equals(kayttajanimi));

        assertTrue("Luodun käyttäjän tallennettu salasanatiiviste ei saa olla null.", luotuKayttaja.getSalasana() != null);
        assertTrue("Luodun käyttäjän tallennetun salasanatiivisteen pitää olla oikea salasanatiiviste.",
                this.passwordEncoder.matches(salasana, luotuKayttaja.getSalasana()));
        assertFalse("Luodun käyttäjän tallennettu salasanatiiviste ei saa olla annettu salasana.", luotuKayttaja.getSalasana().equals(salasana));
    }
}
