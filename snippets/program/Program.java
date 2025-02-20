package program;

import java.util.function.BiFunction;
import java.util.function.Function;

sealed interface Program<S, T> {

  record Done<S, T>(T value) implements Program<S, T> {}

  record FlatMap<S, T, R>(
      Program<S, T> current, 
      Function<T, Program<S, R>> next) implements Program<S, R> {
    public R safeEval(S state) {
      return next.apply(current.eval(state)).eval(state);
    }
  }

  non-sealed interface Dsl<S, T> extends Program<S, T> {
     T handle(S state);
  }

  default T eval(S state) {
    return switch (this) {
      case Done<S, T>(T value) -> value;
      case FlatMap<S, ?, T> flatMap -> flatMap.safeEval(state);
      case Dsl<S, T> dsl -> dsl.handle(state);
    };
  }

  static <S, T> Program<S, T> done(T value) {
    return new Done<>(value);
  }

  static <S, T, U, R> Program<S, R> zip(Program<S, T> pt, Program<S, U> pu, BiFunction<T, U, R> mapper) {
    return pt.flatMap(t -> pu.map(u -> mapper.apply(t, u)));
  }

  default <R> Program<S, R> map(Function<T, R> mapper) {
    return flatMap(mapper.andThen(Program::done));
  }

  default <R> Program<S, R> andThen(Program<S, R> next) {
    return flatMap(_ -> next);
  }

  default <R> Program<S, R> flatMap(Function<T, Program<S, R>> next) {
    return new FlatMap<>(this, next);
  }
}
