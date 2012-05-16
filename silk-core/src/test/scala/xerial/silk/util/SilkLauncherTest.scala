/*
 * Copyright 2012 Taro L. Saito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xerial.silk.util

import io.DataProducer
import scala.io.Source
import java.io.{PrintStream, OutputStream, InputStream}
import xerial.silk.lens.ObjectSchema

//--------------------------------------
//
// SilkLauncherTest.scala
// Since: 2012/03/22 14:31
//
//--------------------------------------

object SilkLauncherTest {

  class TestModule extends SilkCommandModule with Logger {
    val moduleName = "global"

    @command(description="say hello")
    def hello = "Hello World"

    @command(description="do ping-pong")
    def ping(@argument name:String="pong")
    = "ping %s".format(name)

  }

  trait SubModule {
    @command
    def waf = {
      println("method waf is called")
      "Waf! Waf!"
    }
  }

  class MixedTrait extends TestModule with SubModule

  class StreamTestModule extends SilkCommandModule with Logger {
    val moduleName = "stream test"

    @command(description="process stream")
    def stream(in:InputStream) = {
      for(line <- Source.fromInputStream(in).getLines()) {
        debug(line)
      }
    }

  }

  
}


/**
 * @author leo
 */
class SilkLauncherTest extends SilkSpec {

  import SilkLauncherTest._
  
  "SilkLauncher" should {



    "detect global option" in {
      val sig = ObjectSchema.of[TestModule].findSignature
      trace(Source.fromString(sig.toString).getLines.filter(_.contains("MethodType")).mkString("\n"))

      SilkLauncher.of[TestModule].execute("-h")
      SilkLauncher.of[TestModule].execute("ping -h")


    }

    "call method without arguments" in {
      val ret = SilkLauncher.of[TestModule].execute("hello")
      ret must be ("Hello World")
    }

    "call method with arguments" in {
      val ret = SilkLauncher.of[TestModule].execute("ping pong")
      ret must be ("ping pong")
    }

    "use default method argumets" in {
      val ret = SilkLauncher.of[TestModule].execute("ping")
      ret must be ("ping pong")
    }

    "inherit functions defined in other traits" in {
      val s = ObjectSchema.of[MixedTrait]
      val cl = classOf[MixedTrait]
      val p = ObjectSchema.getParentsByReflection(cl)
      trace("parent classes:%s", p.mkString(", "))

      val m = s.methods
      val wafCommand = m.find(_.name == "waf")
      wafCommand must be ('defined)
      val cmd = wafCommand.get.findAnnotationOf[command]
      cmd must be ('defined)

      val l = SilkLauncher.of[MixedTrait]

      val ret= l.execute("waf")
      ret must be ("Waf! Waf!")
    }


    "pass stream input" in {
      pending
      val l = SilkLauncher.of[StreamTestModule]
      l.execute(new DataProducer() {
        def produce(out: OutputStream) {
          val p = new PrintStream(out)
          p.println("hello world!")
          p.flush
        }
      })
    }

  }

}