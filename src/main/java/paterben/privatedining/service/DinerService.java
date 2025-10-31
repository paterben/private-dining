package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Diner;

public interface DinerService {
    public List<Diner> listDiners();
    
    public Diner createDiner(Diner diner);

    public Optional<Diner> getDinerById(String id);
}
