package paterben.privatedining.core;

import java.time.Instant;

import paterben.privatedining.core.model.Reservation;

public class ReservationConflict {
    /**
     * Returns true if the two reservations overlap in time, false otherwise.
     * 
     * Assumes that both reservations have start < end.
     * 
     * @param r1 The first reservation.
     * @param r2 The second reservation.
     * @return True iff. the two reservations overlap.
     */
    public static boolean reservationsOverlap(Reservation r1, Reservation r2) {
        Instant r1start = r1.getReservationStart();
        Instant r1end = r1.getReservationEnd();
        Instant r2start = r2.getReservationStart();
        Instant r2end = r2.getReservationEnd();
        if (r1end.equals(r2start)) {
            return false;
        }
        if (r1end.isBefore(r2start)) {
            return false;
        }
        if (r2end.equals(r1start)) {
            return false;
        }
        if (r2end.isBefore(r1start)) {
            return false;
        }
        return true;
    }
}
