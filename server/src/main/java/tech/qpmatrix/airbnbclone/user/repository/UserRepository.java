package tech.qpmatrix.airbnbclone.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.qpmatrix.airbnbclone.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPublicId(UUID publicId);
}
