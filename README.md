# Construyendo DSLs en Java: Aprovechando las Nuevas Herramientas del Lenguaje

¿Te gustaría crear herramientas que hablen el mismo lenguaje que el problema que intentas resolver? En esta charla exploraremos el fascinante mundo de los **Domain-Specific Languages** (DSLs) en **Java**, una técnica que permite crear mini-lenguajes específicos para describir y resolver problemas de manera natural y expresiva.

Gracias a las potentes características que **Java** ha introducido recientemente, como *records*, *sealed classes* o *pattern matching*, desarrollar DSLs es ahora más accesible y eficaz que nunca. Hablaremos de cómo estas herramientas facilitan la creación de DSLs al hacer que el código sea más claro y menos propenso a errores.

Exploraremos paso a paso las novedades del lenguaje, y aplicaremos estas técnicas con ejemplos prácticos para construir un DSL que no solo sea expresivo, sino también funcional y eficiente. Verás cómo un DSL puede transformar la manera en que representamos problemas complejos y cómo, al final de la charla, podrás empezar a darle vida a tus propias herramientas para que hagan cosas realmente útiles.

## Compilar snippets

Para compilar los snippets necesitarás tener instalado [sdkman](https://sdkman.io/). Este comando instalará la versión de java necesaria para compilar los snippets

```sh
sdk env install
```

En lugar de gradle o maven uso [mill](https://mill-build.org/) como herramienta de building. Se trata de una alternativa mucho más ligera y sencilla. Este comando compilará y ejecutará el código fuente de los snippets.

```sh
./mill run
```