package com.example.cleanarch.common.domain.repositories;

import com.example.cleanarch.common.domain.entities.Permission;
import com.example.cleanarch.common.domain.entities.Role;
import java.util.List;
import java.util.Optional;

public interface IRoleRepository {
    // Basic CRUD
    Role save(Role role);
    Optional<Role> findById(String id);
    Optional<Role> findByName(String name);
    List<Role> findAll();
    List<Role> findAllActive();
    List<Role> findByIds(List<String> ids);
    void deleteById(String id);
    boolean existsByName(String name);

    // Business methods
    Role assignPermissions(String roleId, List<Permission> permissions);
    Role removePermission(String roleId, String permissionCode);
    Role activateRole(String roleId);
    Role deactivateRole(String roleId);
    boolean hasPermission(String roleId, String permissionCode);
}
