# Puerto Veracruz 3D

Este proyecto es una representación gráfica tridimensional e interactiva del malecón y zona portuaria de la ciudad de Veracruz, México. Está desarrollado  en **Java**, empleando las bibliotecas **JOGL** (Java Binding for the OpenGL API) y **JOML** (Java OpenGL Math Library) para construir un motor de renderizado gráfico de alto rendimiento a 60 fotogramas por segundo.

## Características Principales

- **Shadow Mapping Avanzado:** Renderizado en dos pasadas utilizando un *Framebuffer Object* (FBO) con un *Shadow Map* de 8192x8192 píxeles, logrando sombras dinámicas de alta fidelidad.
- **Suavizado de Sombras (PCF):** Implementación de filtro matemático PCF (Percentage-Closer Filtering) 3x3 para evitar el efecto de sierra (aliasing) en los bordes de las sombras.
- **Modelo de Iluminación Blinn-Phong:** Ecuación lumínica programada en *Shaders* GLSL para calcular la luz ambiental, luz difusa dependiente del ángulo del sol, y destellos especulares reflexivos.
- **Geometría Procedural:** Construcción algorítmica de los edificios y monumentos históricos (Faro Carranza, Hotel Emporio, Muelle, etc.) utilizando únicamente polígonos matemáticos primarios (cajas, cilindros, esferas).
- **Importador de Modelos OBJ (.obj):** Un *parser* personalizado capaz de cargar modelos complejos (como yates y helicópteros) en formato Wavefront OBJ.
- **Horneado de Texturas (Texture Baking):** Técnica empleada durante la importación de modelos complejos para transcribir el color de una imagen mapeada directamente al formato unificado de colores por vértice.
- **Cámara Primera Persona (FPS):** Sistema de navegación interactivo para recorrer la escena libremente.

## Requisitos del Sistema

- **Java Development Kit (JDK) 17** o superior.
- **Apache Maven** (para la gestión de dependencias y compilación).
- Tarjeta gráfica con soporte para OpenGL 2.0 o superior.

## Instalación y Ejecución

El proyecto está configurado con Maven, por lo que la importación de librerías (`jogl-all`, `gluegen-rt`, `joml`) se realiza automáticamente.

1. Abre una terminal en la raíz del proyecto (donde se encuentra el archivo `pom.xml`).
2. Compila el proyecto ejecutando:
   ```bash
   mvn clean install
   ```
3. Ejecuta la clase principal desde tu IDE (buscando `PuertoVeracruz.java` y ejecutando su método `main`) o utiliza Maven:
   ```bash
   mvn exec:java -Dexec.mainClass="veracruz.PuertoVeracruz"
   ```

## Controles de Cámara

El simulador utiliza un sistema de control idéntico al de los videojuegos de PC:

- **Ratón:** Mover el ratón controla la rotación de la cámara (Mirar alrededor).
- **W / S:** Avanzar y retroceder.
- **A / D:** Desplazamiento lateral (Strafe izquierdo/derecho).
- **Q / E:** Modificar la altitud (Subir / Bajar verticalmente).
- **Shift Izquierdo (mantener):** Aumentar velocidad de movimiento (Correr).
- **Rueda del Ratón o teclas `+` y `-`:** Ajustar el zoom / Campo de Visión (FOV).
- **ESC:** Cerrar la aplicación de forma segura.

## Arquitectura de Código

El código fuente se encuentra en el paquete `veracruz` y se divide lógicamente en:

- `PuertoVeracruz.java`: Punto de entrada del programa. Configura el perfil OpenGL, inicializa la ventana `JFrame` y el animador a 60 FPS.
- `VeracruzDemo.java`: El motor de renderizado. Contiene el código fuente de los *Shaders* GLSL, configura el *Shadow Map*, maneja los controles del usuario y la cámara.
- `SceneBuilder.java`: Es el constructor del escenario. Define dónde, de qué color y con qué proporciones se colocan las primitivas geométricas para formar el malecón y sus edificios.
- `Geo.java`: Librería matemática que genera las primitivas 3D por código (cajas, esferas, cilindros, planos).
- `ObjLoader.java`: Lector e intérprete de modelos importados en formato `.obj`.
