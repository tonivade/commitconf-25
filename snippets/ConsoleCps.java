import java.util.function.Function;

/*
 * https://en.wikipedia.org/wiki/Continuation-passing_style
 */
sealed interface ConsoleCps {

  record WriteLine(String line, ConsoleCps next) implements ConsoleCps {}
  record ReadLine(Function<String, ConsoleCps> next) implements ConsoleCps {}
  record End() implements ConsoleCps {}

  static ConsoleCps writeLine(String line, ConsoleCps next) {
    return new WriteLine(line, next);
  }

  static ConsoleCps writeLine(String line) {
    return writeLine(line, end());
  }

  static ConsoleCps readLine(Function<String, ConsoleCps> next) {
    return new ReadLine(next);
  }

  static ConsoleCps prompt(String question, Function<String, ConsoleCps> next) {
    return writeLine(question, readLine(next));
  }

  static ConsoleCps sayHello(String name) {
    return writeLine("Hello " + name);
  }

  static ConsoleCps end() {
    return new End();
  }

  default String eval() {
    return switch (this) {
      case WriteLine(var line, var next) -> {
        System.console().println(line);
        yield next.eval();
      }
      case ReadLine(var next) -> {
        var line = System.console().readLine();
        yield next.apply(line).eval();
      }
      case End _ -> null;
    };
  }

  static void main() {
    new WriteLine("What's your name?", 
      new ReadLine(name -> new WriteLine("Hello " + name + "!", 
        new End())));

    var program = prompt("What's your name?", ConsoleCps::sayHello);

    program.eval();
  }
}
