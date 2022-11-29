package cs2030s.fp;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/** Infinite list created with Memo 
 * to achieve lazy evaluation.
 */
public class InfiniteList<T> {
  /**
   * the head of the infinite list.
   */
  private Memo<Actually<T>> head;
  /**
   * the tail of the infinite list.
   */ 
  private Memo<InfiniteList<T>> tail;
  
  /**
   * private constructor to initialise head and tail.
   *
   * @param head the head of the infinite list
   * @param tail the tail of the infinite list
   */
  private InfiniteList(Memo<Actually<T>> head, Memo<InfiniteList<T>> tail) {
    this.head = head;
    this.tail = tail;
  }
  
  /**
   * the END indicating the end of the list.
   */ 
  private static final End END = new End(null, null);

  /**
   * generate method to generate an infinite list.
   *
   * @param <T> the type stored in the Infinite list
   * @param prod the Constant used to produce value
   * @return an infinist list containing prod wrapped in memo
   */
  public static <T> InfiniteList<T> generate(Constant<T> prod) {
    return new InfiniteList<>(Memo.from(() -> Actually.ok(prod.init())), Memo
        .from(() -> InfiniteList.generate(prod)));
  }

  /**
   * iterate method to create infinite list with
   * first element containing seed and subsequent 
   * elements transformed by func.
   *
   * @param <T> the type stored in the infinite list
   * @param seed the first value
   * @param func the immutator to transform subsequent values
   * @return infinitelist generated
   */
  public static <T> InfiniteList<T> iterate(T seed, Immutator<T, T> func) {
    return new InfiniteList<>(Memo.from(Actually.ok(seed)), Memo
        .from(() -> InfiniteList.iterate(func.invoke(seed), func)));
  }
  
  /**
   * head method to evaluate the head.
   *
   * @return T the value stored
   */
  public T head() {
    return this.head.get().except(() -> this.tail.get().head()); 
  }
 
  /**
   * tail method to return the tail.
   *
   * @return a tail as an infinite list
   */
  public InfiniteList<T> tail() {
    return this.head.get().transform(cur -> this.tail.get()).except(() -> this.tail.get().tail());
  }
  
  /**
   * a map method to map all the elements in the list to another list.
   *
   * @param <R> the return type of the immutator
   * @param f the immutator
   * @return a mapped infinite list
   */
  public <R> InfiniteList<R> map(Immutator<? extends R, ? super T> f) {
    return new InfiniteList<>(Memo.from(() -> this.head.get().transform(f)), Memo
        .from(() -> this.tail.get().map(f)));
  }
  
  /**
   * a filter method filter those out elements failing the predicate.
   *
   * @param pred the immutator serving as a predicate
   * @return an infinite list after filtering
   */
  public InfiniteList<T> filter(Immutator<Boolean, ? super T> pred) {
    return new InfiniteList<>(Memo.from(() -> this.head.get().check(pred)), Memo
        .from(() -> this.tail.get().filter(pred)));
  }
  
  /**
   * a limit method to limit the length of the infinite list.
   *
   * @param n the size of the infinite list
   * @return the infinite list with a size limit
   */
  public InfiniteList<T> limit(long n) {
    if (n < 1) {
      return InfiniteList.<T>end();
    }
    return new InfiniteList<T>(this.head, Memo.from(() -> this.tail
          .get().limit(n - this.head.get()
            .transform(x -> 1).except(() -> 0))));
  }
  
  /**
   * a takeWhile method to keep generating the list while the pred is true.
   *
   * @param pred the immutator serving as the predicate
   * @return an infinite list whose elements satisfy the predicate
   */
  public InfiniteList<T> takeWhile(Immutator<Boolean, ? super T> pred) {
    Memo<Actually<T>> head = Memo.from(() -> Actually
        .ok(this.head()).check(pred));
    return new InfiniteList<>(head, Memo.from(() -> head
        .get().transform(t -> this.tail().takeWhile(pred)).except(() -> end())));
  }
  
  /**
   * a toList method to convert the array to an array list.
   *
   * @return an ArrayList
   */
  public List<T> toList() {
    List<T> temp = new ArrayList<>();
    InfiniteList<T> list = this;
    while (!list.isEnd()) {
      list.head.get().finish(x -> temp.add(x));
      list = list.tail.get();
    }
    return temp;
  }

  /**
   * a reduce method to reduce the list using the combiner.
   *
   * @param <U> the generic type to reduce the list to.
   * @param id the identity
   * @param acc the combiner
   * @return a value of tyoe U
   */
  public <U> U reduce(U id, Combiner<? extends U, ? super U, ? super T> acc) {
    return this.head.get().transform(x -> this.tail.get().reduce(acc.combine(id, x), acc))
      .except(() -> this.tail.get().reduce(id, acc));
  }


  /**
   * a count method to count the number of elements in the list.
   *
   * @return returns a long number
   */
  public long count() {
    long identity = 0;
    return this.reduce(identity, (x, y) -> x + 1);
  }

  @Override
  public String toString() {
    return "[" + this.head + " " + this.tail + "]";
  }
  
  /**
   * an isEnd method to indicate whether this element
   * is the end of the list.
   *
   * @return boolean
   */
  public boolean isEnd() {
    return false;
  }

  /** 
   * an end method to return the end of infinite list.
   *
   * @param <T> the type of the end
   * @return an infinite list which is the end of the list
   */
  public static <T> InfiniteList<T> end() {
    @SuppressWarnings("unchecked")
    InfiniteList<T> end = (InfiniteList<T>) InfiniteList.END;
    return end;
  
  }

  /**
   * The class indicating the end of the infinite list.
   */
  private static class End extends InfiniteList<Object> {

    /**
     * a constroctor for the End.
     *
     * @param head the head of the list
     * @param tail the tail of the list
     */
    private End(Memo<Actually<Object>> head, Memo<InfiniteList<Object>> tail) {

      super(null, null);
    }

    /**
     * a head method to indicate there is no more.
     *
     * @return Exception
     */
    @Override
    public Object head() {
      throw new NoSuchElementException();
    }

    /**
     * a tail method to indicate no tail can be found.
     *
     * @return Exception
     */
    @Override
    public InfiniteList<Object> tail() {
      throw new NoSuchElementException();
    }

    /**
     * an isEnd method to indicate this is the end of the list.
     *
     * @return boolean
     */
    @Override
    public boolean isEnd() {
      return true;
    }


    /**
     * a map method to map all the elements in the list to another list.
     *
     * @param f the immutator
     * @param <R> the return type in the immutator
     * @return an End of the list
     */
    @Override 
    public <R> InfiniteList<R> map(Immutator<? extends R, ? super Object> f) {
      return InfiniteList.<R>end();
    }


    /**
     * a filter method filter those out elements failing the predicate.
     *
     * @param pred the immutator serving as a predicate
     * @return The End of the list
     */
    @Override
    public InfiniteList<Object> filter(Immutator<Boolean, ? super Object> pred) {
      return InfiniteList.<Object>end();
    }


    /**
     * a takeWhile method to keep generating the list while the pred is true.
     *
     * @param pred the immutator serving as the predicate
     * @return The End instance
     */
    @Override
    public InfiniteList<Object> takeWhile(Immutator<Boolean, ? super Object> pred) {
      return InfiniteList.<Object>end();
    }

    /**
     * a limit method to limit the size of the list.
     *
     * @param n the size of the list
     * @return The end instance
     */
    @Override
    public InfiniteList<Object> limit(long n) {
      return InfiniteList.<Object>end();
    }


    /**
     * a reduce method to reduce the list using the combiner.
     *
     * @param <U> the generic type to reduce the list to.
     * @param id the identity
     * @param acc the combiner
     * @return the identity
     */
    @Override
    public <U> U reduce(U id, Combiner<? extends U, ? super U, ? super Object> acc) {
      return id;
    }

    @Override
    public String toString() {
      return "-";
    }

  }


  
}
