# seeSea – Frontend (React + Vite)

This is the frontend for the **seeSea** project, built with [React](https://react.dev/) and [Vite](https://vitejs.dev/) for modern, fast development.

---

## 🚀 Quick Start

### 📦 Install dependencies
```bash
npm install
```

### 🧪 Start in development mode (with HTTPS)
```bash
./start.sh
```
> Make sure you have `ships.key` and `ships.crt` in the project root for HTTPS. You can generate a dev cert with OpenSSL if needed.

---

## 📁 Project Structure

```
fe/ships-map/
├── public/              # Optional (not used unless explicitly configured in Vite)
├── src/
│   ├── assets/          # Images, logos, static files
│   ├── components/      # Shared UI components (e.g. Header, Button)
│   ├── features/        # Domain-specific code (e.g. auth/, map/)
│   ├── pages/           # Route-based views (e.g. WelcomePage, Dashboard)
│   ├── styles/          # CSS modules or global styles
│   ├── utils/           # Utility functions (e.g. API, formatters)
│   ├── config/          # App setup files (e.g. reportWebVitals, setupTests)
│   ├── App.jsx          # App layout, providers, and routing
│   └── index.jsx        # React DOM render entry point
├── ships.key            # Local HTTPS key (development only)
├── ships.crt            # Local HTTPS cert (development only)
├── vite.config.js       # Vite configuration (includes HTTPS cert loading)
└── package.json         # Project metadata and scripts
```

---

## 🔐 HTTPS Support (Local Dev)

This project runs on `https://localhost:3000` using a local certificate.

To generate a self-signed cert:

```bash
openssl req -x509 -newkey rsa:2048 -nodes -keyout ships.key -out ships.crt -days 365 \
  -subj "/C=US/ST=Local/L=Dev/O=LocalDev/CN=localhost"
```

Make sure both files are in the root folder next to `vite.config.js`.

---

## 🛠 Tech Stack

- **React 19**
- **Vite** (dev server + bundler)
- **React Router v7**
- **Leaflet / React Leaflet**
- **SockJS + STOMP.js** (for real-time map data)
