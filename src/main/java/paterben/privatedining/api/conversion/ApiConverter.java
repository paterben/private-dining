package paterben.privatedining.api.conversion;

import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;

    public ApiConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Diner toCore(ApiDiner apiDiner) {
        Diner diner = this.modelMapper.map(apiDiner, Diner.class);
        return diner;
    }

    public ApiDiner toApi(Diner diner) {
        ApiDiner apiDiner = this.modelMapper.map(diner, ApiDiner.class);
        return apiDiner;
    }

    public Restaurant toCore(ApiRestaurant apiRestaurant) {
        Restaurant restaurant = this.modelMapper.map(apiRestaurant, Restaurant.class);
        return restaurant;
    }

    public ApiRestaurant toApi(Restaurant restaurant) {
        ApiRestaurant apiRestaurant = this.modelMapper.map(restaurant, ApiRestaurant.class);
        return apiRestaurant;
    }

    public Table toCore(ApiTable apiTable) {
        Table table = this.modelMapper.map(apiTable, Table.class);
        return table;
    }

    public ApiTable toApi(Table table) {
        ApiTable apiTable = this.modelMapper.map(table, ApiTable.class);
        return apiTable;
    }

    public Reservation toCore(ApiReservation apiReservation) {
        Reservation reservation = this.modelMapper.map(apiReservation, Reservation.class);
        return reservation;
    }

    public ApiReservation toApi(Reservation reservation) {
        ApiReservation apiReservation = this.modelMapper.map(reservation, ApiReservation.class);
        return apiReservation;
    }
}
