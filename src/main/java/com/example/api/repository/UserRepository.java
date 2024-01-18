package com.example.api.repository;

import com.example.api.domain.AppUser;
import com.example.api.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<AppUser, Integer> {

	boolean existsByEmail(String email);
	AppUser findByEmail(String email);
}