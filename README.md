# Proyecto1Progra3
Proyecto3 Isaac Hernandez Vargas(Grupo 2) Dylan Araya(Grupo 1)
# Falta logica de reservas!!! YA
# Boton de Imprimir pdfs FALTA IMPLEMENTACION
# Panel calendarizacion YA
# Panel actividades YA (vista semanal por hora, cruza reservas activas con fecha/hora)
# Panel estadisticas
# Crear Frame de funcionarios YA
# Incluir el llm YA (pestaña "Asistente IA" en la vista de Funcionario — usa la API de Claude para interpretar solicitudes en lenguaje natural y prellenar el formulario de reservas)
# Configurar mejor la GUI - colores YA (tema oscuro aplicado consistentemente vía Theme/Themes.java)
# revisar el cambio de contrasenias YA
# en categorias revisar el textfield de id, no deja escribir, igual cambiar el layout de ahi YA
# en recursos revisar combobox de categoria, al dar limpiar no se cambia, aniadir un estado null YA
# en agregar categorias falta una funcion que aumente el contador del id, creo que es por el segundo constructor para el xml YA
# en recursos el cmbbox solo se actualiza si se cierra el programa. YA
# si se borra un recurso, no se puede borrar la categoria, revisar el metodo YA
# agregar un boton que cierre sesion, osea que envie de nuevo al panel de login
# hacer las junit testing 
## Configurar el Asistente de IA
El Asistente de IA (pestaña "Asistente IA" en la vista de Funcionario) necesita una llave de la API de Anthropic para funcionar. Configurela de una de estas dos formas:

1. Variable de entorno `ANTHROPIC_API_KEY`, o
2. Un archivo `apikey.txt` en la raiz del proyecto, con la llave como unico contenido (este archivo esta en `.gitignore`, nunca se sube a GitHub).

Sin la llave configurada, el resto de la aplicacion funciona normal; solo esa pestaña muestra un mensaje pidiendo configurarla.

## Validaciones de integridad agregadas
- No se puede borrar un Recurso que tenga reservas activas.
- No se puede borrar un Funcionario que tenga reservas activas.
- No se puede borrar una Categoria que tenga recursos asociados (ya existia).
