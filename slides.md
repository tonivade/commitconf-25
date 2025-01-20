---
marp: true
title: Construyendo DSLs en Java
description: Aprovechando las nuevas herramientas del lenguaje
theme: gaia
footer: #commitconf 2025
author: Antonio Muñoz
transition: fade-out
backgroundColor: #eee
color: #000
---

# Construyendo DSLs en Java

Aprovechando las nuevas herramientas del lenguaje

<!-- _color: #fff -->
<!-- _footer: _Antonio Muñoz_ -->
<!-- _backgroundImage: url('https://images.unsplash.com/photo-1484417894907-623942c8ee29?q=80&w=1932&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D') -->

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
* Ejemplos: JOOQ, LINQ.
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

* Patrón Builder

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
