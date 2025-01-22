import java.util.Random;
import java.util.function.Function;

sealed interface Game<T> {

  record WriteLine(String line) implements Game<Void> {}
  record ReadLine() implements Game<String> {}

  record RandomNumber() implements Game<Void> {}
  record CheckNumber(int number) implements Game<Boolean> {}

  record Done<T>(T value) implements Game<T> {}
  record AndThen<T, R>(Game<T> current, Function<T, Game<R>> next) implements Game<R> {
    @Override
    public R eval(State state) {
      return next.apply(current.eval(state)).eval(state);
    }
  };

  static Game<Void> writeLine(String line) {
    return new WriteLine(line);
  }

  static Game<String> readLine() {
    return new ReadLine();
  }

  static Game<Void> randomNumber() {
    return new RandomNumber();
  }

  static Game<Boolean> checkNumber(int number) {
    return new CheckNumber(number);
  }

  static <T> Game<T> done(T value) {
    return new Done<T>(value);
  }

  default <R> Game<R> map(Function<T, R> mapper) {
    return andThen(mapper.andThen(Done::new));
  }

  default <R> Game<R> andThen(Game<R> next) {
    return andThen(_ -> next);
  }

  default <R> Game<R> andThen(Function<T, Game<R>> next) {
    return new AndThen<>(this, next);
  }

  @SuppressWarnings({ "preview", "unchecked" })
  default T eval(State state) {
    return (T) switch (this) {
      case WriteLine(var line) -> {
        System.console().println(line);
        yield null;
      }
      case ReadLine _ -> System.console().readLine();
      case RandomNumber _ -> {
        state.next();
        yield null;
      }
      case CheckNumber(var number) -> state.check(number);
      case Done<T>(var value) -> value;
      case AndThen<?, T> andThen -> andThen.eval(state);
    };
  }

  static Game<String> prompt(String question) {
    return writeLine(question).andThen(readLine());
  }

  static Game<Void> sayHello(String name) {
    return writeLine("Hello " + name);
  }

  static Game<Void> loop() {
    return prompt("Enter a number")
      .map(Integer::parseInt)
      .andThen(Game::checkNumber)
      .andThen(Game::winOrContinue);
  }

  static Game<Void> winOrContinue(boolean answer) {
    if (answer) {
      return writeLine("YOU WIN!!");
    }
    return loop();
  }

  static Game<Void> playOrExit(String answer) {
    if (answer.equalsIgnoreCase("y")) {
      return randomNumber().andThen(loop());
    }
    return writeLine("Bye!");
  }

  static void main(String... args) {
    var program = prompt("What's your name?")
        .andThen(Game::sayHello)
        .andThen(prompt("Do you want to play a game? (Y/y)"))
        .andThen(Game::playOrExit);

    program.eval(new State());
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
}
