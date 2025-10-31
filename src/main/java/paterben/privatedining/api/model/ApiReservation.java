package paterben.privatedining.api.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Reservation metadata.")
public class ApiReservation {
    @Id
    @Schema(description = "Reservation ID. Set automatically.")
    private String id;
    @Schema(description = "ID of the restaurant the reservation is for. Set automatically.")
    private String restaurantId;
    @Schema(description = "ID of the table the reservation is for. Set automatically.")
    private String tableId;
    @Schema(description = "ID of the diner the reservation is for. Required.")
    private String dinerId;
    @Schema(description = "Name under which the reservation is made. Required.")
    private String name;
    @Schema(description = "Start time of the reservation. Required.")
    private Instant reservationStart;
    @Schema(description = "End time of the reservation. Required.")
    private Instant reservationEnd;
    @Schema(description = "True if the reservation has been cancelled, either by the restaurant or the diner.")
    private Boolean isCancelled;
    @CreatedDate
    @Schema(description = "Reservation creation time. Set automatically.")
    private Instant createdAt;
    @Schema(description = "Cancellation time of the reservation, or null if not cancelled. Set automatically.")
    private Instant cancelledAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getDinerId() {
        return dinerId;
    }

    public void setDinerId(String dinerId) {
        this.dinerId = dinerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getReservationStart() {
        return reservationStart;
    }

    public void setReservationStart(Instant reservationStart) {
        this.reservationStart = reservationStart;
    }

    public Instant getReservationEnd() {
        return reservationEnd;
    }

    public void setReservationEnd(Instant reservationEnd) {
        this.reservationEnd = reservationEnd;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public ApiReservation() {
    }

    public ApiReservation(String dinerId, String name, Instant reservationStart, Instant reservationEnd) {
        this.dinerId = dinerId;
        this.name = name;
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
    }

    @Override
    public String toString() {
        return String.format(
                "ApiReservation[id='%s', restaurantId='%s', tableId='%s', dinerId='%s', name='%s', reservationStart='%s', reservationEnd='%s', isCancelled='%s', createdAt='%s', cancelledAt='%s']",
                id, restaurantId, tableId, dinerId, name, reservationStart, reservationEnd, isCancelled, createdAt,
                cancelledAt);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((restaurantId == null) ? 0 : restaurantId.hashCode());
        result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
        result = prime * result + ((dinerId == null) ? 0 : dinerId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((reservationStart == null) ? 0 : reservationStart.hashCode());
        result = prime * result + ((reservationEnd == null) ? 0 : reservationEnd.hashCode());
        result = prime * result + ((isCancelled == null) ? 0 : isCancelled.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((cancelledAt == null) ? 0 : cancelledAt.hashCode());
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
        ApiReservation other = (ApiReservation) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (restaurantId == null) {
            if (other.restaurantId != null)
                return false;
        } else if (!restaurantId.equals(other.restaurantId))
            return false;
        if (tableId == null) {
            if (other.tableId != null)
                return false;
        } else if (!tableId.equals(other.tableId))
            return false;
        if (dinerId == null) {
            if (other.dinerId != null)
                return false;
        } else if (!dinerId.equals(other.dinerId))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reservationStart == null) {
            if (other.reservationStart != null)
                return false;
        } else if (!reservationStart.equals(other.reservationStart))
            return false;
        if (reservationEnd == null) {
            if (other.reservationEnd != null)
                return false;
        } else if (!reservationEnd.equals(other.reservationEnd))
            return false;
        if (isCancelled == null) {
            if (other.isCancelled != null)
                return false;
        } else if (!isCancelled.equals(other.isCancelled))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (cancelledAt == null) {
            if (other.cancelledAt != null)
                return false;
        } else if (!cancelledAt.equals(other.cancelledAt))
            return false;
        return true;
    }
}
