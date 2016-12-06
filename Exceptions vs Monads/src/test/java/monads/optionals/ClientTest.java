package monads.optionals;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import errorcodes.Client;
import errorcodes.DataAccess;
import errorcodes.Server;

public class ClientTest {
    
    private Client client;
        
    @Before
    public void setup() {
        client = new Client(
                new Server(
                        new DataAccess()));
    }

    @Test
    public void givenCorrectInputReturnsExpectedOutput() {
        assertEquals("something", 
                     client.execute("ready for reading"));
    }
    
    @Test
    public void givenIncorrectInputReturnsFailingOutput() {
        assertEquals("request failed", 
                     client.execute("wrong input"));
    }
}
