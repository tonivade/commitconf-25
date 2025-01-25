package program;

import static program.Console.prompt;
import static program.Console.writeLine;

import java.util.Random;

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

  static Program<Context, Void> loop() {
    return Console.<Context>prompt("Enter a number")
      .map(Integer::parseInt)
      .andThen(Game::checkNumber)
      .andThen(Game::winOrContinue);
  }

  static Program<Context, Void> winOrContinue(boolean answer) {
    if (answer) {
      return writeLine("YOU WIN!!");
    }
    return loop();
  }

  static Program<Context, Void> playOrExit(String answer) {
    if (answer.equalsIgnoreCase("y")) {
      return Game.<Context>randomNumber().andThen(loop());
    }
    return writeLine("Bye!");
  }

  static Program<Context, String> whatsYourName() {
    return prompt("What's your name?");
  }

  static void main(String... args) {
    var program = whatsYourName()
        .andThen(Console::sayHello)
        .andThen(prompt("Do you want to play a game? (Y/y)"))
        .andThen(Game::playOrExit);

    program.eval(new Context());
  }

  final class Context implements Game.State, Console.Service {

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

    @SuppressWarnings("preview")
    @Override
    public void writeLine(String line) {
      System.console().println(line);
    }

    @Override
    public String readLine() {
      return System.console().readLine();
    }
  }
}
