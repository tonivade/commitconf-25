import static java.lang.System.console;

import java.util.function.Function;

/*
 * https://en.wikipedia.org/wiki/Monad_(functional_programming)
 */
sealed interface ConsoleDsl {

  record WriteLine(String line) implements ConsoleDsl {}
  record ReadLine() implements ConsoleDsl {}
  record AndThen(ConsoleDsl current, Function<String, ConsoleDsl> next) implements ConsoleDsl {};

  default ConsoleDsl andThen(ConsoleDsl next) {
    return andThen(_ -> next);
  }

  default ConsoleDsl andThen(Function<String, ConsoleDsl> next) {
    return new AndThen(this, next);
  }

  static ConsoleDsl writeLine(String line) {
    return new WriteLine(line);
  }

  static ConsoleDsl readLine() {
    return new ReadLine();
  }

  static ConsoleDsl prompt(String question) {
    return writeLine(question).andThen(readLine());
  }

  static ConsoleDsl sayHello(String name) {
    return writeLine("Hello " + name);
  }

  @SuppressWarnings("preview")
  default String eval() {
    return switch (this) {
      case WriteLine(var line) -> {
        console().println(line);
        yield null;
      }
      case ReadLine _ -> console().readLine();
      case AndThen(var current, var next) -> next.apply(current.eval()).eval();
    };
  }

  static void main() {
    var program = prompt("What's your name?").andThen(ConsoleDsl::sayHello);

    program.eval();
  }
}
