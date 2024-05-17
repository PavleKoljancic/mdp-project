package model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class Ingredient implements Serializable{
	

	private String name;
	private double quantityAvailable;
	private double pricePerQuantity;
	
	public Ingredient() {

	}

	public Ingredient(String name, double quantityAvailable, double pricePerQuantity) {
		this.name = name;
		this.quantityAvailable = quantityAvailable;
		this.pricePerQuantity = pricePerQuantity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getQuantityAvailable() {
		return quantityAvailable;
	}

	public void setQuantityAvailable(double quantityAvailable) {
		this.quantityAvailable = quantityAvailable;
	}

	public double getPricePerQuantity() {
		return pricePerQuantity;
	}

	public void setPricePerQuantity(double pricePerQuantity) {
		this.pricePerQuantity = pricePerQuantity;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingredient other = (Ingredient) obj;
		return Objects.equals(name, other.name);
	}

	public HashMap<String,String> toHashMap() 
	{
		HashMap<String,String> result = new HashMap<String, String>();
		result.put("name",this.name);
		result.put("quantity",  Double.toString(quantityAvailable));
		return result;
	}
}
