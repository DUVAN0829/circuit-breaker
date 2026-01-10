package co.duvan.circuit.app.controller;

import co.duvan.circuit.app.model.User;
import co.duvan.circuit.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    //* Vars
    private final UserService service;

    //* Constructor
    public UserController(UserService service) {
        this.service = service;
    }

    //* Handler Methods
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {

        Optional<User> userOp = this.service.findById(id);

        if (userOp.isPresent()) {
            return ResponseEntity.ok(userOp.orElseThrow());
        }

        return ResponseEntity.notFound().build();

    }

}
