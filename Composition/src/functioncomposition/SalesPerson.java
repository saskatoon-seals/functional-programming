package functioncomposition;

import java.util.function.Function;

public class SalesPerson {

	private Customer customer;
	private Manager manager;
	
	public SalesPerson(Customer customer) {
		this(customer, null);
	}
	
	public SalesPerson(Manager manager) {
		this(null, manager);
	}
	
	public SalesPerson(Customer customer, Manager manager) {
		this.customer = customer;
		this.manager = manager;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public Manager getManager() {
		return manager;
	}
	
	//Flexibility of passing function compositions around
	public static String getEmailAddress(SalesPerson salesPerson, 
								  Function<SalesPerson, String> toEmailAddress) {
		return toEmailAddress.apply(salesPerson);
	}
}
