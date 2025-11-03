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

public class ApiConverterTest {
    private ApiConverter apiConverter = new ApiConverter(new ModelMapper());

    @Test
    @DisplayName("ApiConverter converts ApiDiner to Diner and vice versa")
    void testDinerConversion() {
        ApiDiner apiDiner = new ApiDiner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));
        Diner diner = new Diner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));

        assertEquals(diner, apiConverter.toCore(apiDiner));
        assertEquals(apiDiner, apiConverter.toApi(diner));
    }

    @Test
    @DisplayName("ApiConverter converts ApiReservation to Reservation and vice versa")
    void testReservationConversion() {
        ApiReservation apiReservation = new ApiReservation("1234", "2345", "3456", "4567", "reservation1", 3,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        Reservation reservation = new Reservation("1234", "2345", "3456", "4567", "reservation1", 3,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));

        assertEquals(reservation, apiConverter.toCore(apiReservation));
        assertEquals(apiReservation, apiConverter.toApi(reservation));
    }

    @Test
    @DisplayName("ApiConverter converts ApiRestaurant to Restaurant and vice versa")
    void testRestaurantConversion() {
        ApiRestaurant apiRestaurant = new ApiRestaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        Restaurant restaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));

        assertEquals(restaurant, apiConverter.toCore(apiRestaurant));
        assertEquals(apiRestaurant, apiConverter.toApi(restaurant));
    }

    @Test
    @DisplayName("ApiConverter converts ApiTable to Table and vice versa")
    void testTableConversion() {
        ApiTable apiTable = new ApiTable("1234", "table1", 1, 3, RoomType.HALL, 1.5);
        Table table = new Table("1234", "table1", 1, 3, RoomType.HALL, 1.5);

        assertEquals(table, apiConverter.toCore(apiTable));
        assertEquals(apiTable, apiConverter.toApi(table));
    }
}
