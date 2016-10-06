import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

//All of the examples are achieving exactly the same thing in different ways.
public class Main {
    
    public static final int ID = 456;

    public static void main(String... args) {
        Customers customers = new Customers();
        
        Customer defaultCustomer = new Customer(0, "Default");
        
        customers.addCustomer(defaultCustomer.getId(), defaultCustomer);
        customers.addCustomer(123, "Sue");
        customers.addCustomer(456, "Bob");
        customers.addCustomer(789, "Mary");
        
        Optional<Customer> optionalCustomer = customers.findOptionalCustomer(ID);
        
        //Example 1
        if (optionalCustomer.isPresent()) {
            if (optionalCustomer.get().getName().equals("Mary")) {
                System.out.println("Processing Marry!");
            } else {
                System.out.println(optionalCustomer.get());
            }
        } else {
            System.out.println(defaultCustomer);
        }
        
        //Example 2
        Consumer<Customer> processCustomer = customer -> {
            if (customer.getName().equals("Mary")) {
                System.out.println("Processing Marry!");
            } else {
                System.out.println(customer);
            }
        };        
        optionalCustomer.ifPresent(processCustomer);
        
        //Example 3 - filter
        System.out.println(
            customers.findOptionalCustomer(ID)
                     .filter(customer -> customer.getId() > 400)
                     .orElseGet(() -> customers.findOptionalCustomer(0).get()));
        
        //Example 4 - map
        System.out.println(
                customers.findOptionalCustomer(ID)
                         .map(customer -> customer.getName().trim()) //returns value wrapped in optional
                         .orElse("No name!"));
        
        //Example 5 - lambda expressions
        Function<Customer, Customer> processMary = 
                customer -> {
                    if (customer.getName() == "Mary") {
                        System.out.println("Processing Mary");
                    };
                    return customer;
                };
                
        Function<Customer, Customer> processNotMary = 
                customer -> {
                    if (customer.getName() != "Mary") {
                        System.out.println(customer);
                    };
                    return customer;
                };
                
        Function<Customer, Customer> processCustomer2 = 
                customer -> {
                    if (customer.getName() == "Mary") {
                        System.out.println("Processing Mary");
                    } else {
                        System.out.println(customer);
                    };
                    return customer;
                };
       
        try {
            customers.findOptionalCustomer(ID)
                     .map(processMary)                  //Equal to map(processCustomer2) for both maps
                     .map(processNotMary)
                     .orElseThrow(NoCustomerFoundException::new); //Happens when invalid ID was provided
        } catch (NoCustomerFoundException e) {
            e.printStackTrace();
        }
    }
}
