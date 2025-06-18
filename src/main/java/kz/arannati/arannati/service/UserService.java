package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndActiveIsTrue(String email);

    boolean existsByEmail(String email);

    List<User> findByRoleAndActiveIsTrue(String role);

    List<User> findVerifiedCosmetologists();

    Page<User> findUnverifiedCosmetologists(Pageable pageable);

    Page<User> searchActiveUsers(String search, Pageable pageable);

    long countByRoleAndActiveIsTrue(String role);
    
    User save(User user);
    
    Optional<User> findById(Long id);
    
    void deleteById(Long id);
    
    List<User> findAll();
    
    Page<User> findAll(Pageable pageable);
}