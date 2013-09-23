package fake.db

import collection.mutable

object FakeDb {
    private[this] val objects: mutable.Map[String, Persistable] = mutable.HashMap.empty
    
    def save[P <: Persistable](p: P with NotYetPersisted): P with Persisted = {
        require(!objects.contains(p.id), "That ID is already in use")
        objects(p.id) = p
        p.asInstanceOf[P with Persisted]
    }
    
    def update[P <: Persistable with Persisted](p: P): P = {
        require(objects.contains(p.id), "No such record to update")
        objects(p.id) = p
        p
    }
    
    def find[A <: Persistable](id: String): A with Persisted =
        objects(id).asInstanceOf[A with Persisted]
}

abstract class Persistable {
    def id: String
}

trait Persisted extends Persistable
trait NotYetPersisted extends Persistable

case class Person(id: String, first: String, last: String, age: Int) extends Persistable

object Person {
    def create(id: String, first: String, last: String, age: Int): Person with NotYetPersisted = 
        new Person(id, first, last, age) with NotYetPersisted
    def updated(p: Person with Persisted)(id: String = p.id, first: String = p.first, last: String = p.last, age: Int = p.age): Person with Persisted = {
        new Person(id, first, last, age) with Persisted
    }
}