package co.duvan.circuit.app.service;

import co.duvan.circuit.app.model.User;
import co.duvan.circuit.app.repositorie.UserRepositorie;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    //* Vars
    private final UserRepositorie repositorie;

    //* Constructor
    public UserServiceImpl(UserRepositorie repositorie) {
        this.repositorie = repositorie;
    }

    //* Methods
    @Override
    public Optional<User> findById(Long id) {
        return this.repositorie.findById(id);
    }

}
