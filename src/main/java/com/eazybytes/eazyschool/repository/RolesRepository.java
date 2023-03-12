package com.eazybytes.eazyschool.repository;

import com.eazybytes.eazyschool.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepository extends JpaRepository<Roles,Integer> {

    Roles getByRoleName(String roleName);
}
