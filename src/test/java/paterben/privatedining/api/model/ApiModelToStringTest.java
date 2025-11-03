package paterben.privatedining.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import paterben.privatedining.core.model.RoomType;

public class ApiModelToStringTest {
    @Test
    @DisplayName("ApiDiner.toString() works")
    void testApiDinerToString() {
        ApiDiner diner = new ApiDiner("1234", "diner1", "email1", Instant.ofEpochSecond(1234));
        assertEquals("ApiDiner[id='1234', name='diner1', email='email1', createdAt='1970-01-01T00:20:34Z']",
                diner.toString());
    }

    @Test
    @DisplayName("ApiReservation.toString() works")
    void testApiReservationToString() {
        ApiReservation reservation = new ApiReservation("1234", "2345", "3456", "4567", "reservation1", 3,
                Instant.ofEpochSecond(11111), Instant.ofEpochSecond(22222), Instant.ofEpochSecond(1234));
        assertEquals(
                "ApiReservation[id='1234', restaurantId='2345', tableId='3456', dinerId='4567', name='reservation1', numGuests='3', reservationStart='1970-01-01T03:05:11Z', reservationEnd='1970-01-01T06:10:22Z', isCancelled='false', createdAt='1970-01-01T00:20:34Z', cancelledAt='null']",
                reservation.toString());
    }

    @Test
    @DisplayName("ApiRestaurant.toString() works")
    void testApiRestaurantToString() {
        ApiRestaurant restaurant = new ApiRestaurant("1234", "restaurant1", "address1", "email1", "EUR",
                Instant.ofEpochSecond(1234));
        assertEquals(
                "ApiRestaurant[id='1234', name='restaurant1', address='address1', email='email1', currency='EUR', createdAt='1970-01-01T00:20:34Z']",
                restaurant.toString());
    }

    @Test
    @DisplayName("ApiTable.toString() works")
    void testApiTableToString() {
        ApiTable table = new ApiTable("1234", "table1", 1, 3, RoomType.HALL, 1.5);
        assertEquals(
                "ApiTable[id='1234', name='table1', minCapacity='1', maxCapacity='3', roomType='HALL', minSpend='1.5']",
                table.toString());
    }
}
