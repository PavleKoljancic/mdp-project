package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

public class Candy implements Serializable{
	private String name;
	private double price;
	private double quantity;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	@Override
	public String toString() {
		return "Name=" + name + ", Price:" + price + ", Quantity=" + quantity;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Candy(String name, double price, double quantity) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
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
		Candy other = (Candy) obj;
		return Objects.equals(name, other.name);
	}
	public double getQuantity() {
		return quantity;
	}
	public Candy() {
		// TODO Auto-generated constructor stub
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public HashMap<String,String> toHashMap() 
	{
		HashMap<String,String> result = new HashMap<String, String>();
		result.put("name",this.name);
		result.put("quantity",  Double.toString(this.quantity));
		result.put("price",  Double.toString(this.price));
		return result;
	}
}
