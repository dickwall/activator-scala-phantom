package fake.db

import collection.mutable

// The sort of fake you might use to replace a real DB in tests
// This one is not very good since it is not thread safe for parallel testing
object FakeDb {
    private[this] val objects: mutable.Map[String, Persistable] = mutable.HashMap.empty
    
    // Saving a persistable object gives back that object "with Persisted"
    def save[P <: Persistable](p: P with NotYetPersisted): P with Persisted = {
        require(!objects.contains(p.id), "That ID is already in use")
        objects(p.id) = p
        p.asInstanceOf[P with Persisted]
    }
    
    // Updates can only be done on a Persistable that has already been Persisted
    // and the result is also "with Persisted"
    def update[P <: Persistable with Persisted](p: P): P = {
        require(objects.contains(p.id), "No such record to update")
        objects(p.id) = p
        p
    }
    
    // Clearly if we pull an item out of the database, it has been Persisted
    def find[P <: Persistable](id: String): P with Persisted =
        objects(id).asInstanceOf[P with Persisted]
}

// Simple Persistable super-class - defines an ID
abstract class Persistable {
    def id: String
}

// Our phantom types, Persisted means it has been saved, NotYetPersisted means it hasn't
trait Persisted extends Persistable
trait NotYetPersisted extends Persistable

// A simple case class that implements Persistable
case class Person(id: String, first: String, last: String, age: Int) extends Persistable

// Companion object that provides factory methods for creating and updating Person
// with the correct types being used on the results
object Person {
    
    // A newly created person will be NotYetPersisted (until saved)
    def create(id: String, first: String, last: String, age: Int): Person with NotYetPersisted = 
        new Person(id, first, last, age) with NotYetPersisted
    
    // A person can only be updated if already Persisted
    def updated(p: Person with Persisted)(id: String = p.id, first: String = p.first, last: String = p.last, age: Int = p.age): Person with Persisted =
        new Person(id, first, last, age) with Persisted
}