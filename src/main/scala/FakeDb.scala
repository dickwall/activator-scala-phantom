package fake.db

import collection.mutable

object FakeDb {
    private[this] val objects: mutable.Map[String, Persistable] = mutable.HashMap.empty
    
    def save(p: Persistable): Unit = {
        require(!objects.contains(p.id), "That ID is already in use")
        objects(p.id) = p
    }
    
    def update(p: Persistable): Unit = {
        require(objects.contains(p.id), "No such record to update")
        objects(p.id) = p
    }
    
    def find[A <: Persistable](id: String): A =
        objects(id).asInstanceOf[A]
}

abstract class Persistable {
    def id: String
}

trait Persisted extends Persistable
trait NotYetPersisted extends Persistable

case class Person(id: String, first: String, last: String, age: Int) extends Persistable