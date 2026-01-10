package co.duvan.circuit.app.repositorie;

import co.duvan.circuit.app.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepositorie extends CrudRepository<User, Long> {
}
