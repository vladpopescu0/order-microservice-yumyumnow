package nl.tudelft.sem.template.order.controllers;

import nl.tudelft.sem.template.order.api.OrderApi;
import nl.tudelft.sem.template.order.domain.user.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController implements OrderApi {
    private final OrderRepository repository;

    @Autowired
    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    //Methods
}
