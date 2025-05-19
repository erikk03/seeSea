# Frontend README

This React frontend was bootstrapped with [Create React App](https://github.com/facebook/create-react-app) and follows a feature-driven folder structure for maintainability.

---

## Project Setup

Install dependencies:

```bash
npm install
```

Start in development mode:

```bash
npm start
```

Build for production:

```bash
npm run build
```

Run tests:

```bash
npm test
```

Eject (one-way operation):

```bash
npm run eject
```

---

## Available Scripts

In the `frontend/` directory, you can run:

- **`npm start`**: Runs the app in development mode at [http://localhost:3000](http://localhost:3000).
- **`npm test`**: Launches the interactive test runner.
- **`npm run build`**: Bundles the app for production into the `build/` folder.
- **`npm run eject`**: Ejects configuration for full control (irreversible).

Refer to the official Create React App docs for more details on each command.

---

## Folder Structure

```
fe/
├── public/             # Static files (index.html, favicon, manifest)
├── src/                # Application source code
│   ├── assets/         # Images, fonts, SVGs
│   ├── components/     # Shared UI components (Buttons, Modals)
│   ├── features/       # Feature-specific code (grouped by domain)
│   │   ├── auth/       # LoginPage.js,
│   │   └── map/        # WebSocketMap.js,
│   ├── hooks/          # Custom React hooks (useAuth, useWebSocket)
│   ├── context/        # React Context providers (ThemeContext, AuthContext)
│   ├── services/       # API & WebSocket clients (axios, socket setup)
│   ├── utils/          # Pure helper functions (formatDate, validators)
│   ├── styles/         # Global CSS, resets, variables
│   ├── App.js          # Root component with routing/providers
│   ├── App.css         # Global or App-level styles
│   ├── index.js        # Entry point rendering `<App />`
│   ├── index.css       # Global CSS reset/base styles
│   ├── reportWebVitals.js # Performance metrics helper
│   └── setupTests.js   # Test setup (Jest, React Testing Library)
└── package.json        # Scripts, dependencies, metadata
```

### Directory Descriptions

- **`public/`**: Files copied directly into build output; contains `index.html`, icons, and manifest.
- **`src/assets/`**: Static assets like images, fonts, and SVGs.
- **`src/components/`**: Pure, reusable UI controls. Keep business logic minimal here.
- **`src/features/`**: Group code by domain (e.g., `auth`, `map`, `billing`). Each feature folder holds its UI components/pages, state slices, and API calls.
- **`src/hooks/`**: Custom hooks encapsulating reusable React logic.
- **`src/context/`**: Context providers for cross-cutting concerns (theme, global state).
- **`src/services/`**: API client and socket configuration.
- **`src/utils/`**: Stateless helper functions. No side effects or React code.
- **`src/styles/`**: Global CSS files, variables, and resets.
- **Root files**:
  - `App.js`: Main app component, sets up routes and providers.
  - `index.js`: Bootstraps React into the DOM.
  - `reportWebVitals.js`: Optional performance measurement.
  - `setupTests.js`: Testing framework setup.
  - CSS files: `App.css` and `index.css` for global styling.

---

This structure balances Create React App conventions with a clear, feature-driven layout. It should help onboard new developers quickly and keep the codebase organized as it scales!