package paterben.privatedining.core.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ModelToStringTest {
    @Test
    @DisplayName("Diner.toString() works")
    void testDinerToString() {
        Diner diner = new Diner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));
        assertEquals("Diner[id='1234', name='diner1', email='email1', createdAt='1970-01-01T00:20:34Z']",
                diner.toString());
    }

    @Test
    @DisplayName("DinerReservations.toString() works")
    void testDinerReservationsToString() {

        Reservation reservation = new Reservation("2345", "3456", "4567", "1234", "reservation1",
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        DinerReservations dinerReservations = new DinerReservations("1234", Collections.singletonList(reservation));
        assertEquals(
                "DinerReservations[id='1234', reservations='[Reservation[id='2345', restaurantId='3456', tableId='4567', dinerId='1234', name='reservation1', reservationStart='1970-01-01T03:05:11Z', reservationEnd='1970-01-01T06:10:22Z', isCancelled='false', createdAt='1970-01-01T00:20:34Z', cancelledAt='null']]']",
                dinerReservations.toString());
    }

    @Test
    @DisplayName("Reservation.toString() works")
    void testReservationToString() {
        Reservation reservation = new Reservation("1234", "2345", "3456", "4567", "reservation1",
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        assertEquals(
                "Reservation[id='1234', restaurantId='2345', tableId='3456', dinerId='4567', name='reservation1', reservationStart='1970-01-01T03:05:11Z', reservationEnd='1970-01-01T06:10:22Z', isCancelled='false', createdAt='1970-01-01T00:20:34Z', cancelledAt='null']",
                reservation.toString());
    }

    @Test
    @DisplayName("Restaurant.toString() works")
    void testRestaurantToString() {
        Restaurant restaurant = new Restaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        assertEquals(
                "Restaurant[id='1234', name='restaurant1', address='address1', email='email1', currency='EUR', createdAt='1970-01-01T00:20:34Z']",
                restaurant.toString());
    }

    @Test
    @DisplayName("RestaurantTables.toString() works")
    void testRestaurantTablesToString() {
        Table table = new Table("2345", "table1", 1, 3, RoomType.HALL, 1.5);
        RestaurantTables restaurantTables = new RestaurantTables("1234", Collections.singletonList(table));
        assertEquals(
                "RestaurantTables[id='1234', tables='[Table[id='2345', name='table1', minCapacity='1', maxCapacity='3', roomType='HALL', minSpend='1.5']]']",
                restaurantTables.toString());
    }

    @Test
    @DisplayName("Table.toString() works")
    void testTableToString() {
        Table table = new Table("1234", "table1", 1, 3, RoomType.HALL, 1.5);
        assertEquals(
                "Table[id='1234', name='table1', minCapacity='1', maxCapacity='3', roomType='HALL', minSpend='1.5']",
                table.toString());
    }

    @Test
    @DisplayName("TableReservations.toString() works")
    void testTableReservationsToString() {
        Reservation reservation = new Reservation("4567", "2345", "1234", "3456", "reservation1",
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        TableReservations tableReservations = new TableReservations("1234", "2345",
                Collections.singletonList(reservation));
        assertEquals(
                "TableReservations[id='1234', restaurantId='2345', reservations='[Reservation[id='4567', restaurantId='2345', tableId='1234', dinerId='3456', name='reservation1', reservationStart='1970-01-01T03:05:11Z', reservationEnd='1970-01-01T06:10:22Z', isCancelled='false', createdAt='1970-01-01T00:20:34Z', cancelledAt='null']]']",
                tableReservations.toString());
    }
}
