package paterben.privatedining.api.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Diner metadata.")
public class ApiDiner {
    @Id
    @Schema(description = "Diner ID. Set automatically on creation.")
    private String id;
    @Schema(description = "Diner preferred name. Required.")
    private String name;
    @Schema(description = "Diner email. Required. Must be globally unique.")
    private String email;
    @CreatedDate
    @Schema(description = "Diner creation time. Set automatically on creation.")
    private Instant createdAt;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ApiDiner() {
    }

    public ApiDiner(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public ApiDiner(String id, String name, String email, Instant createdAt) {
        this(name, email);
        this.id = id;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format(
                "ApiDiner[id='%s', name='%s', email='%s', createdAt='%s']",
                id, name, email, createdAt);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
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
        ApiDiner other = (ApiDiner) obj;
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
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        return true;
    }
}
