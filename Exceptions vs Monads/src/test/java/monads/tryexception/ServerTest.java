package monads.tryexception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import io.meat.Try;

public class ServerTest {

    Server server;
    
    @Before
    public void setup() {
        server = new Server(
                new DataAccess());
    }
    
    @Test
    public void givenCorrectInputReturnsOptionalWithString() {
        Optional<String> result = server.makeRequest("ready for reading");
        
        assertEquals("something", result.get());
    }
    
    @Test
    public void givenWrongInputReturnsOptionalEmpty() {
        Optional<String> result = server.makeRequest("wrong input");
        
        assertEquals(Optional.empty(), result);
    }
    
    @Test
    public void givenCorrectInputReturnsTryWithString() {
        Try<String> result = server.makeRequestWithTry("ready for reading");
        
        assertEquals("something", result.get());
    }
    
    @Test
    public void givenWrongInputReturnsTryWithException() {
        Try<String> result = server.makeRequestWithTry("wrong input");
        
        assertEquals(IOException.class, result.getFailure()
                                              .get()
                                              .getClass());
    }
    
    @Test
    public void throwsWhenTryingToGetResultOfFailedTry() {
        Try<String> result = server.makeRequestWithTry("wrong input");
        
        try {
            result.get();
            fail("Should throw an exception!");
        } catch (RuntimeException e) {
            assertEquals(IOException.class, e.getCause()
                                             .getClass());
        }        
    }
}
