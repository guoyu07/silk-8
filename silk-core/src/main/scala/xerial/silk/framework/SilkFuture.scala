package xerial.silk.framework

import xerial.silk.SilkException


/**
 * SilkFuture interface to postpone the result acquisition.
 * @tparam A
 */
trait SilkFuture[A] {

  def respond(k: A => Unit): Unit

  /**
   * Get the result. This operation blocks until the result will be available.
   * @return the value
   */
  def get : A = {
    var v : A = null.asInstanceOf[A]
    respond(x => v = x)
    v
  }

  /**
   * Supply the data value for this future. Any process awaiting result will be signalled after this method.
   * @param v
   */
  def set(v: A) : Unit = throw SilkException.na

  def foreach(k: A => Unit) { respond(k) }

  def map[B](f: A => B) = new SilkFuture[B] {
    def respond(k: B => Unit) {
      SilkFuture.this.respond(x => k(f(x)))
    }
  }

  def flatMap[B](f: A => Responder[B]) = new SilkFuture[B] {
    def respond(k: B => Unit) {
      SilkFuture.this.respond(x => f(x).respond(k))
    }
  }

  def filter(p: A => Boolean) = new SilkFuture[A] {
    def respond(k: A => Unit) {
      SilkFuture.this.respond(x => if (p(x)) k(x) else ())
    }
  }

}

class ConcreteSilkFuture[A](v:A) extends SilkFuture[A] {
  def respond(k: A => Unit) : Unit = { k(v) }
}


/**
 * SilkFuture implementation for multi-threaded code.
 * @tparam A
 */
class SilkFutureMultiThread[A](@volatile private var holder: Option[A] = None) extends SilkFuture[A] with Guard {
  private val notNull = newCondition

  override def set(v: A) : Unit = {
    guard {
      holder = Some(v)
      notNull.signalAll
    }
  }

  def respond(k: (A) => Unit) {
    guard {
      while (holder.isEmpty) {
        notNull.await
      }
      k(holder.get)
    }
  }


}


