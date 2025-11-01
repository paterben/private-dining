package paterben.privatedining.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import paterben.privatedining.core.model.Diner;
import paterben.privatedining.core.model.DinerReservations;
import paterben.privatedining.repository.DinerRepository;
import paterben.privatedining.repository.DinerReservationsRepository;

@Service
public class DinerServiceImpl implements DinerService {

    @Autowired
    private DinerRepository dinerRepository;

    @Autowired
    private DinerReservationsRepository dinerReservationsRepository;

    @Override
    @Transactional
    public Diner createDiner(Diner diner) {
        ValidateDinerForCreation(diner);

        Optional<Diner> existingDiner = dinerRepository.findByEmail(diner.getEmail());
        if (existingDiner.isPresent()) {
            throw new ServiceException("Diner with email \"" + diner.getEmail() + "\" already exists",
                    HttpStatus.CONFLICT);
        }

        Diner newDiner = dinerRepository.save(diner);
        // MongoDB silently truncates the created time to milliseconds.
        // See https://github.com/spring-projects/spring-data-mongodb/issues/2883.
        newDiner.setCreatedAt(newDiner.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        // Create an empty document in the dinerReservations repository within the same
        // transaction, so it does not have to be created when adding the first
        // reservation.
        DinerReservations dinerReservations = new DinerReservations();
        dinerReservations.setId(newDiner.getId());
        dinerReservationsRepository.save(dinerReservations);

        return newDiner;
    }

    @Override
    public Optional<Diner> getDinerById(String dinerId) {
        Optional<Diner> diner = dinerRepository.findById(dinerId);
        return diner;
    }

    @Override
    public List<Diner> listDiners() {
        List<Diner> diners = dinerRepository.findAll();
        return diners;
    }

    private void ValidateDinerForCreation(Diner diner) {
        if (StringUtils.hasLength(diner.getId())) {
            throw new ServiceException("`id` must not be set when creating a diner.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(diner.getName())) {
            throw new ServiceException("`name` is required when creating a diner.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!StringUtils.hasLength(diner.getEmail())) {
            throw new ServiceException("`email` is required when creating a diner.",
                    HttpStatus.BAD_REQUEST);
        }
        if (diner.getCreatedAt() != null) {
            throw new ServiceException("`createdAt` must not be set when creating a diner.",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
