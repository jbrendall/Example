package lc;

import org.json.JSONArray;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class Util {
    static Stream<Object> arrayToStream(JSONArray array) {
        return StreamSupport.stream(array.spliterator(), false);
    }

    static class Tuple<T1, T2> {
        final T1 _1;
        final T2 _2;

        Tuple(T1 t1, T2 t2) {
            this._1 = t1;
            this._2 = t2;
        }
    }

    static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
