import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import fake.db._

class DBPersonSpec extends FunSpec with ShouldMatchers {
  describe("The FakeDb") {
      it ("should allow new people to be saved") {
          val p1 = Person.create("id1", "Fred", "Smith", 23)
          FakeDb.save(p1)
      }
      
      it ("should not allow existing IDs to be saved") {
          // this is still a runtime test, UUIDs being assigned from DB
          // could eliminate this error also
          val p1 = Person.create("id2", "Fred", "Blogs", 21)
          val p2 = Person.create("id2", "Bad", "Save", 12)
          FakeDb.save(p1)
          intercept[IllegalArgumentException] {
              FakeDb.save(p2)
          }
      }
      
      // Only saved objects can be updated - try updating p1 before it has been saved
      it ("should allow existing IDs to be updated") {
          val p1 = Person.create("id3", "To Be", "Updated", 11)
          
          // FakeDb.update(p1)    // try uncommenting this to see what happens
          
          val p1Saved = FakeDb.save(p1)
          
          assert(FakeDb.find[Person]("id3") === p1)
          
          // now we can update the saved person.
          val p2 = Person.updated(p1Saved)(first = "Has Been", age = 12)
          FakeDb.update(p2)
          
          assert(FakeDb.find[Person]("id3") === p2)
      }
      
      it ("should not allow new IDs to be updated") {
          val p1 = Person.create("id4", "To Be", "Updated", 11)
          // we can't do this, since p1 has to be saved before it can be updated - try
          // val p2 = Person.updated(p1)(first = "Has Been", age = 12)
          
          // FakeDb.update(p2)
      }
      
      it ("should allow an item retrieved from the DB to be updated but not saved") {
          val p1 = Person.create("id5", "Saved", "ToDB", 55)
          FakeDb.save(p1)
          
          val result = FakeDb.find[Person]("id5")
          assert(result == p1)
          
          val p2 = Person.updated(result)(first = "SavedAgain")
          
          // we can't save p2 again since it is already Persisted - try
          // FakeDb.save(p2)
          
          assert(FakeDb.find[Person]("id5") == p1)
          
          FakeDb.update(p2)
          
          assert(FakeDb.find[Person]("id5") == p2)
      }
  }
}
