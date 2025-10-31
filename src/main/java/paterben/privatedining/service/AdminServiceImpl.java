package paterben.privatedining.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import paterben.privatedining.repository.DinerRepository;
import paterben.privatedining.repository.DinerReservationsRepository;
import paterben.privatedining.repository.RestaurantRepository;
import paterben.privatedining.repository.RestaurantTablesRepository;
import paterben.privatedining.repository.TableReservationsRepository;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantTablesRepository restaurantTablesRepository;

    @Autowired
    private TableReservationsRepository tableReservationsRepository;

    @Autowired
    private DinerRepository dinerRepository;

    @Autowired
    private DinerReservationsRepository dinerReservationsRepository;

    @Override
    public void deleteAllData() {
        tableReservationsRepository.deleteAll();
        dinerReservationsRepository.deleteAll();
        restaurantTablesRepository.deleteAll();
        restaurantRepository.deleteAll();
        dinerRepository.deleteAll();
    }
}
