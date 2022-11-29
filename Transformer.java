/**
 * a class that can chain two
 * transformers.
 * @author HAO ZEYU
 */


package cs2030s.fp;

public abstract class Transformer<R, P> implements Immutator<R, P> {

  public abstract R invoke(P input);
  
  public <N> Transformer<R, N> after(Transformer<? extends P, ? super N> value) {

    Transformer<R, P> f = this;
    return new Transformer<R, N>() {
      public R invoke(N input) {
        return f.invoke(value.invoke(input));
      }
    };
  }


  public <T> Transformer<T, P> before(Transformer<? extends T, ? super R> value) {

    Transformer<R, P> f = this;
    return new Transformer<T, P>() {
      public T invoke(P input) {
        return value.invoke(f.invoke(input));
      }
    };
  }
}

