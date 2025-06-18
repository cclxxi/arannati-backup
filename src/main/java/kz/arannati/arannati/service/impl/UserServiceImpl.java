package kz.arannati.arannati.service.impl;

import kz.arannati.arannati.entity.User;
import kz.arannati.arannati.repository.UserRepository;
import kz.arannati.arannati.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return null;
    }

    @Override
    public Optional<User> findByEmailAndActiveIsTrue(String email) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public List<User> findByRoleAndActiveIsTrue(String role) {
        return null;
    }

    @Override
    public List<User> findVerifiedCosmetologists() {
        return null;
    }

    @Override
    public Page<User> findUnverifiedCosmetologists(Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> searchActiveUsers(String search, Pageable pageable) {
        return null;
    }

    @Override
    public long countByRoleAndActiveIsTrue(String role) {
        return 0;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {
        
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return null;
    }
}