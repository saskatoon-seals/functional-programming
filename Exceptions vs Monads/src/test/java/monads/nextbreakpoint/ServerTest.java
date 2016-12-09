package monads.nextbreakpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.nextbreakpoint.Try;

public class ServerTest {

    Server server;
    
    @Before
    public void setup() {
        server = new Server(
                new DataAccess());
    }
        
    @Test
    public void givenCorrectInputReturnsTryWithString() {
        Try<String, ?> result = server.makeRequestWithTry("ready for reading");
        
        assertEquals("something", result.get());
    }
    
    @Test
    public void givenWrongInputReturnsTryWithIoExceptionAndEmptyOptional() {
        Try<String, IOException> result = server.makeRequestWithTry("wrong input");
        
        assertTrue(result.isFailure());
        assertFalse(result.isPresent());
        
        result.ifFailure(e -> {
            if (e instanceof IOException == false) {        
                fail();
            }
        });
        
        assertEquals(Optional.empty(), result.value());
    }
    
    @Test
    public void throwsWhenTryingToGetResultOfFailedTry() {
        Try<String, IOException> result = server.makeRequestWithTry("wrong input");
        
        try {
            result.get();
            fail("Should throw an exception!");
        } catch (NoSuchElementException e) {
            return;
        }        
        
        fail("Should throw an exception");
    }
}
