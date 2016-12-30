package kauppalista.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import kauppalista.domain.Kauppalista;
import kauppalista.domain.Kayttaja;
import kauppalista.repository.KayttajaRepository;
import kauppalista.service.KayttajaService;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("default")
@RunWith(SpringRunner.class)
@SpringBootTest
public class KauppalistaTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KayttajaService kayttajaService;

    @Autowired
    KayttajaRepository kayttajaRepository;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void tunnuksenLuontiJaKauppalistanLuontiToimiiOikein() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajat = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkm = kayttajat.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = "salasana" + UUID.randomUUID().toString();

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is3xxRedirection()) // onnistunut redirect
                .andExpect(redirectedUrl("/etusivu"));  // sivulle "/etusivu".

        MvcResult uusiRes = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
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

        Long kayttajaServiceKayttajaId = this.kayttajaService.haeKirjautunutKayttaja().getId();
        assertTrue("Luodun käyttäjän kayttajaServicestä haettu id ei saa olla null.", kayttajaServiceKayttajaId != null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue("Autentikaatio auth ei saa olla null, kun on kirjauduttu sisään autologinilla.", auth != null);

        Long kayttajaRepositoryKayttajaId = kayttajaRepository.findByKayttajanimi(auth.getName()).getId();
        assertTrue("Luodun käyttäjän kayttajaRepositorystä haettu id ei saa olla null.", kayttajaRepositoryKayttajaId != null);

        assertTrue("Luodun käyttäjän kayttajaServicestä ja kayttajaRepositorystä haettujen id-arvojen pitää olla samat.",
                kayttajaServiceKayttajaId.equals(kayttajaRepositoryKayttajaId));

        Long kayttajaId = kayttajaServiceKayttajaId;

        // Tarkistetaan että päästään käyttäjä-sivulle, jolla näkyy
        // käyttäjän kauppalistat.
        MvcResult kauppalistatRes = this.mockMvc.perform(get("/kayttajat/" + kayttajaId + "/kauppalistat"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttaja"))
                .andExpect(model().attributeExists("kauppalistat"))
                .andExpect(view().name("kayttaja"))
                .andReturn();

        List<Kauppalista> kauppalistat = new ArrayList((Collection<Kauppalista>) kauppalistatRes.getModelAndView().getModel().get("kauppalistat"));
        int kauppalistojenLkm = kauppalistat.size();
        assertTrue("Kun on luotu uusi käyttäjä, käyttäjällä ei saa olla valmiina yhtään kauppalistaa.", kauppalistojenLkm == 0);

        // No niin, sitten luodaan uusi kauppalista.
        String kauppalistaNimi = "testilista 1";

        mockMvc.perform(post("/kayttajat/" + kayttajaId + "/kauppalistat").param("kauppalistaNimi", kauppalistaNimi))
                .andExpect(status().is3xxRedirection());

        // Haetaan uusi kauppalistat-sivu.
        MvcResult kauppalistatRes1kpl = this.mockMvc.perform(get("/kayttajat/" + kayttajaId + "/kauppalistat"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttaja"))
                .andExpect(model().attributeExists("kauppalistat"))
                .andExpect(view().name("kayttaja"))
                .andReturn();

        List<Kauppalista> kauppalistatLuonninJalkeen = new ArrayList((Collection<Kauppalista>) kauppalistatRes1kpl.getModelAndView().getModel().get("kauppalistat"));
        int kauppalistojenLkmLuonninJalkeen = kauppalistatLuonninJalkeen.size();
        assertTrue("Kun on luotu käyttäjän ensimmäinen kauppalista, kauppalistojen lukumäärän pitää olla 1.", kauppalistojenLkmLuonninJalkeen == 1);

        // No niin, sitten luodaan uusi kauppalista.
        String kauppalistaNimi2 = "testilista 2";

        mockMvc.perform(post("/kayttajat/" + kayttajaId + "/kauppalistat").param("kauppalistaNimi", kauppalistaNimi2))
                .andExpect(status().is3xxRedirection());

        // Haetaan uusi kauppalistat-sivu.
        MvcResult kauppalistatRes2kpl = this.mockMvc.perform(get("/kayttajat/" + kayttajaId + "/kauppalistat"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttaja"))
                .andExpect(model().attributeExists("kauppalistat"))
                .andExpect(view().name("kayttaja"))
                .andReturn();

        List<Kauppalista> kauppalistatToisenLuonninJalkeen = new ArrayList((Collection<Kauppalista>) kauppalistatRes2kpl.getModelAndView().getModel().get("kauppalistat"));
        int kauppalistojenLkmToisenLuonninJalkeen = kauppalistatToisenLuonninJalkeen.size();
        assertTrue("Kun on luotu käyttäjän toinen kauppalista, kauppalistojen lukumäärän pitää olla 2.", kauppalistojenLkmToisenLuonninJalkeen == 2);
    }

    @Test
    public void eiKahtaSamaaTunnusta() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = "salasana" + UUID.randomUUID().toString();

        mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is3xxRedirection()) // onnistunut redirect
                .andExpect(redirectedUrl("/etusivu"));  // sivulle "/etusivu".

        MvcResult resLisayksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisayksenJalkeen = new ArrayList((Collection<Kayttaja>) resLisayksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisayksenJalkeen = kayttajatLisayksenJalkeen.size();

        assertTrue("Kun on luotu uusi käyttäjä, käyttäjien määrän pitäisi kasvaa yhdellä.", kayttajienLkmLisayksenJalkeen == kayttajienLkmAlussa + 1);

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jolla on varattu käyttäjänimi, käyttäjien määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmLisayksenJalkeen);
    }

    @Test
    public void eiLyhyttaSalasanaa() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = "abcd01!";

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jolla liian lyhyt salasana, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }

    @Test
    public void salasanaEiSaaOllaKayttajatunnus() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = kayttajanimi;

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on sama kuin käyttäjätunnus, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }

    @Test
    public void salasanaEiSaaOllaKayttajatunnusTakaperin() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = new StringBuilder(kayttajanimi).reverse().toString();

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on käyttäjätunnus takaperin, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }

    @Test
    public void salasanaEiSaaOllaKayttajatunnuksenOsa() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        String salasana = kayttajanimi.substring(1);

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on sama kuin käyttäjätunnuksen loppuosa, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);

        String toinenSalasana = kayttajanimi.substring(0, kayttajanimi.length() - 1);

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", toinenSalasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resToisenLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatToisenLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resToisenLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmToisenLisaysyrityksenJalkeen = kayttajatToisenLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on sama kuin käyttäjätunnuksen alkuosa, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmToisenLisaysyrityksenJalkeen == kayttajienLkmAlussa);

        String kolmasSalasana = kayttajanimi.substring(1, kayttajanimi.length() - 1);

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", kolmasSalasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resKolmannenLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatKolmannenLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resKolmannenLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmKolmannenLisaysyrityksenJalkeen = kayttajatKolmannenLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on sama kuin käyttäjätunnuksen keskiosa, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmKolmannenLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }

    @Test
    public void salasananPitaaSisaltaaKirjainValiltaAZaz() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        long luku = UUID.randomUUID().getLeastSignificantBits();
        String salasana = "!0123456789" + luku;

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on ei sisällä kirjaimia a ... z eikä A - Z, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }

    @Test
    public void salasananPitaaSisaltaaNumero() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        long luku = UUID.randomUUID().getLeastSignificantBits();
        StringBuilder salasana = new StringBuilder("!abcdefgh" + luku);

        for (int i = 0; i < salasana.toString().length(); i++) {
            char merkki = salasana.toString().charAt(i);
            if (merkki < '0' && merkki > '9') {
                continue;
            }

            // merkki on numero. muutetaan se kirjaimeksi.
            merkki = (char) (merkki - '0' + 'a');
            salasana.replace(i, i + 1, "" + merkki);
        }

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana.toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on ei sisällä kirjaimia a ... z eikä A - Z, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }

    @Test
    public void salasananPitaaSisaltaaErikoismerkki() throws Exception {
        MvcResult res = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatAlussa = new ArrayList((Collection<Kayttaja>) res.getModelAndView().getModel().get("kayttajat"));

        int kayttajienLkmAlussa = kayttajatAlussa.size();

        String kayttajanimi = "kayttaja" + UUID.randomUUID().toString();
        long luku = UUID.randomUUID().getLeastSignificantBits();
        StringBuilder salasana = new StringBuilder("abcdefgh" + luku);

        for (int i = 0; i < salasana.toString().length(); i++) {
            char merkki = salasana.toString().charAt(i);
            if (merkki >= '0' && merkki <= '9') {
                continue;
            }

            if (merkki >= 'a' && merkki <= 'z') {
                continue;
            }

            if (merkki >= 'A' && merkki <= 'Z') {
                continue;
            }

            // merkki on erikoismerkki. muutetaan se numeroksi.
            merkki = (char) ((merkki % 10) + '0');
            salasana.replace(i, i + 1, "" + merkki);
        }

        this.mockMvc.perform(post("/etusivu").param("kayttajanimi", kayttajanimi).param("salasana", salasana.toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        MvcResult resLisaysyrityksenJalkeen = this.mockMvc.perform(get("/etusivu"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("kayttajat"))
                .andExpect(view().name("etusivu"))
                .andReturn();

        List<Kayttaja> kayttajatLisaysyrityksenJalkeen
                = new ArrayList((Collection<Kayttaja>) resLisaysyrityksenJalkeen.getModelAndView().getModel().get("kayttajat"));
        int kayttajienLkmLisaysyrityksenJalkeen = kayttajatLisaysyrityksenJalkeen.size();

        assertTrue("Kun on yritetty luoda uusi käyttäjä, jonka salasana on ei sisällä yhtään erikoismerkkiä, käyttäjätunnusten määrä ei saa muuttua.",
                kayttajienLkmLisaysyrityksenJalkeen == kayttajienLkmAlussa);
    }
}
