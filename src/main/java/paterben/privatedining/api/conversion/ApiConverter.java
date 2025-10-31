package paterben.privatedining.api.conversion;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import paterben.privatedining.api.model.ApiDiner;
import paterben.privatedining.api.model.ApiReservation;
import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.api.model.ApiTable;
import paterben.privatedining.core.model.Diner;
import paterben.privatedining.core.model.Reservation;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.Table;

@Component
public class ApiConverter {
    @Autowired
    private ModelMapper modelMapper;

    public Diner ToCore(ApiDiner apiDiner) {
        Diner diner = this.modelMapper.map(apiDiner, Diner.class);
        return diner;
    }

    public ApiDiner ToApi(Diner diner) {
        ApiDiner apiDiner = this.modelMapper.map(diner, ApiDiner.class);
        return apiDiner;
    }

    public Restaurant ToCore(ApiRestaurant apiRestaurant) {
        Restaurant restaurant = this.modelMapper.map(apiRestaurant, Restaurant.class);
        return restaurant;
    }

    public ApiRestaurant ToApi(Restaurant restaurant) {
        ApiRestaurant apiRestaurant = this.modelMapper.map(restaurant, ApiRestaurant.class);
        return apiRestaurant;
    }

    public Table ToCore(ApiTable apiTable) {
        Table table = this.modelMapper.map(apiTable, Table.class);
        return table;
    }

    public ApiTable ToApi(Table table) {
        ApiTable apiTable = this.modelMapper.map(table, ApiTable.class);
        return apiTable;
    }

    public Reservation ToCore(ApiReservation apiReservation) {
        Reservation reservation = this.modelMapper.map(apiReservation, Reservation.class);
        return reservation;
    }

    public ApiReservation ToApi(Reservation reservation) {
        ApiReservation apiReservation = this.modelMapper.map(reservation, ApiReservation.class);
        return apiReservation;
    }
}
