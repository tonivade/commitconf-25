package program;

import static program.Console.prompt;
import static program.Console.writeLine;

import java.util.Random;
import java.util.function.Function;

sealed interface Program<S, T> {

  record Done<S, T>(T value) implements Program<S, T> {
    @Override
    public T eval(S state) {
      return value;
    }
  }
  record AndThen<S, T, R>(Program<S, T> current, Function<T, Program<S, R>> next) implements Program<S, R> {
    @Override
    public R eval(S state) {
      return next.apply(current.eval(state)).eval(state);
    }
  };

  non-sealed interface Dsl<S, T> extends Program<S, T> {}

  T eval(S state);

  static <S, T> Program<S, T> done(T value) {
    return new Done<>(value);
  }

  default <R> Program<S, R> map(Function<T, R> mapper) {
    return andThen(mapper.andThen(Program::done));
  }

  default <R> Program<S, R> andThen(Program<S, R> next) {
    return andThen(_ -> next);
  }

  default <R> Program<S, R> andThen(Function<T, Program<S, R>> next) {
    return new AndThen<>(this, next);
  }

  static Program<Context, Void> loop() {
    return Console.<Context>prompt("Enter a number")
      .map(Integer::parseInt)
      .andThen(Game::checkNumber)
      .andThen(Program::winOrContinue);
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

  static void main(String... args) {
    var program = Console.<Context>prompt("What's your name?")
        .andThen(Console::sayHello)
        .andThen(prompt("Do you want to play a game? (Y/y)"))
        .andThen(Program::playOrExit);

    program.eval(new Context());
  }
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

  @Override
  @SuppressWarnings("preview")
  public void writeLine(String line) {
    System.console().println(line);
  }

  @Override
  public String readLine() {
    return System.console().readLine();
  }
}
