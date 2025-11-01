package paterben.privatedining.api.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import paterben.privatedining.api.model.ApiDiner;
import paterben.privatedining.api.model.ApiReservation;
import paterben.privatedining.api.model.ApiRestaurant;
import paterben.privatedining.api.model.ApiTable;
import paterben.privatedining.core.model.Diner;
import paterben.privatedining.core.model.Reservation;
import paterben.privatedining.core.model.Restaurant;
import paterben.privatedining.core.model.RoomType;
import paterben.privatedining.core.model.Table;

public class ApiConverterTests {
    private ApiConverter apiConverter = new ApiConverter(new ModelMapper());

    @Test
    @DisplayName("ApiConverter converts ApiDiner to Diner and vice versa")
    void dinerConversionTest() {
        ApiDiner apiDiner = new ApiDiner("diner1", "email1");
        apiDiner.setId("1234");
        apiDiner.setCreatedAt(Instant.ofEpochSecond(1234));

        Diner diner = new Diner("diner1", "email1");
        diner.setId("1234");
        diner.setCreatedAt(Instant.ofEpochSecond(1234));

        assertEquals(diner, apiConverter.toCore(apiDiner));
        assertEquals(apiDiner, apiConverter.toApi(diner));
    }

    @Test
    @DisplayName("ApiConverter converts ApiReservation to Reservation and vice versa")
    void reservationConversionTest() {
        ApiReservation apiReservation = new ApiReservation("diner1", "reservation1",
                Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        apiReservation.setId("1234");
        apiReservation.setRestaurantId("restaurant1");
        apiReservation.setTableId("table1");
        apiReservation.setCreatedAt(Instant.ofEpochSecond(1234));

        Reservation reservation = new Reservation("diner1", "reservation1",
                Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        reservation.setId("1234");
        reservation.setRestaurantId("restaurant1");
        reservation.setTableId("table1");
        reservation.setCreatedAt(Instant.ofEpochSecond(1234));

        assertEquals(reservation, apiConverter.toCore(apiReservation));
        assertEquals(apiReservation, apiConverter.toApi(reservation));
    }

    @Test
    @DisplayName("ApiConverter converts ApiRestaurant to Restaurant and vice versa")
    void restaurantConversionTest() {
        ApiRestaurant apiRestaurant = new ApiRestaurant("restaurant1", "address1", "email1", "EUR");
        apiRestaurant.setId("1234");
        apiRestaurant.setCreatedAt(Instant.ofEpochSecond(1234));

        Restaurant restaurant = new Restaurant("restaurant1", "address1", "email1", "EUR");
        restaurant.setId("1234");
        restaurant.setCreatedAt(Instant.ofEpochSecond(1234));

        assertEquals(restaurant, apiConverter.toCore(apiRestaurant));
        assertEquals(apiRestaurant, apiConverter.toApi(restaurant));
    }

    @Test
    @DisplayName("ApiConverter converts ApiTable to Table and vice versa")
    void tableConversionTest() {
        ApiTable apiTable = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        apiTable.setId("1234");

        Table table = new Table("table1", 1, 3, RoomType.HALL, 1.5);
        table.setId("1234");

        assertEquals(table, apiConverter.toCore(apiTable));
        assertEquals(apiTable, apiConverter.toApi(table));
    }
}
