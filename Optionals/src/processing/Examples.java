package processing;

import java.util.Optional;
import java.util.function.Function;

public class Examples {
    //APIs:
    
    public static Integer addition(Integer x1, Integer x2) {
        return x1 + x2;
    }
    
    public static Optional<Integer> nullAdditionA(Optional<Integer> x1, Optional<Integer> x2) {
        return x1.flatMap(
                x1temp -> x2.map(
                        x2temp -> addition(x1temp, x2temp)));
    }
    
    //The same as in the previous method, only with explicit types declaration
    public static Optional<Integer> nullAdditionB(Optional<Integer> x1, Optional<Integer> x2) {
        return x1.flatMap(
                (Integer x1temp) -> x2.map(
                        (Integer x2temp) -> addition(x1temp, x2temp)));
    }
    
    public static Optional<Integer> nullAdditionC(Optional<Integer> x1, Optional<Integer> x2) {
        return x1.flatMap(x1temp -> x2.map(Examples.additionCurrying(x1temp)));
    }
    
    //Helper methods:
    
    private static Function<Integer, Integer> additionCurrying(Integer x1) {
        return x2 -> addition(x1, x2);
    }
}
