package paterben.privatedining.service;

import java.util.List;
import java.util.Optional;

import paterben.privatedining.core.model.Diner;

/**
 * Service for managing {@link Diner Diners}.
 */
public interface DinerService {
    /**
     * Lists all diners.
     */
    public List<Diner> listDiners();

    /**
     * Creates the given diner.
     * 
     * @param diner the diner to create. Required fields must be set. Fields that
     *              are set automatically must not be set.
     * @throws ServiceException if the {@code diner} is invalid or a diner with the
     *                          same email already exists.
     * @return the created diner.
     */
    public Diner createDiner(Diner diner) throws ServiceException;

    /**
     * Gets the diner with the given ID.
     * 
     * @param dinerId the diner ID.
     * @return the diner metadata, or an empty {@link Optional} if the diner does
     *         not exist.
     */
    public Optional<Diner> getDinerById(String dinerId);
}
