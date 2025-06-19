package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.CosmetologistVerificationDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.dto.auth.CosmetologistRegistrationRequest;
import kz.arannati.arannati.dto.auth.UserRegistrationRequest;
import kz.arannati.arannati.entity.Role;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.UserRepository;
import kz.arannati.arannati.service.CosmetologistVerificationService;
import kz.arannati.arannati.service.FileStorageService;
import kz.arannati.arannati.service.RoleService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final CosmetologistVerificationService cosmetologistVerificationService;

    @Override
    public UserDTO convertToDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .isVerified(user.isVerified())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public User convertToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());

        // Get or create role based on role name
        if (userDTO.getRole() != null) {
            user.setRole(roleService.getOrCreateRole(userDTO.getRole()));
        }

        user.setVerified(userDTO.isVerified());
        user.setActive(userDTO.isActive());
        user.setCreatedAt(userDTO.getCreatedAt());
        user.setUpdatedAt(userDTO.getUpdatedAt());

        // Note: Password is not set here as it's not part of the DTO
        // Password should be handled separately for security reasons

        return user;
    }

    @Override
    public Optional<UserDTO> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDto);
    }

    @Override
    public Optional<UserDTO> findByEmailAndActiveIsTrue(String email) {
        return userRepository.findByEmailAndActiveIsTrue(email)
                .map(this::convertToDto);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<UserDTO> findByRoleAndActiveIsTrue(String role) {
        return userRepository.findByRoleNameAndActiveIsTrue(role)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findVerifiedCosmetologists() {
        return userRepository.findVerifiedCosmetologists()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> findUnverifiedCosmetologists(Pageable pageable) {
        Page<User> userPage = userRepository.findUnverifiedCosmetologists(pageable);
        List<UserDTO> userDTOs = userPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    @Override
    public Page<UserDTO> searchActiveUsers(String search, Pageable pageable) {
        Page<User> userPage = userRepository.searchActiveUsers(search, pageable);
        List<UserDTO> userDTOs = userPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    @Override
    public long countByRoleAndActiveIsTrue(String role) {
        return userRepository.countByRoleNameAndActiveIsTrue(role);
    }

    @Override
    public UserDTO save(UserDTO userDTO) {
        User user = convertToEntity(userDTO);

        // Note: If this is a new user or password needs to be updated,
        // password handling should be done separately

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserDTO> userDTOs = userPage.getContent()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
    }

    @Override
    public UserDTO createUser(UserRegistrationRequest request) {
        // Check if user already exists
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleService.getOrCreateRole("USER"));
        user.setActive(true);
        user.setVerified(true); // Regular users are automatically verified
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("Created new user: {}", savedUser.getEmail());

        return convertToDto(savedUser);
    }

    @Override
    public UserDTO createCosmetologist(CosmetologistRegistrationRequest request, MultipartFile diplomaFile) {
        // Check if user already exists
        if (existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        // Create new user with cosmetologist role
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(roleService.getOrCreateRole("COSMETOLOGIST"));
        user.setActive(true);
        user.setVerified(false); // Cosmetologists need verification
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("Created new cosmetologist: {}", savedUser.getEmail());

        try {
            // Store diploma file
            String filename = fileStorageService.storeFile(diplomaFile, "cosmetologist-diplomas");

            // Create verification record using DTO
            CosmetologistVerificationDTO verificationDTO = CosmetologistVerificationDTO.builder()
                .userId(savedUser.getId())
                .institutionName(request.getInstitutionName())
                .graduationYear(request.getGraduationYear())
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .diplomaFilePath(filename)
                .diplomaOriginalFilename(diplomaFile.getOriginalFilename())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

            cosmetologistVerificationService.save(verificationDTO);
            log.info("Created verification record for cosmetologist: {}", savedUser.getEmail());

        } catch (IOException e) {
            log.error("Failed to store diploma file for cosmetologist {}: {}", savedUser.getEmail(), e.getMessage());
            // We don't throw exception here to avoid transaction rollback
            // The admin will still be able to review the cosmetologist even without the diploma file
        }

        return convertToDto(savedUser);
    }

    @Override
    public boolean sendPasswordResetEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmailAndActiveIsTrue(email);
        if (userOptional.isEmpty()) {
            log.warn("Password reset requested for non-existent user: {}", email);
            return false;
        }

        // In a real application, we would:
        // 1. Generate a reset token
        // 2. Store it in the database with an expiry time
        // 3. Send an email with a link containing the token

        // For now, we'll just log that we would send an email
        log.info("Would send password reset email to: {}", email);

        // TODO: Implement email sending functionality
        // This would typically use a mail service to send an email with a link like:
        // https://example.com/auth/reset-password?token=someToken

        return true;
    }
}
