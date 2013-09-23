import org.scalatest._
import org.scalatest.matchers.ShouldMatchers
import fake.db._

class DBPersonSpec extends FunSpec with ShouldMatchers {
  describe("The FakeDb") {
      it ("should allow new people to be saved") {
          val p1 = Person("id1", "Fred", "Smith", 23)
          FakeDb.save(p1)
      }
      
      it ("should not allow existing IDs to be saved") {
          val p1 = Person("id2", "Fred", "Blogs", 21)
          val p2 = Person("id2", "Bad", "Save", 12)
          FakeDb.save(p1)
          intercept[IllegalArgumentException] {
              FakeDb.save(p2)
          }
      }
      
      it ("should allow existing IDs to be updated") {
          val p1 = Person("id3", "To Be", "Updated", 11)
          val p2 = p1.copy(first = "Has Been", age = 12)
          
          FakeDb.save(p1)
          assert(FakeDb.find[Person]("id3") === p1)
          
          FakeDb.update(p2)
          assert(FakeDb.find[Person]("id3") === p2)
      }
      
      it ("should not allow new IDs to be updated") {
          val p1 = Person("id4", "To Be", "Updated", 11)
          val p2 = p1.copy(first = "Has Been", age = 12)
          
          intercept[IllegalArgumentException] {
              FakeDb.update(p2)
          }
          intercept[NoSuchElementException] {
              assert(FakeDb.find[Person]("id4") === p2)
          }
      }
      
      it ("should allow an item retrieved from the DB to be updated but not saved") {
          val p1 = Person("id5", "Saved", "ToDB", 55)
          FakeDb.save(p1)
          
          val result = FakeDb.find[Person]("id5")
          assert(result == p1)
          
          val p2 = result.copy(first = "SavedAgain")
          intercept[IllegalArgumentException] {
              FakeDb.save(p2)
          }
          
          assert(FakeDb.find[Person]("id5") == p1)
          
          FakeDb.update(p2)
          
          assert(FakeDb.find[Person]("id5") == p2)
      }
  }
}
