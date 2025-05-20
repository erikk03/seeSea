# Ships Map – Frontend (React + Vite)

This is the frontend for the **Ships Map** project, built with [React](https://react.dev/) and [Vite](https://vitejs.dev/) for modern, fast development.

---

## 🚀 Quick Start

### 📦 Install dependencies
```bash
npm install
```

### 🧪 Start in development mode (with HTTPS)
```bash
npm run dev
```
> Make sure you have `ships.key` and `ships.crt` in the project root for HTTPS. You can generate a dev cert with OpenSSL if needed.

### 📦 Build for production
```bash
npm run build
```

### 🔍 Preview production build locally
```bash
npm run preview
```

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

## 🧪 Testing Setup

- `@testing-library/react` and `jest-dom` are installed.
- You can write tests in `*.test.js(x)` files.
- Test setup lives in: `src/config/setupTests.js`

Run tests with:

```bash
npm test
```

(Optional: configure `vitest` for Vite-native testing)

---

## ✅ Scripts

```bash
npm run dev        # Start dev server (https://localhost:3000)
npm run build      # Build for production
npm run preview    # Preview production build locally
```

---

## 🛠 Tech Stack

- **React 19**
- **Vite** (dev server + bundler)
- **React Router v7**
- **Leaflet / React Leaflet**
- **SockJS + STOMP.js** (for real-time map data)
- **Testing Library**

---

## 📦 Deployment Tips

- Build output is in the `/dist` folder
- Ensure any HTTPS certs used in production are real and trusted
- Vite assets are served as static files — can be hosted on any static host (Netlify, Vercel, Nginx, etc.)

---

This structure is modular, fast, and easy to scale. Happy coding! ⚓🗺️