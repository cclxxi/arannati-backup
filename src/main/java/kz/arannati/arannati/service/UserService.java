package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<UserDTO> findByEmail(String email);

    Optional<UserDTO> findByEmailAndActiveIsTrue(String email);

    boolean existsByEmail(String email);

    List<UserDTO> findByRoleAndActiveIsTrue(String role);

    List<UserDTO> findVerifiedCosmetologists();

    Page<UserDTO> findUnverifiedCosmetologists(Pageable pageable);

    Page<UserDTO> searchActiveUsers(String search, Pageable pageable);

    long countByRoleAndActiveIsTrue(String role);

    UserDTO save(UserDTO userDTO);

    Optional<UserDTO> findById(Long id);

    void deleteById(Long id);

    List<UserDTO> findAll();

    Page<UserDTO> findAll(Pageable pageable);

    UserDTO convertToDto(User user);

    User convertToEntity(UserDTO userDTO);
}
