/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kauppalista.service;

import java.util.ArrayList;
import java.util.List;
import kauppalista.domain.Tuote;
import kauppalista.repository.KauppalistaRepository;
import kauppalista.repository.KayttajaRepository;
import kauppalista.repository.TuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KauppalistaService {

    @Autowired
    private TuoteRepository tuoteRepository;

    @Autowired
    private KauppalistaRepository kauppalistaRepository;

    @Autowired
    private KayttajaRepository kayttajaRepository;

    public KauppalistaService() {
    }

    public List<Tuote> haeBooleanillaTuotteetKauppalistalta(Long kauppalistaId, boolean b) {
        List<Tuote> listanTuotteet = kauppalistaRepository.findOne(kauppalistaId).getOstettavatTuotteet();
        List<Tuote> palautettavatTuotteet = new ArrayList();
        for(Tuote t : listanTuotteet){
            if(t.getOstettu() == b) {
                palautettavatTuotteet.add(t);
            }
        }
        return palautettavatTuotteet;
    }    
}
