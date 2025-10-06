package com.example.cleanarch.common.domain.repositories;

import com.example.cleanarch.common.domain.entities.Role;
import com.example.cleanarch.common.domain.entities.User;
import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    // Basic CRUD
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findAllActive();
    void deleteById(String id);
    boolean existsByEmail(String email);

    // Business methods
    User assignRoles(String userId, List<Role> roles);
    User removeRole(String userId, String roleId);
    User activateUser(String userId);
    User deactivateUser(String userId);
    User changePassword(String userId, String newPassword);
    boolean hasRole(String userId, String roleName);
    boolean hasPermission(String userId, String permissionCode);
}
