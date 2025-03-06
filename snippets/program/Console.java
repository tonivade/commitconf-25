package program;

import static java.lang.System.console;

sealed interface Console<T> extends Program.Dsl<Void, T> {

  record WriteLine(String line) implements Console<Void> {}
  record ReadLine() implements Console<String> {}

  @SuppressWarnings("unchecked")
  static <S> Program<S, Void> writeLine(String line) {
    return (Program<S, Void>) new WriteLine(line);
  }

  @SuppressWarnings("unchecked")
  static <S> Program<S, String> readLine() {
    return (Program<S, String>) new ReadLine();
  }

  @SuppressWarnings("unchecked")
  static <S> Program<S, String> prompt(String question) {
    return (Program<S, String>) writeLine(question).andThen(readLine());
  }

  static <S> Program<S, Void> sayHello(String name) {
    return writeLine("Hello " + name + "!");
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(Void service) {
    return (T) switch (this) {
      case WriteLine(var line) -> {
        console().println(line);
        yield null;
      }
      case ReadLine _ -> console().readLine();
    };
  }

  static void main() {
    var program = prompt("What's your name?").andThen(Console::sayHello);

    program.eval(null);
  }
}
