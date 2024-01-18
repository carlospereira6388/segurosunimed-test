package com.example.api.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.example.api.domain.AppUser;
import com.example.api.dto.AuthResponseDTO;
import com.example.api.dto.UserDataDTO;
import com.example.api.dto.UserResponseDTO;
import com.example.api.exception.CustomException;
import com.example.api.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.api.repository.UserRepository;
import com.example.api.security.JwtTokenProvider;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
    private Mapper mapper;

	public AuthResponseDTO login(String email, String password) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
			AppUser user = userRepository.findByEmail(email);
			UserResponseDTO userResponseDTO = mapper.convert(user, UserResponseDTO.class);
			String token = jwtTokenProvider.generateToken(email);
			return new AuthResponseDTO(true, userResponseDTO, token);
		} catch (Exception e) {
			throw new CustomException("E-mail/senha inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	public AuthResponseDTO register(UserDataDTO userDataDTO) {
		if (!isPasswordValid(userDataDTO.getPassword())) {
			throw new CustomException(
					"Senha não corresponde aos requisitos mínimos de segurança.",
					HttpStatus.UNPROCESSABLE_ENTITY);
		}

		if (!userRepository.existsByEmail(userDataDTO.getEmail())) {
			userDataDTO.setPassword(passwordEncoder.encode(userDataDTO.getPassword()));
			
			AppUser user = null;
			
			try {
				user = mapper.convert(userDataDTO, AppUser.class);
				userRepository.save(user);
			} catch (Exception e) {
				throw new CustomException("Erro no cadastro do usuário", HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			UserResponseDTO userResponseDTO = mapper.convert(user, UserResponseDTO.class);
			
			String token = jwtTokenProvider.generateToken(userDataDTO.getEmail());
			
			return new AuthResponseDTO(true, userResponseDTO, token);
		} else {
			throw new CustomException("O e-mail já está sendo usado", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	public List<UserResponseDTO> getAllUsers() {
		try {
			Iterable<AppUser> usersIterable = userRepository.findAll();
			List<AppUser> users = StreamSupport.stream(usersIterable.spliterator(), false)
					.collect(Collectors.toList());
			return users.stream()
					.map(user -> mapper.convert(user, UserResponseDTO.class))
					.collect(Collectors.toList());
		} catch (Exception e) {
			throw new CustomException("Erro ao recuperar os usuários", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private boolean isPasswordValid(String password) {
		return password.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}");
	} 
}