package co.duvan.circuit.app.service;

import co.duvan.circuit.app.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(Long id);

}
