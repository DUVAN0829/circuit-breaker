package co.duvan.circuit.app.controller;

import co.duvan.circuit.app.model.User;
import co.duvan.circuit.app.service.UserService;
import org.apache.coyote.Response;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    //* Vars
    private final UserService service;
    private final CircuitBreakerFactory circuitBreakerFactory;

    //* Constructor
    public UserController(UserService service, CircuitBreakerFactory circuitBreakerFactory) {
        this.service = service;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    //* Handler Methods
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) throws InterruptedException {

        if (id.equals(1L)) {
            throw new IllegalStateException("Producto no encontrado");
        }

        if (id.equals(2L)) {
            TimeUnit.SECONDS.sleep(5L);
        }

        Optional<User> userOp = this.service.findById(id);

        if (userOp.isPresent()) {
            return ResponseEntity.ok(userOp.orElseThrow());
        }

        return ResponseEntity.notFound().build();

    }

    @GetMapping("/username/{id}")
    public ResponseEntity<String> getUserName(@PathVariable Long id) {

        ResponseEntity<User> response = circuitBreakerFactory.create("username").run(() -> {
            try {
                return this.getById(id);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, e -> ResponseEntity.ok(new User("Duglas", 21)))    ;

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).build();
        }

        User user = response.getBody();

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user.getName());

    }

}
