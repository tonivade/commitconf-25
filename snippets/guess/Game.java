package guess;

import java.util.Random;

sealed interface Game<T> extends Program<T> {

  ThreadLocal<State> STATE = ThreadLocal.withInitial(State::new);

  record RandomNumber() implements Game<Void> {}
  record CheckNumber(int number) implements Game<Boolean> {}

  static Program<Void> randomNumber() {
    return new RandomNumber();
  }

  static Program<Boolean> checkNumber(int number) {
    return new CheckNumber(number);
  }

  @SuppressWarnings("unchecked")
  @Override
  default T eval() {
    return (T) switch (this) {
      case RandomNumber _ -> {
        STATE.get().next();
        yield null;
      }
      case CheckNumber(var number) -> STATE.get().check(number);
    };
  }
}

final class State {

  private final Random random = new Random();

  private int value;

  void next() {
    this.value = random.nextInt(10);
  }

  boolean check(int number) {
    return number == value;
  }
}