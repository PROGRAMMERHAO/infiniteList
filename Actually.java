/**
 * an abstract class which have
 * two nested classes Success
 * and Failure.
 *
 * @author HAOZEYU
 */

package cs2030s.fp;
public abstract class Actually<T> implements Immutatorable<T>, Actionable<T> {
  private T input;
  public abstract T except(Constant<? extends T> value); 
  public abstract T unwrap();
  public abstract void finish(Action<? super T> value);
  public abstract <R extends T> T unless(R value);
  public abstract <R> Actually<R> next(Immutator<Actually<R>, ? super T> value);
  @Override
  public abstract <R> Actually<R> transform(Immutator<? extends R, ? super T> value);
  private static final class Success<T> extends Actually<T> {
    private T input;
    protected Success(T input) {
      this.input = input;
    }
    @Override
    public T unwrap() {
      return this.input;
    }
    @Override
    public T except(Constant<? extends T> value) {
      return this.input;
    }
    @Override 
    public void finish(Action<? super T> value) {
      value.call(this.input);
    }
    @Override
    public void act(Action<? super T> value) {
      value.call(this.input);
    }
    @Override
    public <R> Actually<R> next(Immutator<? extends Actually<? extends R>, ? super T> value) {
      try {
        return value.invoke(this.input);
      } catch (Exception e) {
        return Actually.err(e);
      }
    }
    @Override
    public <R extends T> T unless(R value) {

      return this.input;
    }
    @Override
    public <R> Actually<R> transform(Immutator<? extends R, ? super T> value) {
      try { 
        return Actually.ok(value.invoke(this.input));
      } catch (Exception e) {
        return Actually.err(e);
      }
    }
    @Override 
    public String toString() {
      if (this.input == null) {
        return "<null>";
      }
      return "<" + this.input.toString() + ">";

    }
    @Override
    public boolean equals(Object toCheck) {

      if (toCheck == this) {
        return true;
      }
      if (toCheck instanceof Success<?>) {
        Success<?> temp = (Success<?>) toCheck;
        if (temp.input == this.input) {
          return true;
        }
        if (this.input == null || temp.input == null) {
          return false;
        }
        return this.input.equals(temp.input);
      }
      return false;
    }

  }

  private static final class Failure extends Actually<Object> {
    private Exception input;
    protected Failure(Exception input) {
      this.input = input;
    }

    @Override 
    public String toString() {
      return "[" + this.input.getClass().getName().toString() + "] " + 
        this.input.getMessage();
    }
    @Override
    public Exception unwrap() {
      return this.input;
    }
    @Override
    public Object except(Constant<?> value) {
      return value.init();
    }
    @Override
    public void finish(Action<? super Object> input) {
    }
    @Override
    public void act(Action<? super Object> input) {
    }
    @Override
    public <R extends Object> Object unless(R value) {
      return value;
    }

    @Override 
    public <R> Actually<R> transform(Immutator<? extends R, ? super Object> value) {
      return Actually.err(this.input);
    }

    @Override
    public <R> Actually<R> next(Immutator<Actually<R>,
        ? super Object> value) {
      return Actually.err(this.input);
    }
    @Override
    public boolean equals(Object toCheck) {
      if (toCheck == this) {
        return true;
      }
      if (toCheck instanceof Failure) {
        Failure temp = (Failure) toCheck;
        if (temp.input == this.input) {
          return true;
        } else if (this.input == null || temp.input == null) {
          return false;
        } else if (this.input.getMessage() == null || temp.input.getMessage() == null) {
          return false;
        } else {
          return this.input.getMessage().equals(temp.input.getMessage());
        }
      }
      return false;
    }
  }
  public static <T> Actually<T> ok(T res) {
    @SuppressWarnings("unchecked")
    Actually<T> temp = (Actually<T>) new Success<>(res);
    return temp;
  }
  public static <T> Actually<T> err(Exception exception) {
  
    @SuppressWarnings("unchecked")
    Actually<T> temp = (Actually<T>) new Failure(exception);
    return temp;
  }
}
