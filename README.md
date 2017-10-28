# Functional Programming

Example implementations of different concepts of functional programming in Java and Scala.

In object-oriented programming we define classes and then extend them, overloading their methods. In functional programming, we define higher order functions as interfaces and call them with different parameters, resulting in different behaviors.

Functions are first class values, meaning they can be passed around as any other data. The paradigm mainly uses pure functions that don't have side-effects, meaning:
 - No I/O
 - No (checked) exceptions 
 - No global mutable state
 
Applications are normally built by composing pure functions together and side-effects are encapsulated in a form of monads. Normally there's an application layer that glues everything together and where I/O happens. 

Lazy evaluation helps represent infinite data structures and has performance benefits.

Because there's no shared mutable data involved, parallelism comes for "free" as there's no need to synchronize the data access.

Reference: 
- Richard M. Reese: "Learning Java Functional Programming", 2015.
- Raoul-Gabriel Urma: "Java 8 in Action", 2014
- Nickolay Tsvetinov: "Learning Reactive Programming with Java 8", 2016
- Paul Chiusano, Runar Bjarnason: "Functional Programming in Scala", 2015
