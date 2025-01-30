---
marp: true
title: Construyendo DSLs en Java
description: Aprovechando las nuevas herramientas del lenguaje
theme: gaia
footer: :copyright: Antonio Muñoz
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
    - Mastodon: @tonivade@jvm.social
    - Github: https://github.com/tonivade
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

* Están implementados como parte del propio lenguage donde se van a usar.
* Ejemplos: JOOQ, gradle, Assertj.
* Más sencillos de implementar.
* Están limitados a las capacidades del lenguage anfitrión.

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
