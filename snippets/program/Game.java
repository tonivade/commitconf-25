package program;

import static program.Console.prompt;
import static program.Console.writeLine;

import java.util.Random;

sealed interface Game<T> extends Program.Dsl<Game.State, T> {

  interface State {
    void next();
    boolean check(int number);
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
  default T eval(State state) {
    return (T) switch (this) {
      case RandomNumber _ -> {
        state.next();
        yield null;
      }
      case CheckNumber(var number) -> state.check(number);
    };
  }

  static Program<GameContext, Void> loop() {
    return Console.<GameContext>prompt("Enter a number")
      .map(Integer::parseInt)
      .andThen(Game::checkNumber)
      .andThen(Game::winOrContinue);
  }

  static Program<GameContext, Void> winOrContinue(boolean answer) {
    if (answer) {
      return writeLine("YOU WIN!!");
    }
    return loop();
  }

  static Program<GameContext, Void> playOrExit(String answer) {
    if (answer.equalsIgnoreCase("y")) {
      return Game.<GameContext>randomNumber().andThen(loop());
    }
    return writeLine("Bye!");
  }

  static Program<GameContext, String> whatsYourName() {
    return prompt("What's your name?");
  }

  static void main(String... args) {
    var program = whatsYourName()
        .andThen(Console::sayHello)
        .andThen(prompt("Do you want to play a game? (Y/y)"))
        .andThen(Game::playOrExit);

    program.eval(new GameContext());
  }

  final class GameContext implements Game.State, Console.DefaultService {

    private final Random random = new Random();

    private int value;

    @Override
    public void next() {
      this.value = random.nextInt(10);
    }

    @Override
    public boolean check(int number) {
      return number == value;
    }
  }
}
