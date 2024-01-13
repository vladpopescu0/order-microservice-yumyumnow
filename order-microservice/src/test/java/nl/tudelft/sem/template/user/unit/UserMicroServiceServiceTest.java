package nl.tudelft.sem.template.user.unit;

import nl.tudelft.sem.template.user.services.UserMicroServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class UserMicroServiceServiceTest {

    @Mock
    WebClient webClient;

    @InjectMocks
    UserMicroServiceService userMicroServiceService;
    @BeforeEach
    void setUp() {
    }

    @Test
    void getUserLocation() {
    }

    @Test
    void getAllVendors() {
    }
}