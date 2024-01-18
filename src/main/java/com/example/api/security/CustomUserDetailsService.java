package com.example.api.security;

import java.util.stream.Collectors;

import com.example.api.repository.UserRepository;
import com.example.api.domain.AppUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	
	 @Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	        final AppUser user = userRepository.findByEmail(email);

	        if (user == null) {
	            throw new UsernameNotFoundException("E-mail '" + email + "' não encontrado.");
	        }
	        
	        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getRoles().stream()
	                .map(role -> new SimpleGrantedAuthority(role.name()))
	                .collect(Collectors.toList()));
	    }
	
	public AppUser getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}