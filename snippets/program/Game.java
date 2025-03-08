package program;

import static program.Console.writeLine;

class Game {

  static Program<Context, Void> randomNumber() {
    return Random.<Context>nextInt(10).andThen(State::setValue);
  }

  static Program<Context, Boolean> checkNumber(int number) {
    return State.<Context>getValue().map(value -> value == number);
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

  static final class Context implements State.Service, Random.Service, Console.Service {

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
