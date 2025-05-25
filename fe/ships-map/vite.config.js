import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import fs from 'fs';

export default defineConfig({
  plugins: [react()],
  define: {
    global: {},
  },
  server: {
    port: 3000,
    https: {
      cert: fs.readFileSync(process.env.VITE_SSL_CRT_FILE),
      key: fs.readFileSync(process.env.VITE_SSL_KEY_FILE),
    },
    proxy: {
      '/socket': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        ws: true,
        secure: false
      }
    }
  },
});
