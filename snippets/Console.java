import java.util.function.Function;

sealed interface Console {

  record WriteLine(String line) implements Console {}
  record ReadLine() implements Console {}
  record AndThen(Console current, Function<String, Console> next) implements Console {};

  default Console andThen(Console next) {
    return andThen(_ -> next);
  }

  default Console andThen(Function<String, Console> next) {
    return new AndThen(this, next);
  }

  static Console writeLine(String line) {
    return new WriteLine(line);
  }

  static Console readLine() {
    return new ReadLine();
  }

  static Console prompt(String question) {
    return writeLine(question).andThen(readLine());
  }

  static Console sayHello(String name) {
    return writeLine("Hello " + name);
  }

  @SuppressWarnings("preview")
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

  static void main(String... args) {
    var program = prompt("What's your name?").andThen(Console::sayHello);

    program.eval();
  }
}
