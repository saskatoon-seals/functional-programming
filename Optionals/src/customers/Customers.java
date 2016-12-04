package customers;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Optional;

public class Customers {

    private AbstractMap<Integer, Customer> customers = new HashMap<>();
    
    public void addCustomer(int id, String name) {
        addCustomer(id, new Customer(id, name));
    }
    
    public void addCustomer(int id, Customer customer) {
        customers.put(id, customer);
    }
    
    public Customer findCustomer(int id) {
        return customers.get(id);
    }
    
    /**
     * Finds and returns customer with the specified id
     * 
     * @param id - customer id
     * @return optional - Optional.empty() in the case when customer doesn't exist, otherwise
     *                    returns Optional.of(customer)
     */
    public Optional<Customer> findOptionalCustomer(int id) {
        return Optional.ofNullable(findCustomer(id));
    }
}
