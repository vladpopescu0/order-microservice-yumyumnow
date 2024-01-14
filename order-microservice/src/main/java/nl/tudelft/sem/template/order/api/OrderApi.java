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
import nl.tudelft.sem.template.order.commons.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

@Validated
@Tag(
        name = "Order",
        description = "everything about orders"
)
public interface OrderApi {
    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Operation(
            operationId = "createOrder",
            summary = "Create new order",
            description = "Creating new order and adding it to the database.",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Order.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Could not make order"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.POST},
            value = {"/order"},
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Order> createOrder(@Parameter(name = "Order",description = "") @RequestBody(required = false) @Valid Order order) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "deleteOrderByID",
            summary = "Delete purchase order by ID",
            description = "Given an orderID, delete it",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Operation succesful"
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.DELETE},
            value = {"/order/{orderID}"}
    )
    default ResponseEntity<Void> deleteOrderByID(@Parameter(name = "orderID",description = "ID of the order that needs to be deleted",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "editOrderByID",
            summary = "Edit an existing order",
            description = "Editing an existing order in the database. Functioning as adding and removing dishes from the order.",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Order.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.PUT},
            value = {"/order/{orderID}"},
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Order> editOrderByID(@Parameter(name = "orderID",description = "ID of vendor to retrieve dishes from",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID, @Parameter(name = "Order",description = "") @RequestBody(required = false) @Valid Order order) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "editOrderRatingByID",
            summary = "Edit the rating of an existing order",
            description = "Editing an existing order rating in the database.",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Order.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "rating exceeded the boundaries of [1-5]"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.PUT},
            value = {"/order/{orderID}/orderRating"},
            produces = {"application/json"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Order> editOrderRatingByID(@Parameter(name = "orderID",description = "ID of order",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID, @Parameter(name = "Order",description = "") @RequestBody(required = false) @Valid Order order) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getAllOrders",
            summary = "Get all orders from the database",
            description = "An admin operation to retrieve all orders",
            tags = {"Order"},
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
                    responseCode = "404",
                    description = "There are no orders in the database"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Order>> getAllOrders() {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }, { \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getCustomerOrderHistory",
            summary = "get all the past orders of a specific customer",
            description = "Returns a list of all past orders of the specified customer",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
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
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Customer not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/{customerID}/history"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<Order>> getCustomerOrderHistory(@Parameter(name = "customerID",description = "ID of customer whose past orders will be returned",required = true,in = ParameterIn.PATH) @PathVariable("customerID") UUID customerID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }, { \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getOrderById",
            summary = "Find order by ID",
            description = "Get order by orderID",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Order.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/{orderID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<Order> getOrderById(@Parameter(name = "orderID",description = "ID of order to return",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getOrderRatingByID",
            summary = "Get the rating of an order",
            description = "Get the rating of an order by orderID",
            tags = {"Order"},
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
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/{orderID}/orderRating"},
            produces = {"application/json"}
    )
    default ResponseEntity<Integer> getOrderRatingByID(@Parameter(name = "orderID",description = "",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "4";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getStatusOfOrderById",
            summary = "Find the status of an order by ID",
            description = "Returns the status of the order an inquiry was made about",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = String.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/{orderID}/status"},
            produces = {"application/json"}
    )
    default ResponseEntity<String> getStatusOfOrderById(@Parameter(name = "orderID",description = "ID of order to return",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "\"accepted\"";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "orderFromVendorIDVendorIDGet",
            summary = "get the number of orders of a vendor",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Integer.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Undefined value for the number of users"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "VendorID does not exist"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/fromVendorID/{vendorID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<Integer> orderFromVendorIDVendorIDGet(@Parameter(name = "vendorID",description = "",required = true,in = ParameterIn.PATH) @PathVariable("vendorID") UUID vendorID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "10";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "orderOrderIDIsPaidGet",
            summary = "Checks if order is paid before being sent to the customer.",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ), @ApiResponse(
                    responseCode = "402",
                    description = "Not paid"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "The order with that id does not exist"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/{orderID}/isPaid"}
    )
    default ResponseEntity<Void> orderOrderIDIsPaidGet(@Parameter(name = "orderID",description = "",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "orderOrderIDVendorGet",
            summary = "Retrieves an order as it should look for the vendor.",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Order.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Something went wrong"
            ), @ApiResponse(
                    responseCode = "403",
                    description = "The Status Is Not Ready"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Id Not Found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/order/{orderID}/vendor"},
            produces = {"application/json"}
    )
    default ResponseEntity<Order> orderOrderIDVendorGet(@Parameter(name = "orderID",description = "",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "updateOrderPaid",
            summary = "Invert the orderPaid field of an order",
            description = "If the orderPaid field is true make it false or true otherwise",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Order.class
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "The orderPaid field is NULL/not set"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.PUT},
            value = {"/order/{orderID}/isPaid"},
            produces = {"application/json"}
    )
    default ResponseEntity<Order> updateOrderPaid(@Parameter(name = "orderID",description = "ID of order",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"date\" : 1700006405000, \"address\" : { \"zip\" : \"2628CC\", \"country\" : \"Netherlands\", \"city\" : \"Delft\", \"street\" : \"Mekelweg 5\" }, \"listOfDishes\" : [ \"listOfDishes\", \"listOfDishes\" ], \"orderID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"customerID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"rating\" : 1, \"vendorID\" : \"550e8400-e29b-41d4-a716-446655440000\", \"orderPaid\" : true, \"specialRequirements\" : \"please knock three times instead of using the bell\", \"status\" : \"delivered\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "updateStatusOfOrderById",
            summary = "Update the status of an order by ID",
            description = "Updates the status of an order by ID",
            tags = {"Order"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation"
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Invalid ID supplied"
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Order not found"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.PUT},
            value = {"/order/{orderID}/status"},
            consumes = {"application/json"}
    )
    default ResponseEntity<Void> updateStatusOfOrderById(@Parameter(name = "orderID",description = "ID of order to update",required = true,in = ParameterIn.PATH) @PathVariable("orderID") UUID orderID, @Parameter(name = "Order",description = "") @RequestBody(required = false) @Valid Order order) {
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }
}

