/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xerial.silk.weaver.jdbc.sqlite

import xerial.silk.core.{SilkOp, Silk, SilkSpec}

object SQLiteWeaverTest {


}

/**
 *
 */
class SQLiteWeaverTest extends SilkSpec {

  import xerial.silk._
  import Silk._

  "SQLiteWeaver" should {
    "run SQL query" in {

      val db = SQLite.openDatabase("target/sample.db")
      val select = db.sql("select 1")

      info(select)
      val g = SilkOp.createOpGraph(select)
      info(g)

      val w = new SQLiteWeaver
      w.weave(select)

    }

    "run pipeline query" in {
      val db = SQLite.createDatabase("target/sample2.db")
      val t = db.sql("create table t (id integer, name string)")
      val insert = for(i <- 0 until 3) yield {
        db.sql(s"insert into t values(${i}, 'leo')") dependsOn t
      }
      val populate = insert.toSilk dependsOn t


      val g = SilkOp.createOpGraph(populate)
      info(g)

    }

  }
}