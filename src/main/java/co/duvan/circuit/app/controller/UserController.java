package co.duvan.circuit.app.controller;

import co.duvan.circuit.app.model.User;
import co.duvan.circuit.app.service.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.apache.coyote.Response;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    //* Vars
    private final UserService service;
    //private final CircuitBreakerFactory circuitBreakerFactory;

    //* Constructor
    public UserController(UserService service) {
        this.service = service;
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

//    @GetMapping("/username/{id}")
//    public ResponseEntity<String> getUserName(@PathVariable Long id) {
//
//        ResponseEntity<User> response = circuitBreakerFactory.create("username").run(() -> {
//            try {
//                return this.getById(id);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }, e -> ResponseEntity.ok(new User("Duglas", 21)))    ;
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            return ResponseEntity.status(response.getStatusCode()).build();
//        }
//
//        User user = response.getBody();
//
//        if (user == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(user.getName());
//
//    }

    //todo -> Method Circuit Breaker

    @CircuitBreaker(name = "username", fallbackMethod = "getFallBackMethodMessage")
    @GetMapping("/username/{id}")
    public ResponseEntity<String> getUserName(@PathVariable Long id) throws InterruptedException {

        ResponseEntity<User> response = this.getById(id);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(response.getStatusCode()).build();
        }

        User user = response.getBody();

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user.getName());

    }

    public ResponseEntity<String> getFallBackMethodMessage(Long id, Throwable ex) {
        return ResponseEntity.ok("Estado OPEN, espera 20s");
    }

    //todo -> Method TimeOut

    @TimeLimiter(name = "username", fallbackMethod = "getUserNameFallbackTimeOut")
    @GetMapping("/username/timeout/{id}")
    public CompletableFuture<String> getUserNameTimeOut(@PathVariable Long id) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<User> response = this.getById(id);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new IllegalStateException("Respuesta no exitosa");
                }

                User user = response.getBody();

                if (user == null) {
                    throw new IllegalStateException("Usuario nulo");
                }

                return user.getName();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public CompletableFuture<String> getUserNameFallbackTimeOut(Long id, Throwable ex) {
        return CompletableFuture.completedFuture("Fallback por timeout (3s)");
    }

}
