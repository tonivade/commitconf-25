package program;

import static program.Console.writeLine;

import java.util.concurrent.ThreadLocalRandom;

sealed interface Game<T> extends Program.Dsl<Game.State, T> {

  interface State {
    void set(int value);
    int get();
  }

  record RandomNumber() implements Game<Void> {}
  record CheckNumber(int number) implements Game<Boolean> {}

  @SuppressWarnings("unchecked")
  static <S extends State> Program<S, Void> randomNumber() {
    return (Program<S, Void>) new RandomNumber();
  }

  @SuppressWarnings("unchecked")
  static <S extends State> Program<S, Boolean> checkNumber(int number) {
    return (Program<S, Boolean>) new CheckNumber(number);
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(State state) {
    return (T) switch (this) {
      case RandomNumber _ -> {
        state.set(ThreadLocalRandom.current().nextInt(10));
        yield null;
      }
      case CheckNumber(var number) -> state.get() == number;
    };
  }

  static Program<Context, Void> play() {
    return Console.<Context>prompt("Enter a number between 0 and 9")
      .map(Integer::parseInt)
      .flatMap(Game::checkNumber)
      .flatMap(result -> {
        if (result) {
          return writeLine("YOU WIN!!");
        }
        return play();
      });
  }

  static void main() {
    var program = doYouWantToPlayAGame()
        .flatMap(answer -> {
          if (answer.equalsIgnoreCase("y")) {
            return Game.<Context>randomNumber().andThen(play());
          }
          return writeLine("Bye!");
        });

    program.eval(new Context());
  }

  static Program<Context, String> doYouWantToPlayAGame() {
    return Console.prompt("Do you want to play a game? (y/n)");
  }

  final class Context implements Game.State, Console.Service {

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
