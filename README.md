# 🎵 Waxly App

Waxly es una aplicación Android desarrollada con **Jetpack Compose** que permite a los usuarios **explorar, coleccionar y gestionar vinilos**, manteniendo una colección personal y una lista de deseos (*wantlist*).

El proyecto está enfocado en buenas prácticas de **arquitectura**, **persistencia local** y **testing**, con un enfoque académico y formativo.

---

## 📱 Funcionalidades principales

- 🔐 **Autenticación de usuarios**
  - Registro e inicio de sesión
  - Persistencia de sesión

- 🏠 **Home**
  - Visualización de vinilos destacados
  - Acciones rápidas para agregar a colección o wantlist

- 💿 **Colección**
  - Gestión de vinilos propios
  - Búsqueda y agregado dinámico

- ⭐ **Wantlist**
  - Lista de vinilos deseados
  - Administración y búsqueda

- 🧾 **Feedback visual**
  - Diálogos de acción
  - Mensajes de confirmación mediante Snackbars

- 🧪 **Testing**
  - Test unitario de autenticación
  - Test de UI para interacción con el diálogo de vinilos

---

## 🗄️ Persistencia de datos

- Se utiliza **Room Database** para almacenamiento local
- La base de datos se inicializa con un **seed de vinilos**
- Las imágenes se referencian mediante el nombre del recurso (`coverName`)
- La UI resuelve dinámicamente los drawables desde `res/drawable`

---

## 🧪 Testing

El proyecto incluye **dos pruebas clave**, enfocadas en estabilidad y claridad.

### ✅ Test unitario

**AuthViewModelTest**

- Verifica:
  - Login exitoso
  - Manejo de errores de autenticación
- Usa:
  - Fake DAO en memoria
  - JUnit4
  - Corrutinas con `runBlocking`

📍 Ubicación:
app/src/test/java/com/app/waxly/AuthViewModelTest.kt

---

### ✅ Test de UI (Compose)

**VinylActionDialogTest**

- Verifica:
  - Renderizado correcto del diálogo
  - Interacción del usuario
  - Ejecución de callbacks al presionar botones

📍 Ubicación:
app/src/androidTest/java/com/app/waxly/VinylActionDialogTest.kt

---

## 🛠️ Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Room
- Coroutines
- Material 3
- JUnit4
- Compose UI Testing

---

## ▶️ Cómo ejecutar el proyecto

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/waxly-app.git
2. Abre el proyecto en Android Studio
3. Sincroniza Gradle
4. Ejecuta la app en un emulador o dispositivo físico

---

## 🧪 Ejecutar tests

- Unit tests
Click derecho sobre AuthViewModelTest → Run
- UI tests
 * Asegúrate de tener un emulador activo
Click derecho sobre VinylActionDialogTest → Run

---

📌 Notas importantes

- Si se agregan nuevos vinilos al seed:
  Es necesario borrar la app o aumentar la versión de la base de datos
- Los nombres de las imágenes deben coincidir exactamente con coverName
- Proyecto desarrollado con fines educativos y académicos

👤 Autor
Desarrollado por Cristóbal Segovia y Bastián Sepúlveda
Proyecto académico – Ingeniería en Informática
