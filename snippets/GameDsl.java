import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

sealed interface GameDsl<T> {

  record WriteLine(String line) implements GameDsl<Void> {}
  record ReadLine() implements GameDsl<String> {}

  record NextInt(int bound) implements GameDsl<Integer> {}

  record GetValue() implements GameDsl<Integer> {}
  record SetValue(int value) implements GameDsl<Void> {}

  record Done<T>(T value) implements GameDsl<T> {}
  record AndThen<T, R>(GameDsl<T> current, Function<T, GameDsl<R>> next) implements GameDsl<R> {
    private R safeEval(Context context) {
      return next.apply(current.eval(context)).eval(context);
    }
  }

  static GameDsl<Void> writeLine(String line) {
    return new WriteLine(line);
  }

  static GameDsl<String> readLine() {
    return new ReadLine();
  }

  static GameDsl<Integer> nextInt(int bound) {
    return new NextInt(bound);
  }

  static GameDsl<Void> setValue(int value) {
    return new SetValue(value);
  }

  static GameDsl<Integer> getValue() {
    return new GetValue();
  }

  static GameDsl<Void> randomNumber() {
    return nextInt(10).andThen(GameDsl::setValue);
  }

  static GameDsl<Boolean> checkNumber(int number) {
    return getValue().map(value -> value == number);
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

  @SuppressWarnings("unchecked")
  default T eval(Context context) {
    return (T) switch (this) {
      case WriteLine(var line) -> {
        System.console().println(line);
        yield null;
      }
      case ReadLine _ -> System.console().readLine();
      case NextInt(int bound) -> ThreadLocalRandom.current().nextInt(bound);
      case GetValue _ -> context.get();
      case SetValue(var value) -> {
        context.set(value);
        yield null;
      }
      case Done<T>(var value) -> value;
      case AndThen<?, T> andThen -> andThen.safeEval(context);
    };
  }

  static GameDsl<String> prompt(String question) {
    return writeLine(question).andThen(readLine());
  }

  static GameDsl<Void> loop() {
    return prompt("Enter a number between 0 and 9")
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
    var program = prompt("Do you want to play a game? (y/n)")
        .andThen(GameDsl::playOrExit);

    program.eval(new Context());
  }

  final class Context {

    private int value;

    void set(int value) {
      this.value = value;
    }

    int get() {
      return value;
    }
  }
}
