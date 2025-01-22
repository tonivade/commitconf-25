package guess;

import java.util.Random;

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