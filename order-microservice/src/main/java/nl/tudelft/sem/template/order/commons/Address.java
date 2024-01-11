package nl.tudelft.sem.template.order.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import javax.persistence.Embeddable;
import java.util.Objects;
@Embeddable
public class Address {
    private String street;
    private String city;
    private String country;
    private String zip;
//    @Id
//    @GeneratedValue(generator = "uuid-hibernate-generator")
//    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
//    private UUID addressID;

    public Address() {
    }

    public Address street(String street) {
        this.street = street;
        return this;
    }

    @Schema(
            name = "street",
            example = "Mekelweg 5",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("street")
    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Address city(String city) {
        this.city = city;
        return this;
    }

    @Schema(
            name = "city",
            example = "Delft",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("city")
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Address country(String country) {
        this.country = country;
        return this;
    }

    @Schema(
            name = "country",
            example = "Netherlands",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("country")
    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Address zip(String zip) {
        this.zip = zip;
        return this;
    }

    @Schema(
            name = "zip",
            example = "2628CC",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("zip")
    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Address address = (Address)o;
            return Objects.equals(this.street, address.street) && Objects.equals(this.city, address.city) && Objects.equals(this.country, address.country) && Objects.equals(this.zip, address.zip);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.street, this.city, this.country, this.zip});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Address {\n");
        sb.append("    street: ").append(this.toIndentedString(this.street)).append("\n");
        sb.append("    city: ").append(this.toIndentedString(this.city)).append("\n");
        sb.append("    country: ").append(this.toIndentedString(this.country)).append("\n");
        sb.append("    zip: ").append(this.toIndentedString(this.zip)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

//    public void setAddressID(UUID addressID) {
//        this.addressID = addressID;
//    }
//
//    public UUID getAddressID() {
//        return addressID;
//    }
}

