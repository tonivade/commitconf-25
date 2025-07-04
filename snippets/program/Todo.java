package program;

import static java.util.stream.Collectors.joining;
import static program.Console.prompt;
import static program.Console.readLine;
import static program.Console.writeLine;
import static program.Program.zip;
import static program.Todo.State.COMPLETED;
import static program.Todo.State.NOT_COMPLETED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

sealed interface Todo<T> extends Program.Dsl<Todo.Repository, T> {

  interface Repository {
    void create(TodoEntity todo);
    void update(int id, UnaryOperator<TodoEntity> update);
    Optional<TodoEntity> find(int id);
    List<TodoEntity> findAll();
    void delete(int id);
    void deleteAll();
  }

  enum State {
    NOT_COMPLETED, COMPLETED
  }

  record TodoEntity(int id, String title, State state) {
    TodoEntity withState(State state) {
      return new TodoEntity(id, title, state);
    }
  }

  record Create(TodoEntity todo) implements Todo<Void> {}
  record Update(int id, UnaryOperator<TodoEntity> update) implements Todo<Void> {}
  record FindOne(int id) implements Todo<Optional<TodoEntity>> {}
  record FindAll() implements Todo<List<TodoEntity>> {}
  record DeleteOne(int id) implements Todo<Void> {}
  record DeleteAll() implements Todo<Void> {}

  @SuppressWarnings("unchecked")
  static <S extends Repository> Program<S, Void> create(TodoEntity todo) {
    return (Program<S, Void>) new Create(todo);
  }

  @SuppressWarnings("unchecked")
  static <S extends Repository> Program<S, Void> update(int id, UnaryOperator<TodoEntity> update) {
    return (Program<S, Void>) new Update(id, update);
  }

  @SuppressWarnings("unchecked")
  static <S extends Repository> Program<S, Optional<TodoEntity>> findOne(int id) {
    return (Program<S, Optional<TodoEntity>>) new FindOne(id);
  }

  @SuppressWarnings("unchecked")
  static <S extends Repository> Program<S, List<TodoEntity>> findAll() {
    return (Program<S, List<TodoEntity>>) new FindAll();
  }

  @SuppressWarnings("unchecked")
  static <S extends Repository> Program<S, Void> deleteOne(int id) {
    return (Program<S, Void>) new DeleteOne(id);
  }

  @SuppressWarnings("unchecked")
  static <S extends Repository> Program<S, Void> deleteAll() {
    return (Program<S, Void>) new DeleteAll();
  }

  @Override
  @SuppressWarnings("unchecked")
  default T handle(Repository repository) {
    return (T) switch (this) {
      case Create(TodoEntity todo) -> {
        repository.create(todo);
        yield null;
      }
      case Update(int id, UnaryOperator<TodoEntity> update) -> {
        repository.update(id, update);
        yield null;
      }
      case FindOne(int id) -> repository.find(id);
      case FindAll _ -> repository.findAll();
      case DeleteOne(int id) -> {
        repository.delete(id);
        yield null;
      }
      case DeleteAll _ -> {
        repository.deleteAll();
        yield null;
      }
    };
  }

  static Program<Context, Integer> printMenu() {
    return Console.<Context>writeLine("Menu")
      .andThen(writeLine("1. Create"))
      .andThen(writeLine("2. List"))
      .andThen(writeLine("3. Find"))
      .andThen(writeLine("4. Delete"))
      .andThen(writeLine("5. Clear"))
      .andThen(writeLine("6. Completed"))
      .andThen(writeLine("7. Exit"))
      .andThen(readLine())
      .map(Integer::parseInt);
  }

  static Program<Context, Void> executeAction(int action) {
    return switch (action) {
      case 1 -> createTodo();
      case 2 -> findAllTodos();
      case 3 -> findTodo();
      case 4 -> deleteTodo();
      case 5 -> deleteAllTodos();
      case 6 -> markCompleted();
      case 7 -> writeLine("Bye!");
      default -> throw new IllegalArgumentException();
    };
  }

  static Program<Context, String> promptTitle() {
    return prompt("Enter title");
  }

  static Program<Context, Integer> promptId() {
    return Console.<Context>prompt("Enter id").map(Integer::parseInt);
  }

  static Program<Context, Void> findAllTodos() {
    return Todo.<Context>findAll()
      .map(list -> list.stream().map(Object::toString).collect(joining("\n")))
      .andThen(Console::writeLine)
      .andThen(loop());
  }

  static Program<Context, Void> deleteAllTodos() {
    return Todo.<Context>deleteAll()
      .andThen(writeLine("all todo removed"))
      .andThen(loop());
  }

  static Program<Context, Void> createTodo() {
    return zip(promptId(), promptTitle(),
        (id, title) -> new TodoEntity(id, title, NOT_COMPLETED))
      .andThen(Todo::create)
      .andThen(writeLine("todo created"))
      .andThen(loop());
  }

  static Program<Context, Void> deleteTodo() {
    return promptId()
      .andThen(Todo::deleteOne)
      .andThen(writeLine("todo removed"))
      .andThen(loop());
  }

  static Program<Context, Void> findTodo() {
    return promptId()
      .andThen(Todo::findOne)
      .map(optional -> optional.map(Object::toString).orElse("not found"))
      .andThen(Console::writeLine)
      .andThen(loop());
  }

  static Program<Context, Void> markCompleted() {
    return promptId()
      .andThen(id -> update(id, entity -> entity.withState(COMPLETED)))
      .andThen(writeLine("todo completed"))
      .andThen(loop());
  }

  static Program<Context, Void> loop() {
    return printMenu().andThen(Todo::executeAction);
  }

  static void main() {
    var program = Console.<Context>writeLine("Welcome to TODO manager")
      .andThen(loop());

    program.eval(new Context());
  }

  final class Context implements Todo.Repository {

    private final Map<Integer, TodoEntity> repository = new HashMap<>();

    @Override
    public void create(TodoEntity todo) {
      repository.put(todo.id(), todo);
    }

    @Override
    public void update(int id, UnaryOperator<TodoEntity> update) {
      var todo = repository.get(id);
      repository.put(id, update.apply(todo));
    }

    @Override
    public Optional<TodoEntity> find(int id) {
      return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<TodoEntity> findAll() {
      return List.copyOf(repository.values());
    }

    @Override
    public void delete(int id) {
      repository.remove(id);
    }

    @Override
    public void deleteAll() {
      repository.clear();
    }
  }
}
