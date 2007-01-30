package coconut.concurrent;

/**
 * Function currying
 * 
 * Original idea by Sean Rhea.
 * 
 */
public class Currying {

    public interface Cur0 {
        void call ();
    }

    public interface Cur1<A1> {
        void call (A1 a1);
    }

    public interface Cur2<A1, A2> {
        void call (A1 a1, A2 a2);
    }

    public interface Cur3<A1, A2, A3> {
        void call (A1 a1, A2 a2, A3 a3);
    }

    public interface Cur4<A1, A2, A3, A4> {
        void call (A1 a1, A2 a2, A3 a3, A4 a4);
    }

    public static <A1> Cur0 wrap (
            final Cur1<A1> in_f,
            final A1 in_a1)
    {
        return new Cur0 () {
            private Cur1<A1> f = in_f;
            private A1 a1 = in_a1;
            public void call () {
                f.call (a1);
            }
        };
    }

    public static <A1, A2> Cur0 wrap (
            final Cur2<A1, A2> in_f,
            final A1 in_a1,
            final A2 in_a2)
    {
        return new Cur0 () {
            private Cur2<A1, A2> f = in_f;
            private A1 a1 = in_a1;
            private A2 a2 = in_a2;
            public void call () {
                f.call (a1, a2);
            }
        };
    }

    public static <A1, A2> Cur1<A2> wrap (
            final Cur2<A1, A2> in_f,
            final A1 in_a1)
    {
        return new Cur1<A2> () {
            private Cur2<A1, A2> f = in_f;
            private A1 a1 = in_a1;
            public void call (A2 a2) {
                f.call (a1, a2);
            }
        };
    }

    public static <A1, A2, A3> Cur0 wrap (
            final Cur3<A1, A2, A3> in_f,
            final A1 in_a1,
            final A2 in_a2,
            final A3 in_a3)
    {
        return new Cur0 () {
            private Cur3<A1, A2, A3> f = in_f;
            private A1 a1 = in_a1;
            private A2 a2 = in_a2;
            private A3 a3 = in_a3;
            public void call () {
                f.call (a1, a2, a3);
            }
        };
    }

    public static <A1, A2, A3> Cur1<A3> wrap (
            final Cur3<A1, A2, A3> in_f,
            final A1 in_a1,
            final A2 in_a2)
    {
        return new Cur1<A3> () {
            private Cur3<A1, A2, A3> f = in_f;
            private A1 a1 = in_a1;
            private A2 a2 = in_a2;
            public void call (A3 a3) {
                f.call (a1, a2, a3);
            }
        };
    }

    public static <A1, A2, A3> Cur2<A2, A3> wrap (
            final Cur3<A1, A2, A3> in_f,
            final A1 in_a1)
    {
        return new Cur2<A2, A3> () {
            private Cur3<A1, A2, A3> f = in_f;
            private A1 a1 = in_a1;
            public void call (A2 a2, A3 a3) {
                f.call (a1, a2, a3);
            }
        };
    }

    public static <A1, A2, A3, A4> Cur0 wrap (
            final Cur4<A1, A2, A3, A4> in_f,
            final A1 in_a1,
            final A2 in_a2,
            final A3 in_a3,
            final A4 in_a4)
    {
        return new Cur0 () {
            private Cur4<A1, A2, A3, A4> f = in_f;
            private A1 a1 = in_a1;
            private A2 a2 = in_a2;
            private A3 a3 = in_a3;
            private A4 a4 = in_a4;
            public void call () {
                f.call (a1, a2, a3, a4);
            }
        };
    }

    public static <A1, A2, A3, A4> Cur1<A4> wrap (
            final Cur4<A1, A2, A3, A4> in_f,
            final A1 in_a1,
            final A2 in_a2,
            final A3 in_a3)
    {
        return new Cur1<A4> () {
            private Cur4<A1, A2, A3, A4> f = in_f;
            private A1 a1 = in_a1;
            private A2 a2 = in_a2;
            private A3 a3 = in_a3;
            public void call (A4 a4) {
                f.call (a1, a2, a3, a4);
            }
        };
    }

    public static <A1, A2, A3, A4> Cur2<A3, A4> wrap (
            final Cur4<A1, A2, A3, A4> in_f,
            final A1 in_a1,
            final A2 in_a2)
    {
        return new Cur2<A3, A4> () {
            private Cur4<A1, A2, A3, A4> f = in_f;
            private A1 a1 = in_a1;
            private A2 a2 = in_a2;
            public void call (A3 a3, A4 a4) {
                f.call (a1, a2, a3, a4);
            }
        };
    }

    public static <A1, A2, A3, A4> Cur3<A2, A3, A4> wrap (
            final Cur4<A1, A2, A3, A4> in_f,
            final A1 in_a1)
    {
        return new Cur3<A2, A3, A4> () {
            private Cur4<A1, A2, A3, A4> f = in_f;
            private A1 a1 = in_a1;
            public void call (A2 a2, A3 a3, A4 a4) {
                f.call (a1, a2, a3, a4);
            }
        };
    }
}
