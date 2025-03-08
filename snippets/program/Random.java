package program;

import java.util.concurrent.ThreadLocalRandom;

sealed interface Random<T> extends Program.Dsl<Random.Service, T> {

  interface Service {
    default Integer nextInt(int bound) {
      return ThreadLocalRandom.current().nextInt(bound);
    }
  }

  record NextInt(int bound) implements Random<Integer> {}

  @SuppressWarnings("unchecked")
  static <S> Program<S, Integer> nextInt(int bound) {
    return (Program<S, Integer>) new NextInt(bound);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(Service service) {
    return (T) switch (this) {
      case NextInt(var bound) -> service.nextInt(bound);
    };
  }
}
