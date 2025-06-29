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

  default String eval() {
    return switch (this) {
      case WriteLine(var line) -> {
        System.console().println(line);
        yield null;
      }
      case ReadLine _ -> System.console().readLine();
      case AndThen(var current, var next) -> next.apply(current.eval()).eval();
    };
  }

  static void main() {
    new AndThen(
      new WriteLine("What's your name?"), _ -> 
        new AndThen(new ReadLine(), 
          name -> new WriteLine("Hello " + name + "!")));

    new WriteLine("What's your name?")
      .andThen(_ -> new ReadLine())
      .andThen(name -> new WriteLine("Hello " + name + "!"));

    var program = prompt("What's your name?").andThen(ConsoleDsl::sayHello);

    program.eval();
  }
}
