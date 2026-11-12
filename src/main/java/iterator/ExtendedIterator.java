package iterator;

import java.util.Iterator;

public interface ExtendedIterator<T> extends Iterator<T> {
    public T previous();
    public boolean hasPrevious();
    public void goFirst();
    public void goLast();
}