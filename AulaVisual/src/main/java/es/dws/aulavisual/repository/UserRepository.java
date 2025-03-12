package es.dws.aulavisual.repository;

import es.dws.aulavisual.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface UserRepository extends JpaRepository <User, Long> {

    Optional<User> findByUserName(String userName);
}
