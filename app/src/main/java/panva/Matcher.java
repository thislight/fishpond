package panva;

public class Matcher<T> {

    private T object;
    private boolean flagMatched;

    {
        flagMatched = false;
    }

    public Matcher(T obj) {
        setObject(obj);
    }

    public void setObject(T obj) {
        this.object = obj;
    }

    public T getObject() {
        return object;
    }

    public boolean compare(T p1, T p2) {
        return p1 == p2;
    }

    private boolean ifDo(boolean x, Func f) {
        if (x) {
            f.call();
            return true;
        }
        return false;
    }

    public Matcher<T> match(T value, Func f) {
        flagMatched = ifDo(compare(value, object), f);
        return this;
    }

    public Matcher<T> elseDo(T value, Func f) {
        flagMatched = ifDo(!compare(value, object), f);
        return this;
    }

    public boolean isMatched() {
        return flagMatched;
    }

    public boolean finish() {
        return isMatched();
    }
}
