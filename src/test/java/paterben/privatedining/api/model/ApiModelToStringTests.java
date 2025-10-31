package paterben.privatedining.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import paterben.privatedining.core.model.RoomType;

public class ApiModelToStringTests {
    @Test
    @DisplayName("ApiDiner.toString() works")
    void dinerToStringTest() {
        ApiDiner diner = new ApiDiner("diner1", "email1");
        diner.setId("1234");
        diner.setCreatedAt(Instant.ofEpochSecond(1234));
        assertEquals("ApiDiner[id='1234', name='diner1', email='email1', createdAt='1970-01-01T00:20:34Z']",
                diner.toString());
    }

    @Test
    @DisplayName("ApiReservation.toString() works")
    void reservationToStringTest() {
        ApiReservation reservation = new ApiReservation("diner1", "reservation1",
                Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        reservation.setId("1234");
                reservation.setRestaurantId("restaurant1");
        reservation.setTableId("table1");
        reservation.setCreatedAt(Instant.ofEpochSecond(1234));
        assertEquals(
                "ApiReservation[id='1234', restaurantId='restaurant1', tableId='table1', dinerId='diner1', name='reservation1', reservationStart='1970-01-01T03:05:11Z', reservationEnd='1970-01-01T06:10:22Z', isCancelled='false', createdAt='1970-01-01T00:20:34Z', cancelledAt='null']",
                reservation.toString());
    }

    @Test
    @DisplayName("ApiRestaurant.toString() works")
    void restaurantToStringTest() {
        ApiRestaurant restaurant = new ApiRestaurant("restaurant1", "address1", "email1", "EUR");
        restaurant.setId("1234");
        restaurant.setCreatedAt(Instant.ofEpochSecond(1234));
        assertEquals(
                "ApiRestaurant[id='1234', name='restaurant1', address='address1', email='email1', currency='EUR', createdAt='1970-01-01T00:20:34Z']",
                restaurant.toString());
    }

    @Test
    @DisplayName("ApiTable.toString() works")
    void tableToStringTest() {
        ApiTable table = new ApiTable("table1", 1, 3, RoomType.HALL, 1.5);
        table.setId("1234");
        assertEquals(
                "ApiTable[id='1234', name='table1', minCapacity='1', maxCapacity='3', roomType='HALL', minSpend='1.5']",
                table.toString());
    }
}
