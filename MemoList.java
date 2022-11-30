import cs2030s.fp.Combiner;
import cs2030s.fp.Immutator;
import cs2030s.fp.Memo;
import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper around a lazily evaluated and memoized
 * list that can be generated with a lambda expression.
 *
 * @author Adi Yoga S. Prabawa
 * @version CS2030S AY 22/23 Sem 1
 */
class MemoList<T> {
  /** The wrapped java.util.List object */
  private List<Memo<T>> list;

  /** 
   * A private constructor to initialize the list to the given one. 
   *
   * @param list The given java.util.List to wrap around.
   */
  private MemoList(List<Memo<T>> list) {
    this.list = list;
  }

  /** 
   * Generate the content of the list.  Given x and a lambda f, 
   generate the list of n elements as [x, f(x), f(f(x)), f(f(f(x))), 
   ... ]
   *
   * @param <T> The type of the elements in the list.
   * @param n The number of elements.
   * @param seed The first element.
   * @param f The immutator function on the elements.
   * @return The created list.
   */
  public static <T> MemoList<T> generate(int n, T seed, Immutator<? extends T, ? super T> f) {
    MemoList<T> memoList = new MemoList<>(new ArrayList<Memo<T>>());
    Memo<T> curr = Memo.from(seed);
    for (int i = 0; i < n; i++) {
      memoList.list.add(curr);
      curr = curr.transform(f);
    }
    return memoList;
  }


  /** 
   * Generate the content of the fibonacci list. Given the first and the 
   second element, generate the finobacci sequence of size n using the 
   combiner.
   *
   * @param <T> The type of the elements in the list.
   * @param n The number of elements.
   * @param fst The first element.
   * @param snd The second element.
   * @param f The combiner function on the elements.
   * @return The created list.
   */
  public static <T> MemoList<T> generate(int n, T fst, T snd, 
      Combiner<? extends T, ? super T, ? super T> f) {
    MemoList<T> memoList = new MemoList<>(new ArrayList<Memo<T>>());
    Memo<T> prev = Memo.from(fst);
    Memo<T> next = Memo.from(snd);
    memoList.list.add(prev);
    memoList.list.add(next);
    for (int i = 2; i < n; i++) {
      Memo<T> temp = prev.combine(next, f);
      prev = next;
      memoList.list.add(temp);
      next = temp;
    }
    return memoList;
  }


  /** 
   * Convert the elements of the list into a new list.  Given an immutator, 
   transform each element into a new list such as [f(a),f(b),f(c)...]
   *
   * @param <R> The type of the elements in the list.
   * @param immutate The immutator function on the elements.
   * @return The created list.
   */
  public <R> MemoList<R> map(Immutator<? extends R, ? super T> immutate) {
    MemoList<R> memolist = new MemoList<>(new ArrayList<Memo<R>>());
    for (int i = 0; i < this.list.size(); i++) {
      memolist.list.add(this.list.get(i).transform(immutate));
    }
    return memolist;
  }


  /** 
   * Convert the elements of the list into a new list.  Given an immutator, 
   transform each element into a new list such as [a0,a1....,b0,b1...]
   *
   * @param <R> The type of the elements in the list.
   * @param immutate The immutator function on the elements.
   * @return The created list.
   */
  public <R> MemoList<R> flatMap(Immutator<? extends MemoList<R>, ? super T> immutate) {
    MemoList<R> memolist = new MemoList<>(new ArrayList<Memo<R>>());
    for (int i = 0; i < this.list.size(); i++) {
      MemoList<R> changed = immutate.invoke(this.get(i));
      for (int j = 0; j < changed.list.size(); j++) {
        memolist.list.add(changed.list.get(j));
      }
    }
    return memolist;
  }
     

  /** 
   * Return the element at index i of the list.  
   *
   * @param i The index of the element to retrieved (0 for the 1st element).
   * @return The element at index i.
   */
  public T get(int i) {
    return this.list.get(i).get();
  }

  /** 
   * Find the index of a given element.
   *
   * @param v The value of the element to look for.
   * @return The index of the element in the list.  -1 is element is not in the
   list
   */
  public int indexOf(T v) {
    return this.list.indexOf(Memo.from(v));
  }

  /** 
   * Return the string representation of the list.
   *
   * @return The string representation of the list.
   */
  @Override
  public String toString() {
    return this.list.toString();
  }
}
