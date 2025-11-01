package paterben.privatedining.core.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * List of reservations for a diner.
 */
@Document(collection = "dinerReservations")
public class DinerReservations {
    /**
     * Diner ID. Globally unique. Set automatically on creation.
     */
    @Id
    private String id;
    /**
     * The list of reservations. Set to empty on creation.
     */
    private List<Reservation> reservations;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public DinerReservations() {
        this.reservations = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((reservations == null) ? 0 : reservations.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DinerReservations other = (DinerReservations) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (reservations == null) {
            if (other.reservations != null)
                return false;
        } else if (!reservations.equals(other.reservations))
            return false;
        return true;
    }
}
