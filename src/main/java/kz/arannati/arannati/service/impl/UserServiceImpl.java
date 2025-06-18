package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.entity.Role;
import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.UserRepository;
import kz.arannati.arannati.service.RoleService;
import kz.arannati.arannati.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

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
}
