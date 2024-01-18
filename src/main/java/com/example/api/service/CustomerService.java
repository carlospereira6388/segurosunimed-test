package com.example.api.service;

import com.example.api.domain.Address;
import com.example.api.domain.Customer;
import com.example.api.dto.*;
import com.example.api.repository.CustomerRepository;
import com.example.api.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.api.exception.CustomException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private Mapper mapper;

	private final RestTemplate restTemplate = new RestTemplate();

	public CustomerResponseDTO addCustomer(CustomerDataDTO customerDTO) {
		try {
			Customer customer = mapper.convert(customerDTO, Customer.class);
			customer = customerRepository.save(customer);
			return mapper.convert(customer, CustomerResponseDTO.class);
		} catch (Exception e) {
			throw new CustomException("Erro ao adicionar cliente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Page<CustomerResponseDTO> getAllCustomers(String name, String email, String gender, Pageable pageable) {
		try {
			Page<Customer> customerPage;
			if (name == null && email == null && gender == null) {
				customerPage = customerRepository.findAllByOrderByNameAsc(pageable);
			} else {
				customerPage = customerRepository.findByNameContainingAndEmailContainingAndGenderContaining(
						name == null ? "" : name,
						email == null ? "" : email,
						gender == null ? "" : gender,
						pageable);
			}

			return customerPage.map(customer -> mapper.convert(customer, CustomerResponseDTO.class));
		} catch (Exception e) {
			throw new CustomException("Erro ao recuperar clientes", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public CustomerResponseDTO getCustomer(Long id) {
		try {
			Customer customer = customerRepository.findById(id).orElseThrow(() ->
					new CustomException("Cliente não encontrado", HttpStatus.NOT_FOUND));
			return mapper.convert(customer, CustomerResponseDTO.class);
		} catch (CustomException ce) {
			throw ce;
		} catch (Exception e) {
			throw new CustomException("Erro ao recuperar cliente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public CustomerResponseDTO updateCustomer(Long id, CustomerDataDTO customerDTO) {
		try {
			Customer customer = customerRepository.findById(id).orElseThrow(() ->
					new CustomException("Cliente não encontrado", HttpStatus.NOT_FOUND));
			customer = mapper.convert(customerDTO, Customer.class);
			customer.setId(id);
			customer = customerRepository.save(customer);
			return mapper.convert(customer, CustomerResponseDTO.class);
		} catch (CustomException ce) {
			throw ce;
		} catch (Exception e) {
			throw new CustomException("Erro ao atualizar cliente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public void deleteCustomer(Long id) {
		try {
			if (!customerRepository.existsById(id)) {
				throw new CustomException("Cliente não encontrado", HttpStatus.NOT_FOUND);
			}
			customerRepository.deleteById(id);
		} catch (CustomException ce) {
			throw ce;
		} catch (Exception e) {
			throw new CustomException("Erro ao excluir cliente", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public AddressResponseDTO getAddressFromCep(String cep) {
		String url = "https://viacep.com.br/ws/" + cep + "/json/";

		try {
			ResponseEntity<AddressViaCepDTO> response = restTemplate.getForEntity(url, AddressViaCepDTO.class);
			if (response.getBody() != null && !response.getBody().isErro()) {
				return convertToAddressResponseDTO(response.getBody());
			} else {
				throw new CustomException("CEP não encontrado", HttpStatus.NOT_FOUND);
			}
		} catch (HttpClientErrorException e) {
			throw new CustomException("Erro ao consultar CEP", e.getStatusCode());
		}
	}

	private AddressResponseDTO convertToAddressResponseDTO(AddressViaCepDTO addressViaCepDTO) {
		AddressResponseDTO addressResponseDTO = new AddressResponseDTO();

		addressResponseDTO.setStreet(addressViaCepDTO.getLogradouro());
		addressResponseDTO.setCity(addressViaCepDTO.getLocalidade());
		addressResponseDTO.setState(addressViaCepDTO.getUf());
		addressResponseDTO.setZipCode(addressViaCepDTO.getCep());
		addressResponseDTO.setCountry("Brasil");
		addressResponseDTO.setNeighborhood(addressViaCepDTO.getBairro());

		return addressResponseDTO;
	}

	public AddressResponseDTO addAddress(Long customerId, AddressDataDTO addressDTO) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new CustomException("Cliente não encontrado", HttpStatus.NOT_FOUND));

		AddressResponseDTO addressFromCep = getAddressFromCep(addressDTO.getZipCode());
		if (addressFromCep == null) {
			throw new CustomException("CEP não encontrado", HttpStatus.NOT_FOUND);
		}

		addressDTO.setStreet(addressFromCep.getStreet());
		addressDTO.setCity(addressFromCep.getCity());
		addressDTO.setState(addressFromCep.getState());
		addressDTO.setCountry(addressFromCep.getCountry());

		Address address = mapper.convert(addressDTO, Address.class);
		address.setCustomer(customer);
		customer.getAddresses().add(address);
		customerRepository.save(customer);

		return mapper.convert(address, AddressResponseDTO.class);
	}

	public List<AddressResponseDTO> getAddressesByCustomerId(Long customerId) {
		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new CustomException("Cliente não encontrado", HttpStatus.NOT_FOUND));

		return customer.getAddresses().stream()
				.map(address -> mapper.convert(address, AddressResponseDTO.class))
				.collect(Collectors.toList());
	}
}