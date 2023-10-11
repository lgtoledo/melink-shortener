# Documentación de servicio Acortador de URLs


## Generar URL Corta

`getShortLink` es una función Azure diseñada para generar un enlace corto a partir de un enlace largo proporcionado. 

## Endpoint

`POST [BASE_URL]/getShortLink`

## Parámetros

- **body**: Se debe proporcionar en el cuerpo de la solicitud la URL larga que se desea acortar.

## Retorno

- En caso de éxito, devuelve un HTTP status `CREATED` (201) con el enlace corto en formato JSON.
- En caso de error, devuelve un HTTP status correspondiente con el código y motivo del error en el cuerpo.

### Codiguera para Respuestas
| Código | Significado                                               |
|--------|----------------------------------------------------------|
| 0      | OK                                                       |
| 4001   | Se debe de proporcionar un link largo válido.            |
| 5001   | No se pudo generar un link corto único.                  |
|        |                                                          |

## Ejemplos

### Ejemplo : Crear un enlace corto

Solicitud:
```http
POST [BASE_URL]/getShortLink
Content-Type: text/plain

https://www.example.com
```

Respuesta esperada:
```json
{
  "code":0,
  "messagge":"OK",
  {
    "shortLink": "[URL]/aAbB12",
    "longLink": "https://www.example.com"
  }
}
```


---
---
###

## Obtener URL Larga

`getLongLink` es una función Azure diseñada para obtener un enlace largo a partir de un enlace corto proporcionado. La función busca primero en la caché de Redis y, si no lo encuentra, busca en Cosmos DB. Si se encuentra el enlace largo, la función redirige al cliente a ese enlace largo.

## Endpoint

`GET [BASE_URL]/l/{linkCorto}`

## Parámetros

- **linkCorto**: Es el identificador único del enlace corto. Se proporciona en la ruta de la URL.

## Retorno

- En caso de éxito, redirige (HTTP status `FOUND` o 302) al enlace largo correspondiente.
- Si no se encuentra el enlace en Redis ni en Cosmos DB, devuelve un HTTP status `NOT_FOUND` (404) con un código y mensaje indicando que no se encontró el enlace.
- Si ocurre un error durante el procesamiento, devuelve un HTTP status `INTERNAL_SERVER_ERROR` (500) con un código y mensaje de error.

### Codiguera para Respuestas

| Código Personalizado | Significado                                              |
|----------------------|----------------------------------------------------------|
| 4004                 | No se encontró el enlace requerido                       |
| 5001                 | Error al procesar el enlace                              |
|                                                                                 |


## Ejemplos

### Ejemplo: Obtener un enlace largo a partir de un enlace corto

Solicitud:
```
GET [BASE_URL]/l/abcd1234
```

Respuesta esperada:
- Redirección al enlace largo correspondiente (si se encuentra).
- O un código y mensaje de error (si no se encuentra o hay un problema).



---
---


###


# Obtener estadísticas de una URL corta

La función `linkStats` proporciona estadísticas asociadas a un enlace corto específico.

## Endpoint

`GET [BASE_URL]/linkStats?link=[SHORT_LINK]`

## Parámetros de Consulta

- **link**: URL corta (Short Link) para la que se requieren las estadísticas.

## Retorno

- En caso de éxito, devuelve un objeto JSON que incluye un código, un mensaje y datos de estadísticas asociados al enlace corto.
- En caso de error, devuelve un objeto JSON con el código de error y el mensaje descriptivo.

## Ejemplos

### Ejemplo: Obtener estadísticas de un enlace corto

**Solicitud**:
```
GET [BASE_URL]/linkStats?link=https://short.link/l/abc123
```

**Respuesta exitosa**:
```
{
  "code": 0,
  "message": "OK",
  "data": {
    "id": "abc123",
    "creationDateUTC": "2023-10-11T02:23:51.927101400",
    "firstAccessedDateUTC": "2023-10-11T02:24:07.385953",
    "lastAccessedDateUTC": "2023-10-11T02:24:24.532155900",
    "accessCount": 2
  }
}
```

**Respuesta con error (enlace no válido)**:
```
{
  "code": 4001,
  "message": "Se debe de proporcionar un link válido."
}
```

**Respuesta con error (no se encontró información estadística)**:
```
{
  "code": 4004,
  "message": "No se encontró información estadística para el enlace proporcionado."
}
```

## Codiguera

| Código | Significado                                                                    |
|-------|--------------------------------------------------------------------------------|
| 0     | Operación exitosa. Los datos solicitados se encuentran en el campo `data`.     |
| 4001  | Se debe de proporcionar un link válido.                                        |
| 4004  | No se encontró información estadística para el enlace proporcionado.           |


---
---
###

# Eliminar un link

La función `deleteLink` está diseñada para eliminar un enlace corto específico y sus estadísticas asociadas.

## Endpoint

`DELETE [BASE_URL]/deleteLink?id=[LINK_ID]`

## Parámetros de Consulta

- **id**: ID del enlace corto (Short Link) que se desea eliminar.

## Retorno

- En caso de éxito, devuelve un objeto JSON con un código y un mensaje indicando la eliminación exitosa.
- En caso de error, devuelve un objeto JSON con el código de error y el mensaje descriptivo.

## Ejemplos

### Ejemplo: Eliminar un enlace corto

**Solicitud**:
```
DELETE [BASE_URL]/deleteLink?id=abc123
```

**Respuesta exitosa**:
```
{
  "code": 0,
  "message": "OK"
}
```

**Respuesta con error (enlace no encontrado)**:
```
{
  "code": 4004,
  "message": "No se encontró el link a eliminar."
}
```

**Respuesta con error (error interno)**:
```
{
  "code": 5001,
  "message": "Error al eliminar el link."
}
```

## Codiguera

| Código | Significado                                        |
|-------|----------------------------------------------------|
| 0     | Operación exitosa.                                 |
| 4004  | No se encontró el link a eliminar.                 |
| 5001  | Error interno al intentar eliminar el enlace.      |

