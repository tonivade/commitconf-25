import java.util.Random;
import java.util.function.Function;

sealed interface GameDsl<T> {

  record WriteLine(String line) implements GameDsl<Void> {}
  record ReadLine() implements GameDsl<String> {}

  record RandomNumber() implements GameDsl<Void> {}
  record CheckNumber(int number) implements GameDsl<Boolean> {}

  record Done<T>(T value) implements GameDsl<T> {}
  record AndThen<T, R>(GameDsl<T> current, Function<T, GameDsl<R>> next) implements GameDsl<R> {
    public R safeEval(State state) {
      return next.apply(current.eval(state)).eval(state);
    }
  };

  static GameDsl<Void> writeLine(String line) {
    return new WriteLine(line);
  }

  static GameDsl<String> readLine() {
    return new ReadLine();
  }

  static GameDsl<Void> randomNumber() {
    return new RandomNumber();
  }

  static GameDsl<Boolean> checkNumber(int number) {
    return new CheckNumber(number);
  }

  static <T> GameDsl<T> done(T value) {
    return new Done<T>(value);
  }

  default <R> GameDsl<R> map(Function<T, R> mapper) {
    return andThen(mapper.andThen(Done::new));
  }

  default <R> GameDsl<R> andThen(GameDsl<R> next) {
    return andThen(_ -> next);
  }

  default <R> GameDsl<R> andThen(Function<T, GameDsl<R>> next) {
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
      case AndThen<?, T> andThen -> andThen.safeEval(state);
    };
  }

  static GameDsl<String> prompt(String question) {
    return writeLine(question).andThen(readLine());
  }

  static GameDsl<Void> sayHello(String name) {
    return writeLine("Hello " + name);
  }

  static GameDsl<Void> loop() {
    return prompt("Enter a number")
      .map(Integer::parseInt)
      .andThen(GameDsl::checkNumber)
      .andThen(GameDsl::winOrContinue);
  }

  static GameDsl<Void> winOrContinue(boolean answer) {
    if (answer) {
      return writeLine("YOU WIN!!");
    }
    return loop();
  }

  static GameDsl<Void> playOrExit(String answer) {
    if (answer.equalsIgnoreCase("y")) {
      return randomNumber().andThen(loop());
    }
    return writeLine("Bye!");
  }

  static void main(String... args) {
    var program = prompt("What's your name?")
        .andThen(GameDsl::sayHello)
        .andThen(prompt("Do you want to play a game? (Y/y)"))
        .andThen(GameDsl::playOrExit);

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
