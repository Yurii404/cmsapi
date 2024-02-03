package com.sombra.cmsapi.businessservice.repository;

import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByIdAndRole(String id, UserRole role);

  Optional<User> findByEmail(String email);
}
