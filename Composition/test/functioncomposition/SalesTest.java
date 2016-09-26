package functioncomposition;

import static org.junit.Assert.*;

import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import functioncomposition.*;

public class SalesTest {
	
	private SalesPerson salesPerson;
	private Customer customer;
	private Manager manager;
	
	Function<SalesPerson, String> salesPersonToCustomerEmail;
	Function<SalesPerson, String> salesPersonToManagerEmail;
	
	@Before
	public void setup() {
		customer = new Customer("customer@gmail.com");
		manager = new Manager("manager@yahoo.com");
		salesPerson = new SalesPerson(customer, manager);
		
		Function<SalesPerson, Customer> salesPersonToCustomer = SalesPerson::getCustomer;
		Function<SalesPerson, Manager> salesPersonToManager = SalesPerson::getManager;
		Function<Customer, String> customerToEmail = Customer::getEmail;
		Function<Manager, String> managerToEmail = Manager::getEmail;
		
		salesPersonToCustomerEmail = salesPersonToCustomer.andThen(customerToEmail);
		salesPersonToManagerEmail = salesPersonToManager.andThen(managerToEmail);
	}
	
	//*******************************************************************************************************
	//									Object oriented way
	//*******************************************************************************************************

	@Test
	public void printsCustomersEmail() {
		assertThat(salesPerson.getCustomer().getEmail(), is(customer.getEmail()));
	}
	
	@Test
	public void printsManagersEmail() {
		assertThat(salesPerson.getManager().getEmail(), is(manager.getEmail()));
	}
	
	//*******************************************************************************************************
	//									Functional oriented
	//*******************************************************************************************************
	
	@Test
	public void printsCustomerEmailWithFunctionComposition() {
		assertThat(salesPersonToCustomerEmail.apply(salesPerson), is(customer.getEmail()));
	}
	
	@Test
	public void printsManagerEmailWithFunctionComposition() {
		assertThat(salesPersonToManagerEmail.apply(salesPerson), is(manager.getEmail()));
	}
	
	@Test
	public void getEmailAddressForCustomer() {
		//Re-usability of function compositions
		assertThat(SalesPerson.getEmailAddress(salesPerson, salesPersonToCustomerEmail),
				   is(customer.getEmail()));
	}
	
	@Test
	public void getEmailAddressForManager() {
		//Re-usability of function compositions
		assertThat(SalesPerson.getEmailAddress(salesPerson, salesPersonToManagerEmail),
				   is(manager.getEmail()));
	}
}
