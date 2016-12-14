package collecting;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summingInt;

import java.util.ArrayList;
import java.util.List;

import datastructures.Dish;

public class Collecting {
  
  List<Dish> menu = new ArrayList<>();
  
  //Examples with Stream's collect:
  
  //The most primitive solution
  public int getTotalCaloriesA() {
    return menu.stream()
               .collect(reducing(0,
                                 Dish::getCalories,
                                 (calories1, calories2) -> calories1 + calories2));
  }
  
  public int getTotalCaloriesB() {
    return menu.stream()
               .collect(reducing(0,
                                 Dish::getCalories,
                                 Integer::sum));
  }
  
  //The most sophisticated solution
  public int getTotalCaloriesC() {
    return menu.stream()
               .collect(summingInt(Dish::getCalories));
  }
  
  //Examples with Stream's reduce:
  
  public int getTotalCaloriesD() {
    return menu.stream()
               .map(Dish::getCalories)
               .reduce(Integer::sum)
               .get();
  }
  
  //Converts to IntStream that has sum() method
  public int getTotalCaloriesE() {
    return menu.stream()
               .mapToInt(Dish::getCalories)
               .sum();
  }
}
