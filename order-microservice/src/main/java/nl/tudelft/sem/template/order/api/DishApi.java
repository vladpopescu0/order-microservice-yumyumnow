package nl.tudelft.sem.template.order.api;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import nl.tudelft.sem.template.order.commons.Dish;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

@Validated
@Tag(
        name = "Dish",
        description = "everything about dishes"
)
public interface DishApi {
    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Operation(
            operationId = "addDish",
            summary = "Add a new dish of a restaurant",
            description = "Add a new dish of a restaurant",
            tags = {"Dish"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Dish.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "405",
                    description = "Invalid input"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.POST},
            value = {"/dish"},
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Dish> addDish(@Parameter(name = "Dish",description = "Add a new dish of a restaurant",required = true) @RequestBody @Valid Dish dish) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "deleteDishByID",
            summary = "Deletes a dish",
            description = "delete a dish",
            tags = {"Dish"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid dish id value"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.DELETE},
            value = {"/dish/{dishID}"}
    )
    default ResponseEntity<Void> deleteDishByID(@Parameter(name = "dishID",description = "Dish id to delete",required = true,in = ParameterIn.PATH) @PathVariable("dishID") UUID dishID) {
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getAllergyFilteredDishesFromVendor",
            summary = "Get all dishes offered by a vendor filtered by allergy",
            description = "",
            tags = {"Dish"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Dish.class
                                    )
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid tag value"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/dish/allergy-list/{vendorID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Dish>> getAllergyFilteredDishesFromVendor(@Parameter(name = "vendorID",description = "ID of vendor to retrieve dishes from",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID, @Parameter(name = "allergies",description = "Comma-separated list of allergies to filter by",in = ParameterIn.QUERY) @RequestParam(value = "allergies",required = false) @Valid List<String> allergies) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" }, { \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getDishByID",
            summary = "Find dish by ID",
            description = "Returns a single dish",
            tags = {"Dish"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Dish.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Dish not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/dish/{dishID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<Dish> getDishByID(@Parameter(name = "dishID",description = "ID of dish to return",required = true,in = ParameterIn.PATH) @PathVariable("dishID") UUID dishID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getDishesByVendorID",
            summary = "Find dishes by vendor id",
            description = "Return all dishes from a vendor",
            tags = {"Dish"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Dish.class
                                    )
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Vendor not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/dish/list/{vendorID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Dish>> getDishesByVendorID(@Parameter(name = "vendorID",description = "ID of the vendor from who the dishes will be returned",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" }, { \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "updateDishByID",
            summary = "Update an existing dish",
            description = "Update an existing dish by ID",
            tags = {"Dish"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Dish.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Dish not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.PUT},
            value = {"/dish/{dishID}"},
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Dish> updateDishByID(@Parameter(name = "dishID",description = "ID of dish",required = true,in = ParameterIn.PATH) @PathVariable("dishID") UUID dishID, @Parameter(name = "Dish",description = "Update an existent dish",required = true) @RequestBody @Valid Dish dish) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"image\" : \"image\", \"listOfAllergies\" : [ \"lactose\", \"gluten\" ], \"price\" : 3.25, \"dishID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"listOfIngredients\" : [ \"Fries\", \"Lamb\", \"Cheese\" ], \"name\" : \"Kapsalon\", \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"description\" : \"Lamb kapsalon with cheese\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }
}

