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

import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

@Validated
@Tag(
        name = "Analytics Vendor",
        description = "all the necessary analytics for the vendor"
)
public interface VendorApi {
    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Operation(
            operationId = "vendorVendorIDAnalyticsHistoryCustomerIDGet",
            summary = "Get history of orders made by specific customers of a vendor",
            description = "Retrieve the history of orders made by a specific customer of a vendor",
            tags = {"Analytics Vendor"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Order.class
                                    )
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided ID(s) are malformed"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. Vendor/Customer not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/vendor/{vendorID}/analytics/history/{customerID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Order>> vendorVendorIDAnalyticsHistoryCustomerIDGet(@Parameter(name = "vendorID",description = "ID of the vendor",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID, @Parameter(name = "customerID",description = "ID of the customer",required = true,in = ParameterIn.PATH) @PathVariable("customerID") UUID customerID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"price\" : 17.38, \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }, { \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"price\" : 17.38, \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" } ]";
                    nl.tudelft.sem.template.order.api.ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "vendorVendorIDAnalyticsOrderVolumesGet",
            summary = "Get the order volumes of a vendor",
            description = "Get the order volumes analytics of a vendor",
            tags = {"Analytics Vendor"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Integer.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided ID(s) are malformed"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. Vendor not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/vendor/{vendorID}/analytics/orderVolumes"},
            produces = {"application/json"}
    )
    default ResponseEntity<Integer> vendorVendorIDAnalyticsOrderVolumesGet(@Parameter(name = "vendorID",description = "ID of the vendor",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "100";
                    nl.tudelft.sem.template.order.api.ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "vendorVendorIDAnalyticsPeakTimesGet",
            summary = "Get the peak ordering times of a vendor",
            description = "Get the peak ordering times of a vendor",
            tags = {"Analytics Vendor"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = Integer.class
                                    )
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided ID(s) are malformed"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. Vendor not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/vendor/{vendorID}/analytics/peakTimes"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Integer>> vendorVendorIDAnalyticsPeakTimesGet(@Parameter(name = "vendorID",description = "ID of the vendor",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ 0, 0 ]";
                    nl.tudelft.sem.template.order.api.ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "vendorVendorIDAnalyticsPopularItemsGet",
            summary = "Get the popular items of a vendor",
            description = "Get the popular items of a menu of a vendor",
            tags = {"Analytics Vendor"},
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
                    description = "Bad Request. The provided ID(s) are malformed"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. Vendor not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/vendor/{vendorID}/analytics/popularItems"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Dish>> vendorVendorIDAnalyticsPopularItemsGet(@Parameter(name = "vendorID",description = "ID of the vendor",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID) {
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
}

