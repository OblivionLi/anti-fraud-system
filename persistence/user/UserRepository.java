package antifraud.persistence.user;

import antifraud.businesslayer.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameIgnoreCase(String username);
}
