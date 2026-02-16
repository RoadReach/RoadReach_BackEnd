package com.roadreach.roadreach_backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Country {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String code;
	private String name;
	private String locale;
	private String currencyCode;
	private String currencySymbol;
	private String currencyFormat;
	private String dateFormat;

	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<State> states;

	// Getters and setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getLocale() { return locale; }
	public void setLocale(String locale) { this.locale = locale; }
	public String getCurrencyCode() { return currencyCode; }
	public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
	public String getCurrencySymbol() { return currencySymbol; }
	public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }
	public String getCurrencyFormat() { return currencyFormat; }
	public void setCurrencyFormat(String currencyFormat) { this.currencyFormat = currencyFormat; }
	public String getDateFormat() { return dateFormat; }
	public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
	public List<State> getStates() { return states; }
	public void setStates(List<State> states) { this.states = states; }
}
