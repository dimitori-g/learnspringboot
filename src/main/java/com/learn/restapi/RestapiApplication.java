package com.learn.restapi;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.UUID;

import lombok.Data;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;

@SpringBootApplication
public class RestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestapiApplication.class, args);
	}

}

@Data
@Entity
class Coffee {
	@Id
	private String id;
	private String name;

	public Coffee() {}
	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public Coffee(String name) {
		this(UUID.randomUUID().toString(), name);
	}
}

//Comment out @Component annotation for not creating data
//@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;
	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}
	@PostConstruct
	private void loadData() {
		coffeeRepository.saveAll(List.of(
				new Coffee("Coffee Gereza"),
				new Coffee("Coffee Ganador"),
				new Coffee("Coffee Lareno"),
				new Coffee("Coffee Tres Pontas")
		));
	}
}

interface CoffeeRepository extends CrudRepository<Coffee, String> {}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		return coffeeRepository.findById(id);
	}
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		return coffeeRepository.save(coffee);
	}
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		return (coffeeRepository.existsById(id))
				? new ResponseEntity<>(coffeeRepository.save(coffee),
				HttpStatus.OK)
				: new ResponseEntity<>(coffeeRepository.save(coffee),
				HttpStatus.CREATED);
	}
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
	}
}

/*
@RestController
@RequestMapping("/coffees")
class RestApiController {
	private List<Coffee> coffees = new ArrayList<>();
	public RestApiController() {
		coffees.addAll(List.of(
			new Coffee("Coffee Gereza"),
			new Coffee("Coffee Ganador"),
			new Coffee("Coffee Lareno"),
			new Coffee("Coffee Tres Pontas")
		));
	}
	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffees;
	}
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		coffees.add(coffee);
		return coffee;
	}
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		int coffeeIndex = -1;
		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}
		return (coffeeIndex == -1) ?
			new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED) :
			new ResponseEntity<> (coffee, HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffees.removeIf(c -> c.getId().equals(id));
	}
}*/
