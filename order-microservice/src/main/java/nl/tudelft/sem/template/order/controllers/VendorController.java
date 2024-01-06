package nl.tudelft.sem.template.order.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import nl.tudelft.sem.template.order.api.ApiUtil;
import nl.tudelft.sem.template.order.api.VendorApi;
import nl.tudelft.sem.template.order.commons.Dish;
import nl.tudelft.sem.template.order.commons.Order;
import nl.tudelft.sem.template.order.domain.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VendorController implements VendorApi {

    private final transient OrderService orderService;

    @Autowired
    public VendorController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<List<Order>> vendorVendorIDAnalyticsHistoryCustomerIDGet(UUID vendorID, UUID customerID) {
        try {
            List<Order> orders = orderService.getOrdersFromCostumerAtVendor(vendorID,customerID);
            return ResponseEntity.ok(orders);
        } catch (VendorNotFoundException | CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<Integer> vendorVendorIDAnalyticsOrderVolumesGet(UUID vendorID) {
        try {
            Integer volume = orderService.getOrderVolume(vendorID);
            return ResponseEntity.ok(volume);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Integer>> vendorVendorIDAnalyticsPeakTimesGet(UUID vendorID) {
        try {
            List<Integer> volumes = orderService.getOrderVolumeByTime(vendorID);
            return ResponseEntity.ok(volumes);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NoOrdersException e) {
            return ResponseEntity.ok(Arrays.stream(new int[24]).boxed().collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<List<Dish>> vendorVendorIDAnalyticsPopularItemsGet(UUID vendorID) {
        try {
            List<Dish> dishes = orderService.getDishesSortedByVolume(vendorID);
            return ResponseEntity.ok(dishes);
        } catch (VendorNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
