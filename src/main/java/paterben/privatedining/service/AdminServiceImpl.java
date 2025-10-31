package paterben.privatedining.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Override
    public void deleteAllData() {
        tableReservationsRepository.deleteAll();
        restaurantTablesRepository.deleteAll();
        restaurantRepository.deleteAll();
    }
}
