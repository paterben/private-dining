package paterben.privatedining.api.model;

import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;
import paterben.privatedining.core.model.RoomType;

@Schema(description = "Table metadata.")
public class ApiTable {
    @Id
    @Schema(description = "Table ID. Set automatically.")
    private String id;
    @Schema(description = "Table name. Required. Must be unique within a restaurant.")
    private String name;
    @Schema(description = "Table min capacity. Optional. 0 means no minimum.")
    private int minCapacity;
    @Schema(description = "Table max capacity. Required.")
    private int maxCapacity;
    @Schema(description = "Room type. Required.")
    private RoomType roomType;
    @Schema(description = "Minimum spend in restaurant local currency. Optional. 0 means no minimum.")
    private double minSpend;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(int minCapacity) {
        this.minCapacity = minCapacity;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public double getMinSpend() {
        return minSpend;
    }

    public void setMinSpend(double minSpend) {
        this.minSpend = minSpend;
    }

    public ApiTable() {
    }

    public ApiTable(String name, int minCapacity, int maxCapacity, RoomType roomType, double minSpend) {
        this.name = name;
        this.minCapacity = minCapacity;
        this.maxCapacity = maxCapacity;
        this.roomType = roomType;
        this.minSpend = minSpend;
    }

    @Override
    public String toString() {
        return String.format(
                "ApiTable[id='%s', name='%s', minCapacity='%s', maxCapacity='%s', roomType='%s', minSpend='%s']",
                id, name, minCapacity, maxCapacity, roomType, minSpend);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + minCapacity;
        result = prime * result + maxCapacity;
        result = prime * result + ((roomType == null) ? 0 : roomType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(minSpend);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        ApiTable other = (ApiTable) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (minCapacity != other.minCapacity)
            return false;
        if (maxCapacity != other.maxCapacity)
            return false;
        if (roomType != other.roomType)
            return false;
        if (Double.doubleToLongBits(minSpend) != Double.doubleToLongBits(other.minSpend))
            return false;
        return true;
    }
}
