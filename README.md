# CafeteriaSOFT

Sistema de gestión para cafetería desarrollado en *Java (JDK 27)* con arquitectura en capas, núcleo independiente, interfaz dual (*Consola* y *Swing GUI) y patrones de diseño **Decorator, **Strategy* y *Observer*.

## Funcionalidades implementadas
- *RF01*: Catálogo de platos, bebidas, acompañamientos, postres y precios.
- *RF02*: Personalización de productos con adiciones mediante patrón **Decorator* actualizando precio y descripción.
- *RF03*: Combos paso a paso (bebida, plato, acompañamiento y postre opcional) con precio especial.
- *RF04*: Creación y control de estados de pedidos (CREADO, EN_PREPARACION, LISTO).
- *RF05*: Notificaciones automáticas a estaciones (Cocina, Caja, Mesero) mediante patrón **Observer*.
- *RF06*: Promociones dinámicas (Happy Hour, Fin de semana, Sin promoción) mediante patrón **Strategy*.
- *RF07*: Cuadre de ventas y reporte del turno actual por medio de pago.
- *RF08*: Validación de disponibilidad de ítems antes de ingresarlos al pedido.
- *RF09*: Historial de pedidos en memoria consultable durante el turno.
- *RF10*: Simulación de pagos (EFECTIVO, TARJETA, TRANSFERENCIA).

## Compilación y ejecución

### Con Maven (recomendado)
```bash
# Ejecutar pruebas unitarias
mvn test

# Compilar y empaquetar
mvn clean package

# Ejecutar en modo consola
java -cp target/classes vista.Main

# Ejecutar en modo interfaz gráfica (GUI)
java -cp target/classes vista.Main --gui
