package cs2030s.fp;

/**
 * the Memo class that memoize the 
 * value.
 */
public class Memo<T> extends Lazy<T> {
  /**
   * the value of Acutally type.
   */
  private Actually<T> value;

  /**
   * the constructor of Memo.
   *
   * @param value the value passed in
   to lazy and actually
   */
  protected Memo(T value) {
    super(() -> value);
    this.value = Actually.<T>ok(value);
  }
  protected Memo(Constant<? extends T> value) {
    super(value);
    this.value = Actually.<T>err(null);
  }

  /**
   * the factory method of Memo.
   *
   * @param <T> the generic input type
   *
   * @param v the value passed to Memo
   *
   * @return a Memo that contains the 
   value v in its Actually value
   */
  public static <T> Memo<T> from(T v) {
    return new Memo<T>(v); 
  }

  
  public static <T> Memo<T> from(Constant<? extends T> c) {
    return new Memo<T>(c); 
  }

  @Override 
  public <R> Memo<R> transform(Immutator<? extends R, ? super T> immute) {
    return Memo.<R>from(() -> immute.invoke(this.get()));
  }

  @Override
  public <R> Memo<R> next(Immutator<? extends Lazy<? extends R>, ? super T> input) {
    return Memo.<R>from(() -> input.invoke(this.get()).get());
  }

  public <R, S> Memo<R> combine(Memo<? extends S> memo,  
      Combiner<? extends R, ? super T, ? super S> combiner) {
    return Memo.<R>from(() -> combiner.combine(this.get(), memo.get()));
  }

  @Override
  public T get() {
    T toGet = this.value.except(() -> super.get());
    this.value = Actually.<T>ok(toGet);
    return toGet;
  }

  @Override
  public String toString() {
    return this.value.next(temp -> Actually.ok(String.valueOf(temp))).unless("?");
  }
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Memo<?>) {
      Memo<?> temp = (Memo<?>) o;
      if (temp.get().equals(this.get())) {
        return true;
      }
      return false;
    }
    return false;
  }
}

 
