package kz.arannati.arannati.service;

import kz.arannati.arannati.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    
    /**
     * Find a role by its ID
     * @param id the role ID
     * @return an Optional containing the role if found, or empty if not found
     */
    Optional<Role> findById(Long id);
    
    /**
     * Find a role by its name
     * @param name the role name
     * @return an Optional containing the role if found, or empty if not found
     */
    Optional<Role> findByName(String name);
    
    /**
     * Check if a role with the given name exists
     * @param name the role name
     * @return true if the role exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Get all roles
     * @return a list of all roles
     */
    List<Role> findAll();
    
    /**
     * Save a role
     * @param role the role to save
     * @return the saved role
     */
    Role save(Role role);
    
    /**
     * Delete a role by its ID
     * @param id the role ID
     */
    void deleteById(Long id);
    
    /**
     * Get or create a role with the given name
     * @param name the role name
     * @return the existing or newly created role
     */
    Role getOrCreateRole(String name);
}