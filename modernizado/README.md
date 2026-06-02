# Ecopetrol — Aplicativo Modernizado

Versión modernizada del *Portal de Solicitudes* (talento). Reescritura del frontend
basada en el diseño de Figma "Diseño ecopetrol portal de solicitudes".

## Estructura

```
modernizado/
└── front/        # Angular 21 (standalone + signals) + Tailwind v4
```

> El backend modernizado se agregará en `modernizado/back/` más adelante.

## Stack del front

- **Angular 21** — componentes standalone, signals, control flow `@if/@for/@switch`, rutas lazy (`loadComponent`).
- **Tailwind CSS v4** — tokens de marca (`@theme`) + clases de componente (`.app-card`, `.btn-primary`, `.field-input`, `.badge-*`).
- Estado con **signals** (`SessionService`, `SolicitudesStore`, `ToastService`). Datos *mock* en memoria.

## Pantallas (5, derivadas de los 12 frames del Figma)

| Ruta | Pantalla |
|------|----------|
| `/login` | Login — "Sistema de Permisos" |
| `/rol` | Selecciona tu Rol (Empleado / Líder / Gestión de People) |
| `/empleado` | Portal del Empleado — Nueva Solicitud (4 tipos) + Mis Solicitudes |
| `/lider` | Panel de Aprobaciones — KPIs, filtros, aprobar/rechazar + toast |
| `/people` | Reportes Mensuales — KPIs, tabla por empleado, descargar/cierre |

## Correr en local

```bash
cd modernizado/front
npm install
npm start            # http://localhost:4200  (ng serve con proxy a /talento)
npm run build        # build de producción
```

## Conectar al backend real

La app usa datos mock en `src/app/core/mock-data.ts`. Para consumir el backend
`/talento/**`, `proxy.conf.json` ya apunta al frontend público que proxya al ACI.
El siguiente paso es reemplazar las llamadas del `SolicitudesStore`/`SessionService`
por un `HttpClient` contra esos endpoints (login, listar*, crear*, aprobar*, etc.).
