import static java.lang.System.console;
import static java.util.Map.entry;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

sealed interface JsonDsl {

  enum JsonNull implements JsonDsl {
    NULL
  }

  enum JsonBoolean implements JsonDsl {
    TRUE, FALSE
  }

  record JsonString(String value) implements JsonDsl {}

  record JsonNumber(Number value) implements JsonDsl {}

  record JsonObject(Map<String, JsonDsl> value) implements JsonDsl {}

  record JsonArray(List<JsonDsl> value) implements JsonDsl {}

  default String asString() {
    return switch (this) {
      case JsonNull _ -> "null";
      case JsonBoolean b -> switch (b) {
        case TRUE -> "true";
        case FALSE -> "false";
      };
      case JsonString(var value) -> "\"" + value + "\"";
      case JsonNumber(var value) -> String.valueOf(value);
      case JsonObject(var map) -> map.entrySet().stream()
          .map(entry -> "\"" + entry.getKey() + "\":" + entry.getValue().asString())
          .collect(joining(",", "{", "}"));
      case JsonArray(var array) -> array.stream()
          .map(JsonDsl::asString)
          .collect(joining(",", "[", "]"));
    };
  }

  static JsonDsl string(String value) {
    return new JsonString(value);
  }

  static JsonDsl number(Number value) {
    return new JsonNumber(value);
  }

  static JsonDsl array(JsonDsl... elements) {
    return new JsonArray(List.of(elements));
  }

  @SafeVarargs
  static JsonDsl object(Map.Entry<String, JsonDsl>... entries) {
    return new JsonObject(Stream.of(entries)
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
  }

  @SuppressWarnings("preview")
  static void main() {
    var json = array(
        object(
          entry("name", string("Toni")), 
          entry("age", number(46)), 
          entry("old", JsonBoolean.TRUE)),
        object(
          entry("name", string("Baby")), 
          entry("age", JsonNull.NULL), 
          entry("old", JsonBoolean.FALSE)));

    console().println(json.asString());
  }
}