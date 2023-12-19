package nl.tudelft.sem.template.order.commons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.Valid;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID orderID;
    private UUID vendorID;
    private UUID customerID;
    @Embedded
    private Address address;
    private BigDecimal date;
    @ElementCollection
    private @Valid List<UUID> listOfDishes;
    private String specialRequirements;
    private Boolean orderPaid;
    private StatusEnum status;
    private Integer rating;

    public Order() {
    }

    public Order orderID(UUID orderID) {
        this.orderID = orderID;
        return this;
    }

    @Schema(
            name = "orderID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("orderID")
    public @Valid UUID getOrderID() {
        return this.orderID;
    }

    public void setOrderID(UUID orderID) {
        this.orderID = orderID;
    }

    public Order vendorID(UUID vendorID) {
        this.vendorID = vendorID;
        return this;
    }

    @Schema(
            name = "vendorID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("vendorID")
    public @Valid UUID getVendorID() {
        return this.vendorID;
    }

    public void setVendorID(UUID vendorID) {
        this.vendorID = vendorID;
    }

    public Order customerID(UUID customerID) {
        this.customerID = customerID;
        return this;
    }

    @Schema(
            name = "customerID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("customerID")
    public @Valid UUID getCustomerID() {
        return this.customerID;
    }

    public void setCustomerID(UUID customerID) {
        this.customerID = customerID;
    }

    public Order address(Address address) {
        this.address = address;
        return this;
    }

    @Schema(
            name = "address",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("address")
    public @Valid Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Order date(BigDecimal date) {
        this.date = date;
        return this;
    }

    @Schema(
            name = "date",
            example = "1700006405000",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("date")
    public @Valid BigDecimal getDate() {
        return this.date;
    }

    public void setDate(BigDecimal date) {
        this.date = date;
    }

    public Order listOfDishes(List<UUID> listOfDishes) {
        this.listOfDishes = listOfDishes;
        return this;
    }

    public Order addListOfDishesItem(UUID listOfDishesItem) {
        if (this.listOfDishes == null) {
            this.listOfDishes = new ArrayList();
        }

        this.listOfDishes.add(listOfDishesItem);
        return this;
    }

    @Schema(
            name = "listOfDishes",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("listOfDishes")
    public @Valid List<UUID> getListOfDishes() {
        return this.listOfDishes;
    }

    public void setListOfDishes(List<UUID> listOfDishes) {
        this.listOfDishes = listOfDishes;
    }

    public Order specialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
        return this;
    }

    @Schema(
            name = "specialRequirements",
            example = "please knock three times instead of using the bell",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("specialRequirements")
    public String getSpecialRequirements() {
        return this.specialRequirements;
    }

    public void setSpecialRequirements(String specialRequirements) {
        this.specialRequirements = specialRequirements;
    }

    public Order orderPaid(Boolean orderPaid) {
        this.orderPaid = orderPaid;
        return this;
    }

    @Schema(
            name = "orderPaid",
            example = "true",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("orderPaid")
    public Boolean getOrderPaid() {
        return this.orderPaid;
    }

    public void setOrderPaid(Boolean orderPaid) {
        this.orderPaid = orderPaid;
    }

    public Order status(StatusEnum status) {
        this.status = status;
        return this;
    }

    @Schema(
            name = "status",
            example = "delivered",
            description = "Order Status",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("status")
    public StatusEnum getStatus() {
        return this.status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public Order rating(Integer rating) {
        this.rating = rating;
        return this;
    }

    @Schema(
            name = "rating",
            example = "1",
            description = "Rating of the order",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("rating")
    public Integer getRating() {
        return this.rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Order order = (Order)o;
            return Objects.equals(this.orderID, order.orderID) && Objects.equals(this.vendorID, order.vendorID) && Objects.equals(this.customerID, order.customerID) && Objects.equals(this.address, order.address) && Objects.equals(this.date, order.date) && Objects.equals(this.listOfDishes, order.listOfDishes) && Objects.equals(this.specialRequirements, order.specialRequirements) && Objects.equals(this.orderPaid, order.orderPaid) && Objects.equals(this.status, order.status) && Objects.equals(this.rating, order.rating);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.orderID, this.vendorID, this.customerID, this.address, this.date, this.listOfDishes, this.specialRequirements, this.orderPaid, this.status, this.rating});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Order {\n");
        sb.append("    orderID: ").append(this.toIndentedString(this.orderID)).append("\n");
        sb.append("    vendorID: ").append(this.toIndentedString(this.vendorID)).append("\n");
        sb.append("    customerID: ").append(this.toIndentedString(this.customerID)).append("\n");
        sb.append("    address: ").append(this.toIndentedString(this.address)).append("\n");
        sb.append("    date: ").append(this.toIndentedString(this.date)).append("\n");
        sb.append("    listOfDishes: ").append(this.toIndentedString(this.listOfDishes)).append("\n");
        sb.append("    specialRequirements: ").append(this.toIndentedString(this.specialRequirements)).append("\n");
        sb.append("    orderPaid: ").append(this.toIndentedString(this.orderPaid)).append("\n");
        sb.append("    status: ").append(this.toIndentedString(this.status)).append("\n");
        sb.append("    rating: ").append(this.toIndentedString(this.rating)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

    public static enum StatusEnum {
        PENDING("pending"),
        ACCEPTED("accepted"),
        REJECTED("rejected"),
        PREPARING("preparing"),
        GIVEN_TO_COURIER("given to courier"),
        ON_TRANSIT("on-transit"),
        DELIVERED("delivered");

        private String value;

        private StatusEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }

        @JsonCreator
        public static StatusEnum fromValue(String value) {
            StatusEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                StatusEnum b = var1[var3];
                if (b.value.equals(value)) {
                    return b;
                }
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
