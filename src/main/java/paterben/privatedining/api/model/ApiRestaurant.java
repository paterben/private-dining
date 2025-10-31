package paterben.privatedining.api.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Restaurant metadata.")
public class ApiRestaurant {

    @Id
    @Schema(description = "Restaurant ID. Set automatically.")
    private String id;
    @Schema(description = "Restaurant name. Required.")
    private String name;
    @Schema(description = "Restaurant address. Optional.")
    private String address;
    @Schema(description = "Restaurant currency (ISO 4217 currency code). Required.")
    private String currency;
    @CreatedDate
    @Schema(description = "Restaurant creation time. Set automatically.")
    private Instant created;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public ApiRestaurant() {
    }

    public ApiRestaurant(String name, String address, String currency) {
        this.name = name;
        this.address = address;
        this.currency = currency;
    }

    @Override
    public String toString() {
        return String.format(
                "ApiRestaurant[id=%s, name='%s', address='%s', currency='%s',created='%s']",
                id, name, address, currency, created);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
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
        ApiRestaurant other = (ApiRestaurant) obj;
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
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        return true;
    }
}
