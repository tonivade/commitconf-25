---
marp: true
title: Construyendo DSLs en Java
description: Aprovechando las nuevas herramientas del lenguaje
theme: custom
footer: me@tonivade.es
author: Antonio Muñoz
transition: fade-out
backgroundColor: #eee
backgroundImage: url('https://tonivade.es/commitconf25/images/background.png')
color: #000
---

# Construyendo DSLs en Java

Aprovechando las nuevas herramientas del lenguaje

<!-- _color: #fff -->
<!-- _footer: _Antonio Muñoz_ -->
<!-- _backgroundImage: url('https://tonivade.es/commitconf25/images/frontpage.jpg') -->

---

# ¿Quien soy?

* Programo en Java desde Java 1.1.
* Actualmente trabajo en https://clarity.ai como backender.
* Me encontraréis en:
    - Mastodon: https://jvm.social/@tonivade
    - Github: https://github.com/tonivade
    - Blog: https://blog.tonivade.es
    - Email: me@tonivade.es

---

# Empecemos por el principio

* Qué es un DSL?
* Domain Specific Language.
* Está especializado para un dominio específico.

---

# Ventajas

* Vocabulario reducido.
* Sintaxis concisa.
* Mayor expresividad.
* Mayor productividad.

---

# Tipos de DSLs

* Externos.
* Internos o embebidos.

---

# DSLs externos

* Están implementados con un interprete/compilador independiente.
* Ejemplos: Gherkin, SQL, HTML.
* Más complejos de implementar.
* Pero más potentes.

---

# Gherkin

```gherkin
Feature:  Login functionality of social networking site Mastodon. 
Given:  I am a mastodon user. 
When: I enter username as username. 
And I enter the password as the password 
Then I should be redirected to the home page of mastodon
```

---

# SQL

```sql
select 
  people.name, 
  people.age 
from people 
where people.age > 18 
order by people.age ASC
```

---

# HTML

```html
<html>
  <head>
    <title>Example</title>
  </head>
  <body>
    <h1>Hello World</h1>
  </body>
</html>
```

---

# DSLs internos

* Están implementados como parte del propio lenguaje donde se van a usar.
* Ejemplos: JOOQ, gradle, Assertj.
* Más sencillos de implementar.
* Están limitados a las capacidades del lenguaje anfitrión.

---

# De qué voy a hablar hoy?

* DSLs internos implementados en Java.

---

# Por qué Java?

* Soy javero.
* Novedades Java 21:
  * Records.
  * Pattern matching.
  * Sealed interfaces.
* Mi [charla del año pasado](https://www.youtube.com/watch?v=RbLkJXagQXw).

---

# Un DSL sencillo

* ResumeDSL

---

# Patrón builder

* Fluent API.
* Son aburridos de implementar.
* Librerías:
  * Lombok.
  * RecordBuilder.
  * Immutables.

--- 

# Smart constructors

* Los news son aburridos.
* Permiten ser más concisos.

---

# Un DSL mas dinámico

* Console
* Combinar operaciones

---

# Un programa muy sencillo

```bash

```

---

# Un programa muy sencillo

```bash {1}
# What's your name?


```

---

# Un programa muy sencillo

```bash {2}
# What's your name?
Toni

```

---

# Un programa muy sencillo

```bash {3}
# What's your name?
Toni
# Hello Toni!
```

---

# Un programa muy sencillo (II)

```java
sealed interface Console {


}
```

---

# Un programa muy sencillo (II)

```java {2}
sealed interface Console {
  record WriteLine(String line) implements Console {}

}
```

---

# Un programa muy sencillo (II)

```java {3}
sealed interface Console {
  record WriteLine(String line) implements Console {}
  record ReadLine() implements Console {}
}
```

---

# Un programa muy sencillo (III)

```java
sealed interface Console {
  static void main() {



  }
}
```

---

# Un programa muy sencillo (III)

```java {3}
sealed interface Console {
  static void main() {
    // WriteLine("What's your name?")


  }
}
```

---

# Un programa muy sencillo (III)

```java {4}
sealed interface Console {
  static void main() {
    // WriteLine("What's your name?")
    // ReadLine() -> name

  }
}
```

---

# Un programa muy sencillo (III)

```java {5}
sealed interface Console {
  static void main() {
    // WriteLine("What's your name?")
    // ReadLine() -> name
    // WriteLine("Hello $name!")
  }
}
```

---

# Primer Intento

Usando CPS. Continuation Passing Style.

```java
sealed interface Console {
  record WriteLine(String line) implements Console {}
  record ReadLine() implements Console {}
}
```

<!--
Continuation Passing Style es solución muy utilizada, y en muchos compiladores se usa como representación interna.
 -->

---

# Primer Intento

Usando CPS. Continuation Passing Style.

```java {2}
sealed interface ConsoleCps {
  record WriteLine(String line, ConsoleCps next) implements ConsoleCps {}
  record ReadLine() implements Console {}
}
```

---

# Primer Intento

Usando CPS. Continuation Passing Style.

```java {3}
sealed interface ConsoleCps {
  record WriteLine(String line, ConsoleCps next) implements ConsoleCps {}
  record ReadLine(Function<String, ConsoleCps> next) implements ConsoleCps {}
}
```

---

# Primer Intento (II)

```java {3}
sealed interface ConsoleCps {
  static void main() {
    new WriteLine("What's your name?");
  }
}
```

---

# Primer Intento (II)

```java {4}
sealed interface ConsoleCps {
  static void main() {
    new WriteLine("What's your name?", 
      new ReadLine());
  }
}
```

---

# Primer Intento (II)

```java {5}
sealed interface ConsoleCps {
  static void main() {
    new WriteLine("What's your name?", 
      new ReadLine(
        name -> new WriteLine("Hello " + name + "!")));
  }
}
```

---

# Primer Intento (II)

```java {5}
sealed interface ConsoleCps {
  static void main() {
    new WriteLine("What's your name?", 
      new ReadLine(
        name -> new WriteLine("Hello " + name + "!", ???)));
  }
}
```

---

# Primer Intento (II)

```java {8}
sealed interface ConsoleCps {
  static void main() {
    new WriteLine("What's your name?", 
      new ReadLine(
        name -> new WriteLine("Hello " + name + "!", 
          new End())));
  }
}
```

---

# Primer Intento (II)

```java {8}
sealed interface ConsoleCps {
  static void main() {
    var program = new WriteLine("What's your name?", 
      new ReadLine(
        name -> new WriteLine("Hello " + name + "!", 
          new End())));

    program.eval();
  }
}
```

---

# Primer Intento (III)

```java {4}
sealed interface ConsoleCps {
  record WriteLine(String line, ConsoleCps next) implements ConsoleCps {}
  record ReadLine(Function<String, ConsoleCps> next) implements ConsoleCps {}
  record End() implements ConsoleCps {}
}
```

---

# Primer Intento (IV)

```java {4}
default String eval() {
  return switch (this) {
  };
}
```

---

# Primer Intento (IV)

```java {3-6}
default String eval() {
  return switch (this) {
    case WriteLine(var line, var next) -> {
      console().println(line);
      yield next.eval();
    }
  };
}
```

---

# Primer Intento (IV)

```java {7-10}
default String eval() {
  return switch (this) {
    case WriteLine(var line, var next) -> {
      console().println(line);
      yield next.eval();
    }
    case ReadLine(var next) -> {
      var line = console().readLine();
      yield next.apply(line).eval();
    }
  };
}
```

---

# Primer Intento (IV)

```java {11}
default String eval() {
  return switch (this) {
    case WriteLine(var line, var next) -> {
      console().println(line);
      yield next.eval();
    }
    case ReadLine(var next) -> {
      var line = console().readLine();
      yield next.apply(line).eval();
    }
    case End _ -> null;
  };
}
```

---

# Otro Intento

Usando un estilo monádico.

```java
sealed interface ConsoleDsl {
  record WriteLine(String line) implements ConsoleDsl {}
  record ReadLine() implements ConsoleDsl {}
}
```

---

# Otro Intento

Usando un estilo monádico.

```java {4}
sealed interface ConsoleDsl {
  record WriteLine(String line) implements ConsoleDsl {}
  record ReadLine() implements ConsoleDsl {}
  record AndThen() implements ConsoleDsl {}
}
```

---

# Otro Intento

Usando un estilo monádico.

```java {5}
sealed interface ConsoleDsl {
  record WriteLine(String line) implements ConsoleDsl {}
  record ReadLine() implements ConsoleDsl {}
  record AndThen(
    ConsoleDsl current) 
      implements ConsoleDsl {}
}
```

---

# Otro Intento

Usando un estilo monádico.

```java {6}
sealed interface ConsoleDsl {
  record WriteLine(String line) implements ConsoleDsl {}
  record ReadLine() implements ConsoleDsl {}
  record AndThen(
    ConsoleDsl current,
    Function<String, ConsoleDsl> next) 
      implements ConsoleDsl {}
}
```

---

# Otro Intento (II)

```java {3}
sealed interface ConsoleDsl {
  static void main() {
    new WriteLine("What's your name?");
  }
}
```

---

# Otro Intento (II)

```java {4}
sealed interface ConsoleDsl {
  static void main() {
    new AndThen(
      new WriteLine("What's your name?"), _ -> new ReadLine());
  }
}
```

---

# Otro Intento (II)

```java {3,7}
sealed interface ConsoleDsl {
  static void main() {
    new AndThen(
      new AndThen(
        new WriteLine("What's your name?"), 
        _ -> new ReadLine()), 
      name -> new WriteLine("Hello " + name + "!"));
  }
}
```

---

# Otro Intento (II)

```java {7}
sealed interface ConsoleDsl {
  static void main() {
    var program = new AndThen(
      new AndThen(
        new WriteLine("What's your name?"), 
        _ -> new ReadLine()), 
      name -> new WriteLine("Hello " + name + "!"));

    program.eval();
  }
}
```

---

# Otro Intento (III)

```java {3}
sealed interface ConsoleDsl {
  default ConsoleDsl andThen(Function<String, ConsoleDsl> next) {
    return new AndThen(this, next);
  }
}
```

---

# Otro Intento (III)

```java {3-5}
sealed interface ConsoleDsl {
  static void main() {
    var program = new WriteLine("What's your name?")
      .andThen(_ -> new ReadLine()
        .andThen(name -> new WriteLine("Hello " + name + "!")));

    program.eval();
  }
}
```

---

# Otro Intento (IV)

```java
sealed interface ConsoleDsl {
  default String eval() {
    return switch (this) {
    };
  }
}
```

---

# Otro Intento (IV)

```java {4-7}
sealed interface ConsoleDsl {
  default String eval() {
    return switch (this) {
      case WriteLine(var line) -> {
        console().println(line);
        yield null;
      }
    };
  }
}
```

---

# Otro Intento (IV)

```java {8}
sealed interface ConsoleDsl {
  default String eval() {
    return switch (this) {
      case WriteLine(var line) -> {
        console().println(line);
        yield null;
      }
      case ReadLine _ -> console().readLine();
    };
  }
}
```

---

# Otro Intento (IV)

```java {9-10}
sealed interface ConsoleDsl {
  default String eval() {
    return switch (this) {
      case WriteLine(var line) -> {
        console().println(line);
        yield null;
      }
      case ReadLine _ -> console().readLine();
      case AndThen(var current, var next) 
        -> next.apply(current.eval()).eval();
    };
  }
}
```

---

# Otro Intento (V)

```java
sealed interface ConsoleDsl {
  static ConsoleDsl prompt(String question) {
    return new WriteLine(question).andThen(new ReadLine());
  }
}
```

---

# Otro Intento (V)

```java {3-4}
sealed interface ConsoleDsl {
  static void main() {
    var program = new WriteLine("What's your name?")
      .andThen(_ -> new ReadLine()
        .andThen(name -> new WriteLine("Hello " + name + "!")));

    program.eval();
  }
}
```

---

# Otro Intento (V)

```java {3}
sealed interface ConsoleDsl {
  static void main() {
    var program = prompt("What's your name?")
      .andThen(name -> new WriteLine("Hello " + name + "!"));

    program.eval();
  }
}
```

---

# Otro Intento (V)

```java
sealed interface ConsoleDsl {
  static ConsoleDsl sayHello(String name) {
    return writeLine("Hello " + name + "!");
  }
}
```

---

# Otro Intento (V)

```java {4}
sealed interface ConsoleDsl {
  static void main() {
    var program = prompt("What's your name?")
      .andThen(name -> new WriteLine("Hello " + name + "!"));

    program.eval();
  }
}
```

---

# Otro Intento (V)

```java {4}
sealed interface ConsoleDsl {
  static void main() {
    var program = prompt("What's your name?")
      .andThen(ConsoleDsl::sayHello);

    program.eval();
  }
}
```

---

# Un DSL mas divertido

* Game
* Añadir estado

---

# Qué falta?

* Extension methods
* Closures

---

# ¿Preguntas? :thinking:

<!-- _class: lead -->

---

# ¡Gracias! :sparkling_heart:

<!-- _class: lead -->

---

# Documentación Oficial


---

# Artículos / Videos


---

# Enlaces


<!-- TODO:

  - everything

    -->
