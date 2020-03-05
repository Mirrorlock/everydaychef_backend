package everydaychef.api.repository;

import everydaychef.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Boolean existsByName(String name);
    Optional<User> findByName(String username);
}
