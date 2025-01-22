package guess;

sealed interface Game<T> extends Program<T> {

  record RandomNumber() implements Game<Void> {}
  record CheckNumber(int number) implements Game<Boolean> {}

  static Program<Void> randomNumber() {
    return new RandomNumber();
  }

  static Program<Boolean> checkNumber(int number) {
    return new CheckNumber(number);
  }

  @Override
  @SuppressWarnings("unchecked")
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
