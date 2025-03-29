import static java.util.stream.Collectors.joining;

import java.util.stream.Stream;

interface HtmlDsl {

  sealed interface Tag {
    default String toHtml() {
      return switch (this) {
        case Html(var head, var body) ->
          "<html>" + head.toHtml() + body.toHtml() + "</html>";
        case Head(var title) ->
          "<head><title>" + title + "</title></head>";
        case Body(var content) ->
          "<body>" + Stream.of(content).map(Tag::toHtml).collect(joining()) + "</body>";
        case H1(var content) ->
          "<h1>" + content + "</h1>";
      };
    }
  }

  record H1(String content) implements Tag {}

  record Head(String title) implements Tag {}

  record Body(Tag... content) implements Tag {}

  record Html(Head head, Body body) implements Tag {}

  static void main() {
    var page = new Html(
        new Head("Example"),
        new Body(new H1("Hello World!")));

    System.console().println(page.toHtml());
  }
}
