# PricePilot Live Price API

This small server keeps the shopping-provider API key out of the Android APK.

## Run locally

1. Install Node.js 20+.
2. From `server/`, run `npm install`.
3. Set `SERPAPI_KEY` in the environment.
4. Run `npm start`.
5. The API will be available at `http://localhost:3000`.

The Android app expects `GET /search?q=<product>` and receives `{ "products": [...] }`.

## Android configuration

Create/update the project's ignored `local.properties` file and add:

`PRICE_API_BASE_URL=http://YOUR_COMPUTER_IP:3000`

For a physical Android phone, do not use `localhost`; use the computer's LAN IP when the phone and computer are on the same network. For production, deploy this server over HTTPS and use that HTTPS URL.

The server uses a shopping-results provider to obtain current search results. Prices and availability can change and should be treated as a snapshot, not a guarantee. The provider's terms and the stores' terms should be followed.
