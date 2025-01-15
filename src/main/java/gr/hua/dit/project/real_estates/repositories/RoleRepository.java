package gr.hua.dit.project.real_estates.repositories;

import gr.hua.dit.project.real_estates.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Find role by name (ROLE_ADMIN, ROLE_TENANT, etc)
    Optional<Role> findByName(String roleName);

    // Update existing role or insert new one if not exists
    default Role updateOrInsert(Role role) {
        Role existing_role = findByName(role.getName()).orElse(null);
        if (existing_role != null) {
            return existing_role;
        }
        else {
            return save(role);
        }
    }
}