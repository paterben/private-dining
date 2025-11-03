package paterben.privatedining.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import paterben.privatedining.core.model.Reservation;

public class ReservationConflictTest {
    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns false for disjoint intervals - 1")
    void testReservationsDoNotOverlapDisjointOne() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));

        assertFalse(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns false for disjoint intervals - 2")
    void testReservationsDoNotOverlapDisjointTwo() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(33333),
                Instant.ofEpochSecond(44444));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));

        assertFalse(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns false for intervals where one's end is the next's start - 1")
    void testReservationsDoNotOverlapExactOne() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(22222),
                Instant.ofEpochSecond(33333));

        assertFalse(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns false for intervals where one's end is the next's start - 2")
    void testReservationsDoNotOverlapExactTwo() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(22222),
                Instant.ofEpochSecond(33333));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));

        assertFalse(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals where one is included in the other - 1")
    void testReservationsOverlapIncludeOne() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11000),
                Instant.ofEpochSecond(22333));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals where one is included in the other - 2")
    void testReservationsOverlapIncludeTwo() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11000),
                Instant.ofEpochSecond(22333));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals where one is included in the other and one side is equal - 1")
    void testReservationsOverlapIncludeOneSideOne() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(33333));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals where one is included in the other and one side is equal - 2")
    void testReservationsOverlapIncludeOneSideTwo() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(33333));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals which are equal")
    void testReservationsOverlapEqual() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals which overlap partially - 1")
    void testReservationsOverlapPartialOne() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11222),
                Instant.ofEpochSecond(22333));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }

    @Test
    @DisplayName("ReservationOverlap.reservationsOverlap() returns true for intervals which overlap partially - 2")
    void testReservationsOverlapPartialTwo() {
        Reservation r1 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11222),
                Instant.ofEpochSecond(22333));
        Reservation r2 = new Reservation("ignored", "ignored", 0, Instant.ofEpochSecond(11111),
                Instant.ofEpochSecond(22222));

        assertTrue(ReservationConflict.reservationsOverlap(r1, r2));
    }
}
