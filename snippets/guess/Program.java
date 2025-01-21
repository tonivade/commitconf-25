package guess;

import static guess.Console.readLine;
import static guess.Console.writeLine;
import static guess.Game.checkNumber;
import static guess.Game.randomNumber;

import java.util.function.Function;

public sealed interface Program<T> permits Console, Game, Program.AndThen {

  record AndThen<T, R>(Program<T> current, Function<T, Program<R>> next) implements Program<R> {
    @Override
    public R eval() {
      return next.apply(current.eval()).eval();
    }
  };

  default <R> Program<R> andThen(Program<R> next) {
    return andThen(_ -> next);
  }

  default <R> Program<R> andThen(Function<T, Program<R>> next) {
    return new AndThen<>(this, next);
  }

  default T eval() {
    return switch (this) {
      case Console<T> console -> console.eval();
      case Game<T> guess -> guess.eval();
      case AndThen<?, T> andThen -> andThen.eval();
    };
  }

  static Program<String> prompt(String question) {
    return writeLine(question).andThen(readLine());
  }

  static Program<Void> sayHello(String name) {
    return writeLine("Hello " + name);
  }

  static Program<Void> loop() {
    return prompt("Enter a number")
      .andThen(number -> checkNumber(Integer.parseInt(number)))
      .andThen(Program::winOrContinue);
  }

  static Program<Void> winOrContinue(boolean answer) {
    if (answer) {
      return writeLine("YOU WIN!!");
    }
    return loop();
  }

  public static Program<Void> playOrExit(String answer) {
    if (answer.equalsIgnoreCase("y")) {
      return randomNumber().andThen(loop());
    }
    return writeLine("Bye!");
  }

  static void main(String... args) {
    var program = prompt("What's your name?")
        .andThen(Program::sayHello)
        .andThen(prompt("Do you want to play a game? (Y/y)"))
        .andThen(Program::playOrExit);

    program.eval();
  }
}
