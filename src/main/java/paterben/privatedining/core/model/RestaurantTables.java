package paterben.privatedining.core.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// List of tables for a restaurant.
@Document(collection = "restaurantTables")
public class RestaurantTables {
    @Id
    // Restaurant ID. Globally unique.
    private String id;
    // The list of tables.
    private List<Table> tables;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public RestaurantTables() {
        this.tables = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((tables == null) ? 0 : tables.hashCode());
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
        RestaurantTables other = (RestaurantTables) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (tables == null) {
            if (other.tables != null)
                return false;
        } else if (!tables.equals(other.tables))
            return false;
        return true;
    }
}
