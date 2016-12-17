package mapreduce;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queries {

  private List<Transaction> transactions;
  
  public Queries(List<Transaction> transactions) {
    this.transactions = transactions;
  }
  
  private Comparator<Trader> compareNames = Comparator.comparing(Trader::getName);
  
  private Stream<Trader> getTradersStream() {
    return transactions.stream()
                       .map(Transaction::getTrader);
  }
  
  //1.
  public List<Transaction> getTransactionsForYearSortedByValue(int year){
    return transactions.stream()
                       .filter(transaction -> transaction.getYear() == year)
                       .sorted(Comparator.comparing(Transaction::getValue))
                       .collect(Collectors.toList());
  }
  
  //2.
  public Set<String> getDistinctCitiesOfWorkers() {
    return getTradersStream()
            .collect(Collectors.groupingBy(Trader::getCity))
            .keySet();
  }
  
  //3.
  public List<Trader> getTradersForCitySortedByName(String city) {
    return getTradersStream()            
            .filter(trader -> trader.getCity() == city)
            .distinct()
            .sorted(compareNames)
            .collect(Collectors.toList());
                       
  }
  
  //4.
  public String getAllTradersAlphabetically() {
    return getTradersStream()
            .distinct()                       
            .sorted(compareNames)
            .map(Trader::getName)
            .collect(Collectors.joining(", "));
  }
  
  //5.
  public boolean existsTraderFromCity(String city) {
    return getTradersStream()
            .anyMatch(trader -> trader.getCity() == city);
  }
  
  //6.
  public int getTransactionTotalValueForCity(String city) {
    return transactions.stream()
                       .filter(transaction -> transaction.getTrader().getCity() == city)
                       .mapToInt(Transaction::getValue)
                       .sum();
  }
}
