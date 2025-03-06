package program;

import static program.Console.writeLine;

sealed interface Game<T> extends Program.Dsl<Game.State, T> {

  interface State {
    void set(int value);
    int get();
  }

  record SetValue(int value) implements Game<Void> {}
  record GetValue() implements Game<Integer> {}

  @SuppressWarnings("unchecked")
  static <S extends State> Program<S, Void> setValue(int value) {
    return (Program<S, Void>) new SetValue(value);
  }

  @SuppressWarnings("unchecked")
  static <S extends State> Program<S, Integer> getValue() {
    return (Program<S, Integer>) new GetValue();
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(State state) {
    return (T) switch (this) {
      case SetValue(int value) -> {
        state.set(value);
        yield null;
      }
      case GetValue() -> state.get();
    };
  }

  static Program<Context, Void> randomNumber() {
    return Random.<Context>nextInt(10).andThen(Game::setValue);
  }

  static Program<Context, Boolean> checkNumber(int number) {
    return Game.<Context>getValue().map(value -> value == number);
  }

  static Program<Context, Void> play() {
    return Console.<Context>prompt("Enter a number between 0 and 9")
      .map(Integer::parseInt)
      .andThen(Game::checkNumber)
      .andThen(result -> {
        if (result) {
          return writeLine("YOU WIN!!");
        }
        return play();
      });
  }

  static void main() {
    var program = Console.<Context>prompt("Do you want to play a game? (y/n)")
        .andThen(answer -> {
          if (answer.equalsIgnoreCase("y")) {
            return randomNumber().andThen(play());
          }
          return writeLine("Bye!");
        });

    program.eval(new Context());
  }

  final class Context implements Game.State {

    private int value;

    @Override
    public void set(int value) {
      this.value = value;
    }

    @Override
    public int get() {
      return value;
    }
  }
}
