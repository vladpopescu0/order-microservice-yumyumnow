package nl.tudelft.sem.template.order.commons;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.Valid;

@Entity
public class Dish {
    @Id
    @GeneratedValue(generator = "uuid-hibernate-generator")
    @GenericGenerator(name = "uuid-hibernate-generator", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID dishID;
    private UUID vendorID;
    private String name;
    private Float price;
    private String description;
    @ElementCollection
    private @Valid List<String> listOfIngredients;
    @ElementCollection
    private @Valid List<String> listOfAllergies;
    private String image;

    public Dish() {
    }

    public Dish dishID(UUID dishID) {
        this.dishID = dishID;
        return this;
    }

    @Schema(
            name = "dishID",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("dishID")
    public @Valid UUID getDishID() {
        return this.dishID;
    }

    public void setDishID(UUID dishID) {
        this.dishID = dishID;
    }

    public Dish vendorID(UUID vendorID) {
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

    public Dish name(String name) {
        this.name = name;
        return this;
    }

    @Schema(
            name = "name",
            example = "Kapsalon",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Dish price(Float price) {
        this.price = price;
        return this;
    }

    @Schema(
            name = "price",
            example = "3.25",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("price")
    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Dish description(String description) {
        this.description = description;
        return this;
    }

    @Schema(
            name = "description",
            example = "Lamb kapsalon with cheese",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Dish listOfIngredients(List<String> listOfIngredients) {
        this.listOfIngredients = listOfIngredients;
        return this;
    }

    public Dish addListOfIngredientsItem(String listOfIngredientsItem) {
        if (this.listOfIngredients == null) {
            this.listOfIngredients = new ArrayList();
        }

        this.listOfIngredients.add(listOfIngredientsItem);
        return this;
    }

    @Schema(
            name = "listOfIngredients",
            example = "[\"Fries\",\"Lamb\",\"Cheese\"]",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("listOfIngredients")
    public List<String> getListOfIngredients() {
        return this.listOfIngredients;
    }

    public void setListOfIngredients(List<String> listOfIngredients) {
        this.listOfIngredients = listOfIngredients;
    }

    public Dish listOfAllergies(List<String> listOfAllergies) {
        this.listOfAllergies = listOfAllergies;
        return this;
    }

    public Dish addListOfAllergiesItem(String listOfAllergiesItem) {
        if (this.listOfAllergies == null) {
            this.listOfAllergies = new ArrayList();
        }

        this.listOfAllergies.add(listOfAllergiesItem);
        return this;
    }

    @Schema(
            name = "listOfAllergies",
            example = "[\"lactose\",\"gluten\"]",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("listOfAllergies")
    public List<String> getListOfAllergies() {
        return this.listOfAllergies;
    }

    public void setListOfAllergies(List<String> listOfAllergies) {
        this.listOfAllergies = listOfAllergies;
    }

    public Dish image(String image) {
        this.image = image;
        return this;
    }

    @Schema(
            name = "image",
            description = "Base64 encoded image data",
            requiredMode = RequiredMode.NOT_REQUIRED
    )
    @JsonProperty("image")
    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Dish dish = (Dish)o;
            return Objects.equals(this.dishID, dish.dishID) && Objects.equals(this.vendorID, dish.vendorID) && Objects.equals(this.name, dish.name) && Objects.equals(this.price, dish.price) && Objects.equals(this.description, dish.description) && Objects.equals(this.listOfIngredients, dish.listOfIngredients) && Objects.equals(this.listOfAllergies, dish.listOfAllergies) && Objects.equals(this.image, dish.image);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.dishID, this.vendorID, this.name, this.price, this.description, this.listOfIngredients, this.listOfAllergies, this.image});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Dish {\n");
        sb.append("    dishID: ").append(this.toIndentedString(this.dishID)).append("\n");
        sb.append("    vendorID: ").append(this.toIndentedString(this.vendorID)).append("\n");
        sb.append("    name: ").append(this.toIndentedString(this.name)).append("\n");
        sb.append("    price: ").append(this.toIndentedString(this.price)).append("\n");
        sb.append("    description: ").append(this.toIndentedString(this.description)).append("\n");
        sb.append("    listOfIngredients: ").append(this.toIndentedString(this.listOfIngredients)).append("\n");
        sb.append("    listOfAllergies: ").append(this.toIndentedString(this.listOfAllergies)).append("\n");
        sb.append("    image: ").append(this.toIndentedString(this.image)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }
}

