/*
 * Generation of sequences of numbers:
 * 
 * 1. deterministic:
 *    a. with iterator
 *    b. with generator
 * 
 * 2. random:
 *    a. with iterator
 *    b. with generator
 */

package infinitestreams;

import java.util.Random;
import java.util.stream.Stream;

public class Numbers {

    static int number = 0;

    private static int nextNumber() {
        return number++;
    }

    public static void executeDeterministicSequence(boolean ITERATOR_OPTION) {
        if (ITERATOR_OPTION) {
            Stream.iterate(0, n -> n + 1).limit(10).forEach(System.out::println);
        } else {
            Stream.generate(Numbers::nextNumber).limit(10).forEach(System.out::println);
        }
    }

    public static void executeRandomSequence(boolean ITERATOR_OPTION) {
        if (ITERATOR_OPTION) {

        } else {
            Random random = new Random();

            Stream.generate(() -> random.nextInt()).limit(10).forEach(System.out::println);
        }
    }
}
