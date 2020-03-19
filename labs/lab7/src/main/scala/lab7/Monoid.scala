package lab7

trait Semigroup[T] {
  def combine(l: T, r: T): T
}

object Semigroup {
  def apply[T](implicit instance: Semigroup[T]): Semigroup[T] = instance

  implicit def mapCombine[K, V: Semigroup]: Semigroup[Map[K, V]] = (l, r) => {
    val updates =
      for ((lk, lv) <- l; (rk, rv) <- r if lk == rk)
        yield (lk, Semigroup[V].combine(lv, rv))
    l ++ r ++ updates
  }

  implicit val flattenCombine: Semigroup[List[Any]] = (l, r) => l ++ r
}

trait Monoid[T] extends Semigroup[T] {
  def neutral: T
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance

  implicit val listMonoid: Monoid[List[Any]] = new Monoid[List[Any]] {
    override def neutral: List[Any] = List()

    override def combine(l: List[Any], r: List[Any]): List[Any] = Semigroup.flattenCombine.combine(l, r)
  }
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {
  def apply[T](implicit instance: CommutativeSemigroup[T]): CommutativeSemigroup[T] = instance

  implicit val intCombine: CommutativeSemigroup[Int] = (l, r) => l + r

  implicit val longCombine: CommutativeSemigroup[Long] = (l, r) => l + r
  implicit val floatCombine: CommutativeSemigroup[Float] = (l, r) => l + r
  implicit val doubleCombine: CommutativeSemigroup[Double] = (l, r) => l + r


  implicit def mapCombine[K, V: CommutativeSemigroup]: CommutativeSemigroup[Map[K, V]] = (l, r) => {
    val updates =
      for ((lk, lv) <- l; (rk, rv) <- r if lk == rk)
        yield (lk, CommutativeSemigroup[V].combine(lv, rv))
    l ++ r ++ updates
  }
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {

  implicit val intCommutativeMonoid: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override def neutral: Int = 0

    override def combine(l: Int, r: Int): Int = CommutativeSemigroup.intCombine.combine(l, r)
  }

  implicit val longCommutativeMonoid: CommutativeMonoid[Long] = new CommutativeMonoid[Long] {
    override def neutral: Long = 0

    override def combine(l: Long, r: Long): Long = CommutativeSemigroup.longCombine.combine(l, r)
  }

  implicit val floatCommutativeMonoid: CommutativeMonoid[Float] = new CommutativeMonoid[Float] {
    override def neutral: Float = 0

    override def combine(l: Float, r: Float): Float = CommutativeSemigroup.floatCombine.combine(l, r)
  }

  implicit val doubleCommutativeMonoid: CommutativeMonoid[Double] = new CommutativeMonoid[Double] {
    override def neutral: Double = 0

    override def combine(l: Double, r: Double): Double = CommutativeSemigroup.doubleCombine.combine(l, r)
  }

  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] = new CommutativeMonoid[Map[K, V]] {
    override def neutral: Map[K, V] = Map[K, V]()

    override def combine(l: Map[K, V], r: Map[K, V]): Map[K, V] =
      CommutativeSemigroup.mapCombine[K, V].combine(l, r)
  }

  implicit def equalityCommutativeMonoid[T]: CommutativeMonoid[Equality[T]] = new CommutativeMonoid[Equality[T]] {
    override def neutral: Equality[T] = (_: T, _: T) => true

    override def combine(l: Equality[T], r: Equality[T]): Equality[T] =
      (a: T, b: T) => l.equal(a, b) && r.equal(a, b)
  }

  //  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] = ???
}
