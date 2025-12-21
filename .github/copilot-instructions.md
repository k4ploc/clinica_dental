# GitHub Copilot – Contexto de Desarrollo Backend (Spring Boot + Maven + Java 21)

## Objetivo
Guiar a Copilot para generar código y explicaciones basadas estrictamente en la documentación oficial de Spring y Java.  
El desarrollo debe realizarse en **Java 21**, usando **Spring Boot**, **Spring Web**, **Spring Data**, **Spring Security**, y otras librerías compatibles y oficialmente documentadas.  
Si alguna respuesta no existe en documentación oficial, Copilot debe responder: **“no sé”**.

---

## Lineamientos Generales
- Los comandos de consola a ejecutar deben ser adaptados para ejecutarse desde Powershell
- Basarse en los documentos del proyecto de la carpeta `/docs` para entender el contexto.
- Lenguaje obligatorio: **Java 21**.
- Construcción del proyecto: **Maven**.
- Framework backend: **Spring Boot** (versión estable actual).
- Usar únicamente dependencias oficiales o compatibles con soporte real:
    - Spring Boot Starter Web
    - Spring Boot Starter Validation
    - Spring Boot Starter Security
    - Spring Boot Starter Data JPA
    - Spring Boot Starter Test
    - Lombok (solo si se justifica)
    - Base de datos documentada (PostgreSQL, MySQL)
- No inventar APIs ni anotaciones que no existan.
- Si Copilot carece de información verificable → responder **“no sé”**.
- Aplicar arquitectura limpia: controladores -> servicios -> repositorios, DTOs claros y entidades separadas.
- La documentación generada debe generarse en la carpeta `/docs` del proyecto.
- 
---

## Testing
Copilot debe generar tests cuando se agreguen funcionalidades importantes.

**Unit Tests**
- Librerías:
    - JUnit 5
    - Mockito / MockMvc
    - Spring Boot Test
- Tests para:
    - Servicios
    - Repositorios (con DataJpaTest)
    - Controladores (MockMvc)

**Integration Tests**
- Usar `@SpringBootTest` solo cuando sea necesario.
- Tests deben ser aislados, claros y con nombres descriptivos.

---

## Buenas Prácticas y Estilo
- Aplicar principios SOLID.
- Evitar lógica en controladores; moverla a servicios.
- Validación con `jakarta.validation` usando `@Valid`.
- Manejo de errores con `@ControllerAdvice` y excepciones personalizadas.
- Usar `record` en Java 21 para DTOs cuando aplique.
- Evitar código duplicado; sugerir refactorización cuando existan patrones repetidos.
- No usar APIs deprecadas.
- Escribir código con convenciones de estilo oficiales de Java (Java Code Conventions).

---

## Seguridad
- Usar **Spring Security** sin inventar filtros o configuraciones.
- Configurar seguridad con clases modernas basadas en `SecurityFilterChain`.
- Manejo correcto de JWT solo si está bien documentado y con dependencias oficiales.
- Nunca inventar algoritmos de hashing o firmas.

---

## Expectativas de Copilot
Copilot debe:

1. Proponer soluciones basadas solo en documentación oficial de Spring y Java.
2. Generar código limpio, mantenible y modular.
3. Explicar decisiones de arquitectura cuando sea apropiado.
4. Sugerir **refactorizaciones** cada vez que detecte redundancia o complejidad innecesaria.
5. Generar tests unitarios e integrales para nuevas funcionalidades.
6. Evitar suposiciones cuando falte información; pedir contexto o responder **“no sé”**.
7. Usar patrones modernos de Spring Boot (sin `WebSecurityConfigurerAdapter`, sin APIs obsoletas).

---

## Fuentes válidas
Copilot debe basarse únicamente en:

- https://spring.io/projects/spring-boot
- https://docs.spring.io/spring-boot/docs/current/reference/html/
- https://docs.spring.io/spring-framework/docs/current/reference/html/
- https://docs.spring.io/spring-security/reference/
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/
- https://docs.oracle.com/en/java/javase/21/
- Documentación oficial de dependencias compatibles

---

## Resultado Esperado
Un asistente que:

- Genere controladores, servicios, repositorios y configuraciones limpias con Spring Boot.
- Mantenga la arquitectura bien organizada y fácil de mantener.
- Aplique prácticas seguras y modernas de Spring Security.
- Sugiera mejoras y refactorizaciones continuamente.
- No invente información técnica.
- Genere tests desde el inicio del desarrollo.
- Entregue código claro, preciso y respaldado por documentación oficial.

