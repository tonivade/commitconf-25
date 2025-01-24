package program;

sealed interface Game<S extends Game.State, T> extends Program.Dsl<S, T> {

  interface State {
    void next();
    boolean check(int number);
  }

  record RandomNumber<S extends State>() implements Game<S, Void> {}
  record CheckNumber<S extends State>(int number) implements Game<S, Boolean> {}

  static <S extends State> Game<S, Void> randomNumber() {
    return new RandomNumber<>();
  }

  static <S extends State> Game<S, Boolean> checkNumber(int number) {
    return new CheckNumber<>(number);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T eval(S state) {
    return (T) switch (this) {
      case RandomNumber<?> _ -> {
        state.next();
        yield null;
      }
      case CheckNumber<?>(var number) -> state.check(number);
    };
  }
}
