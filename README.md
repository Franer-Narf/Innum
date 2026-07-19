# Innum 
> English

**Index emptionum** — a lightweight, offline shopping-list application for Android.

## Purpose

The objective of Innum is to provide a practical shopping-list tool without accounts, unnecessary services, or online data collection.

The application supports multiple lists, product quantities, local storage, and JSON import or export when the user wants to move or share the data.

## Features

- Create and manage multiple shopping lists.
- Add products with an optional quantity.
- Edit list names and product information with a long press.
- Delete products or lists with a swipe.
- Delete all products from a list.
- Store information locally with SQLite.
- Export all lists to JSON.
- Import a previously exported Innum JSON file.
- Export or share an individual shopping list.
- Share exported data through Android's standard share menu.
- Use the application without an account or internet connection.

## Download

APK releases are available from the repository's [Releases](https://github.com/Franer-Narf/Innum/releases) page.

## Requirements

- Android 8.1 or later (`minSdk 27`).
- Android Studio with its bundled JDK.
- Android SDK 36 for the current project configuration.

## Build from source

```bash
git clone https://github.com/Franer-Narf/Innum.git
cd Innum
./gradlew assembleDebug
```

On Windows:

```powershell
gradlew.bat assembleDebug
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

You can also open the project in Android Studio and run it on an emulator or connected device.

## Usage

### Create a shopping list

1. Open Innum.
2. Enter a name for the new list.
3. Add the list and open it.
4. Enter a product name and quantity.
5. Continue adding products as needed.

When no quantity is entered, Innum uses a default quantity of `1`.

### Edit or delete content

- Long-press a list to rename it.
- Long-press a product to edit its name or quantity.
- Swipe an item to remove it.
- Use the delete button to remove the current list and its products.

### Import and export

Use the application menu to:

- Import a complete Innum JSON backup.
- Save all lists as a JSON file.
- Share the exported JSON file.
- Export or share one specific list.

## Project structure

```text
app/src/main/java/nc/instrumentum/innum/
├── MainActivity.java        Launch screen
├── FirstActivity.java       Information screen
├── SecondActivity.java      Products inside one shopping list
├── ThirdActivity.java       Shopping-list management and import/export
├── DataBaseManager.java     SQLite storage and JSON operations
├── ListClass.java           Shopping-list model
└── Product.java             Product model
```

## Main technologies

- Java
- AndroidX
- SQLite
- JSON reader and writer APIs
- Android Storage Access Framework
- Android FileProvider and share intents

## Data and privacy

Innum does not declare internet access. Lists and products are stored in a local SQLite database.

Data is only exported or shared when the user selects that action. Android backup behavior follows the application and device backup settings.

The application does not collect, use, or share information provided by the user, except when strictly necessary to provide its core functionality. All information is processed solely for the proper operation of the application.


## Language

The current application interface includes English and Spanish text.

## License

This project is licensed under the [Creative Commons Attribution-NonCommercial 4.0 International License](LICENSE.txt).

You may share and adapt the project with attribution, but commercial use is not permitted.

## Author

Developed by [Franer-Narf](https://github.com/Franer-Narf).

<br>
<br>
<br>

---

<br>
<br>
<br>

# Innum
>Español

**Index emptionum** — una aplicación ligera y sin conexión para gestionar listas de la compra en Android.

## Objetivo

El objetivo de Innum es ofrecer una herramienta práctica para crear listas de la compra, sin cuentas, servicios innecesarios ni recopilación de datos en línea.

La aplicación permite gestionar varias listas, añadir cantidades a los productos, almacenar la información localmente e importar o exportar archivos JSON cuando el usuario quiera trasladar o compartir sus datos.

## Características

* Crear y gestionar varias listas de la compra.
* Añadir productos con una cantidad opcional.
* Editar los nombres de las listas y la información de los productos mediante una pulsación prolongada.
* Eliminar productos o listas deslizando el elemento.
* Eliminar todos los productos de una lista.
* Almacenar la información localmente mediante SQLite.
* Exportar todas las listas a un archivo JSON.
* Importar un archivo JSON exportado previamente desde Innum.
* Exportar o compartir una lista de la compra individual.
* Compartir los datos exportados mediante el menú estándar de Android.
* Utilizar la aplicación sin una cuenta ni conexión a Internet.

## Descarga

Las versiones APK están disponibles en la página de [Releases](https://github.com/Franer-Narf/Innum/releases) del repositorio.

## Requisitos

* Android 8.1 o una versión posterior (`minSdk 27`).
* Android Studio con su JDK integrado.
* Android SDK 36 para la configuración actual del proyecto.

## Compilación desde el código fuente

```bash
git clone https://github.com/Franer-Narf/Innum.git
cd Innum
./gradlew assembleDebug
```

En Windows:

```powershell
gradlew.bat assembleDebug
```

El APK de depuración se generará en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

También puedes abrir el proyecto en Android Studio y ejecutarlo en un emulador o en un dispositivo conectado.

## Uso

### Crear una lista de la compra

1. Abre Innum.
2. Introduce un nombre para la nueva lista.
3. Añade la lista y ábrela.
4. Introduce el nombre y la cantidad de un producto.
5. Continúa añadiendo productos según sea necesario.

Cuando no se introduce ninguna cantidad, Innum utiliza el valor predeterminado `1`.

### Editar o eliminar contenido

* Mantén pulsada una lista para cambiarle el nombre.
* Mantén pulsado un producto para editar su nombre o cantidad.
* Desliza un elemento para eliminarlo.
* Utiliza el botón de eliminación para borrar la lista actual y sus productos.

### Importar y exportar

Utiliza el menú de la aplicación para:

* Importar una copia de seguridad completa de Innum en formato JSON.
* Guardar todas las listas en un archivo JSON.
* Compartir el archivo JSON exportado.
* Exportar o compartir una lista específica.

## Estructura del proyecto

```text
app/src/main/java/nc/instrumentum/innum/
├── MainActivity.java        Pantalla de inicio
├── FirstActivity.java       Pantalla de información
├── SecondActivity.java      Productos de una lista de la compra
├── ThirdActivity.java       Gestión de listas e importación/exportación
├── DataBaseManager.java     Almacenamiento SQLite y operaciones JSON
├── ListClass.java           Modelo de lista de la compra
└── Product.java             Modelo de producto
```

## Tecnologías principales

* Java
* AndroidX
* SQLite
* APIs de lectura y escritura JSON
* Storage Access Framework de Android
* FileProvider de Android e intents para compartir

## Datos y privacidad

Innum no declara permisos de acceso a Internet. Las listas y los productos se almacenan en una base de datos SQLite local.

Los datos solo se exportan o comparten cuando el usuario selecciona expresamente esa acción. El comportamiento de las copias de seguridad de Android depende de la configuración de la aplicación y del dispositivo.

La aplicación no recopila, utiliza ni comparte la información proporcionada por el usuario, salvo cuando sea estrictamente necesario para ofrecer sus funciones principales. Toda la información se procesa exclusivamente para garantizar el correcto funcionamiento de la aplicación.

## Idioma

La interfaz actual de la aplicación incluye textos en inglés y español.

## Licencia

Este proyecto está disponible bajo la licencia [Creative Commons Attribution-NonCommercial 4.0 International](LICENSE.txt).

Puedes compartir y adaptar el proyecto siempre que proporciones la atribución correspondiente, pero no se permite su uso comercial.

## Autor

Desarrollado por [Franer-Narf](https://github.com/Franer-Narf).
