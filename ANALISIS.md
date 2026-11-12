# Taller 8 — Análisis

## Predicción inicial

Antes de ejecutar el experimento sin manejo de errores, escribe aquí tu predicción:

> Si el Repository llama a Retrofit sin try/catch desde viewModelScope.launch, y desactivo internet antes de volver a cargar, ¿qué ocurre y por qué?

**Mi predicción, escrita antes de la prueba:** _Completar antes de provocar el fallo._

## Contrato de la API

Rick & Morty y PokéAPI pueden devolver una colección bajo `results`, pero sus objetos representan recursos distintos y tienen campos y estructuras diferentes. En Rick & Morty, cada personaje incluye `id`, `name`, `status`, `species` e `image`.

**URL consultada:** https://rickandmortyapi.com/api/character

## Experimento sin manejo de errores

Realiza este paso en una copia temporal del proyecto, primero con conexión y luego sin conexión, pulsando «Recargar». Guarda una captura de Logcat y registra aquí la excepción que realmente observaste; no la adivines.

- **Excepción exacta observada:** _Completar con el nombre y mensaje de Logcat._
- **Dispositivo/emulador y condición de red:** _Completar._
- **Causa técnica:** sin capturar el fallo de la petición suspendida, la excepción puede escapar de la corrutina iniciada por `viewModelScope.launch`; según el contexto de la corrutina y la configuración de la app, esto puede terminar en un crash. La excepción concreta depende de la red y del dispositivo.
- **Corrección aplicada:** el Repository devuelve `Result`, vuelve a lanzar `CancellationException` para conservar la cancelación normal y transforma otros fallos en `Result.failure`. El ViewModel publica `UiState.Error` y permite volver a cargar.

## Error HTTP frente a fallo de conexión

Un `HttpException` indica que el servidor respondió con un estado HTTP no exitoso (por ejemplo, 404). Una `IOException` normalmente indica un fallo de transporte, como DNS, conexión o timeout, donde no se recibió una respuesta HTTP válida. La excepción exacta debe reportarse según lo observado en la prueba, no inferirse solo por haber desactivado una conexión.

## Decisión de diseño del detalle

La app usa la opción **B**: navega pasando el `id` y busca el personaje en la lista ya cargada en el ViewModel compartido; no realiza una petición HTTP desde el Composable. Si se abre directamente la ruta de detalle sin haber cargado antes la lista, el personaje no estará en la caché y se mostrará un mensaje para regresar a la lista.

## Evidencias pendientes

- [ ] JSON real abierto en el navegador.
- [ ] Lista real cargada con internet.
- [ ] Detalle abierto al tocar un personaje.
- [ ] Logcat de la versión temporal sin manejo de errores.
- [ ] Estado Error en la versión corregida, sin crash.
- [ ] Lista recuperada tras «Reintentar» con la red restaurada.
