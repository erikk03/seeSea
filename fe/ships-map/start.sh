#!/bin/bash
VITE_HTTPS=true \
VITE_SSL_CRT_FILE=./ships.crt \
VITE_SSL_KEY_FILE=./ships.key \
npm run dev
