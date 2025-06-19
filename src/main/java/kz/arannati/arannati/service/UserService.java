package kz.arannati.arannati.service;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.auth.CosmetologistRegistrationRequest;
import kz.arannati.arannati.dto.auth.UserRegistrationRequest;
import kz.arannati.arannati.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Creates a new user from registration request
     * @param request User registration data
     * @return Created user DTO
     */
    UserDTO createUser(UserRegistrationRequest request);

    /**
     * Creates a new cosmetologist from registration request
     * @param request Cosmetologist registration data
     * @param diplomaFile Diploma or certificate file
     * @return Created user DTO
     */
    UserDTO createCosmetologist(CosmetologistRegistrationRequest request, MultipartFile diplomaFile);

    /**
     * Sends password reset email to user
     * @param email User email
     * @return true if email was sent successfully
     */
    boolean sendPasswordResetEmail(String email);
}
