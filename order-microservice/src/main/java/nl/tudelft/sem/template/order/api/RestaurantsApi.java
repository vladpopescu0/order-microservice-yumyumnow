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

import nl.tudelft.sem.template.order.api.ApiUtil;
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
        name = "Restaurants Around",
        description = "filter all restaurants based on some queries"
)
public interface RestaurantsApi {
    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    @Operation(
            operationId = "getAllRestaurants",
            summary = "Find all restaurants around the location of an user",
            description = "Using a constant radius, the endpoint should be able to return all restaurants UUIDs that are within the Euclidean distance",
            tags = {"Restaurants Around"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = UUID.class
                                    )
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided user ID is invalid."
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. No restaurants found around the area of the user."
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/restaurants/{userID}"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<UUID>> getAllRestaurants(@Parameter(name = "userID",description = "ID of user where the rule is applied",required = true,in = ParameterIn.PATH) @PathVariable("userID") UUID userID) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ \"\", \"\" ]";
                    nl.tudelft.sem.template.order.api.ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            operationId = "getAllRestaurantsWithQuery",
            summary = "Find all restaurants around the location of an user and filter the names",
            description = "Using a constant radius, the endpoint should be able to return all restaurants UUIDs that are within the Euclidean distance and the name matches the query string",
            tags = {"Restaurants Around"},
            responses = {@ApiResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = {@Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = UUID.class
                                    )
                            )
                    )}
            ), @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request. The provided user ID or search query is invalid."
            ), @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. No restaurants found within radius or with that name"
            )}
    )
    @RequestMapping(
            method = {RequestMethod.GET},
            value = {"/restaurants/{userID}/{searchQuery}"},
            produces = {"application/json"}
    )
    default ResponseEntity<List<UUID>> getAllRestaurantsWithQuery(@Parameter(name = "userID",description = "ID of user where the rule is applied",required = true,in = ParameterIn.PATH) @PathVariable("userID") UUID userID, @Parameter(name = "searchQuery",description = "string query to search by name, must be URL safe",required = true,in = ParameterIn.PATH) @PathVariable("searchQuery") String searchQuery) {
        this.getRequest().ifPresent((request) -> {
            Iterator var1 = MediaType.parseMediaTypes(request.getHeader("Accept")).iterator();

            while(var1.hasNext()) {
                MediaType mediaType = (MediaType)var1.next();
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ \"\", \"\" ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }

        });
        return new ResponseEntity(HttpStatus.NOT_IMPLEMENTED);
    }
}

