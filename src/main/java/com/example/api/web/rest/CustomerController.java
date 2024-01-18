package com.example.api.web.rest;

import java.util.List;
import javax.validation.Valid;

import com.example.api.dto.AddressDataDTO;
import com.example.api.dto.AddressResponseDTO;
import com.example.api.dto.CustomerDataDTO;
import com.example.api.dto.CustomerResponseDTO;
import com.example.api.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/customers")
@Api(tags = "customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping
	@ApiOperation(
			value = "Adicionar um novo cliente",
			response = CustomerResponseDTO.class,
			authorizations = {@Authorization(value="apiKey")}
	)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Cliente criado com sucesso"),
			@ApiResponse(code = 400, message = "Dados inválidos do cliente")
	})
	public ResponseEntity<CustomerResponseDTO> addCustomer(@Valid @RequestBody CustomerDataDTO customerDataDTO) {
		CustomerResponseDTO customer = customerService.addCustomer(customerDataDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(customer);
	}

	@GetMapping
	@ApiOperation(value = "Listar todos os clientes com filtros opcionais",
			response = CustomerResponseDTO.class,
			responseContainer = "List",
			authorizations = {@Authorization(value = "apiKey")})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Busca realizada com sucesso")
	})
	public ResponseEntity<Page<CustomerResponseDTO>> getAllCustomers(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String gender,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<CustomerResponseDTO> customers = customerService.getAllCustomers(name, email, gender, PageRequest.of(page, size));
		return ResponseEntity.ok(customers);
	}

	@GetMapping("/{id}")
	@ApiOperation(
			value = "Obter um cliente pelo ID",
			response = CustomerResponseDTO.class,
			authorizations = {@Authorization(value="apiKey")}
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cliente encontrado"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<CustomerResponseDTO> getCustomer(@PathVariable Long id) {
		CustomerResponseDTO customer = customerService.getCustomer(id);
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/{id}")
	@ApiOperation(
			value = "Atualizar um cliente",
			response = CustomerResponseDTO.class,
			authorizations = {@Authorization(value="apiKey")}
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Cliente atualizado com sucesso"),
			@ApiResponse(code = 400, message = "Dados inválidos do cliente"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<CustomerResponseDTO> updateCustomer(@Valid @PathVariable Long id, @RequestBody CustomerDataDTO customerDataDTO) {
		CustomerResponseDTO customer = customerService.updateCustomer(id, customerDataDTO);
		return ResponseEntity.ok(customer);
	}

	@DeleteMapping("/{id}")
	@ApiOperation(
			value = "Deletar um cliente",
			authorizations = {@Authorization(value="apiKey")}
	)
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Cliente excluído com sucesso"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		customerService.deleteCustomer(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{customerId}/addresses")
	@ApiOperation(
			value = "Adicionar um endereço a um cliente",
			authorizations = {@Authorization(value = "apiKey")}
	)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Endereço adicionado com sucesso"),
			@ApiResponse(code = 400, message = "Dados inválidos do endereço"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<AddressResponseDTO> addAddress(@PathVariable Long customerId, @Valid @RequestBody AddressDataDTO addressDTO) {
		AddressResponseDTO address = customerService.addAddress(customerId, addressDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(address);
	}

	@GetMapping("/{customerId}/addresses")
	@ApiOperation(
			value = "Listar todos os endereços de um cliente",
			response = AddressResponseDTO.class,
			responseContainer = "List",
			authorizations = {@Authorization(value = "apiKey")}
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Endereços recuperados com sucesso"),
			@ApiResponse(code = 404, message = "Cliente não encontrado")
	})
	public ResponseEntity<List<AddressResponseDTO>> getAddresses(@PathVariable Long customerId) {
		List<AddressResponseDTO> addresses = customerService.getAddressesByCustomerId(customerId);
		return ResponseEntity.ok(addresses);
	}
}
