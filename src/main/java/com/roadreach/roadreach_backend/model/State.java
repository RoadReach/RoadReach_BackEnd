package com.roadreach.roadreach_backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class State {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String abbreviation;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<City> cities;

	@OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Airport> airports;

	// Getters and setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getAbbreviation() { return abbreviation; }
	public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
	public Country getCountry() { return country; }
	public void setCountry(Country country) { this.country = country; }
	public List<City> getCities() { return cities; }
	public void setCities(List<City> cities) { this.cities = cities; }
	public List<Airport> getAirports() { return airports; }
	public void setAirports(List<Airport> airports) { this.airports = airports; }
}
