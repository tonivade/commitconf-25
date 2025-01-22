package guess;

sealed interface Game<T> extends Program<T> {

  record RandomNumber() implements Game<Void> {}
  record CheckNumber(int number) implements Game<Boolean> {}

  static Game<Void> randomNumber() {
    return new RandomNumber();
  }

  static Game<Boolean> checkNumber(int number) {
    return new CheckNumber(number);
  }

  @SuppressWarnings("unchecked")
  @Override
  default T eval(State state) {
    return (T) switch (this) {
      case RandomNumber _ -> {
        state.next();
        yield null;
      }
      case CheckNumber(var number) -> state.check(number);
    };
  }
}
