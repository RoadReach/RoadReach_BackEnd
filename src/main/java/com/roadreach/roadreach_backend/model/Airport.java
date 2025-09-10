package com.roadreach.roadreach_backend.model;

import jakarta.persistence.*;

@Entity
public class Airport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String type;
	private String code;

	@ManyToOne
	@JoinColumn(name = "state_id")
	private State state;

	// Getters and setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	public State getState() { return state; }
	public void setState(State state) { this.state = state; }
}
