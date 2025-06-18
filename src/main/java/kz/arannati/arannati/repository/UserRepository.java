package kz.arannati.arannati.repository;

import kz.arannati.arannati.entity.Role;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndActiveIsTrue(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleNameAndActiveIsTrue(String roleName);

    @Query("SELECT u FROM User u WHERE u.role.name = 'COSMETOLOGIST' AND u.isVerified = true AND u.active = true")
    List<User> findVerifiedCosmetologists();

    @Query("SELECT u FROM User u WHERE u.role.name = 'COSMETOLOGIST' AND u.isVerified = false AND u.active = true")
    Page<User> findUnverifiedCosmetologists(Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND u.active = true")
    Page<User> searchActiveUsers(@Param("search") String search, Pageable pageable);

    long countByRoleNameAndActiveIsTrue(String roleName);
}
