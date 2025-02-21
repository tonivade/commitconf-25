package program;

import java.util.concurrent.ThreadLocalRandom;

sealed interface Random<T> extends Program.Dsl<Void, T> {

  record NextInt(int bound) implements Random<Integer> {}

  @SuppressWarnings("unchecked")
  static <S> Program<S, Integer> nextInt(int bounds) {
    return (Program<S, Integer>) new NextInt(bounds);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(Void state) {
    return (T) switch (this) {
      case NextInt(var bound) -> (Integer) ThreadLocalRandom.current().nextInt(bound);
    };
  }
}
