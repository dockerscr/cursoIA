import { defineConfig, devices } from '@playwright/test';

/**
 * End-to-end configuration for the AstroBookings Client.
 *
 * Two deployed instances of the WAR are expected to be running:
 *
 *  - APP_BASE_URL: instance using the live ActivityBookings API (default http://localhost:3000).
 *  - FIXTURE_APP_BASE_URL: instance whose ASTRO_API_BASE_URL points at tests/support/fixture-api.mjs,
 *    used only to exercise the empty and error states, which the live API cannot produce on demand.
 *
 * The fixture API is started automatically by Playwright (see `webServer` below).
 */
const APP_BASE_URL = (process.env.APP_BASE_URL ?? 'http://localhost:8080/astrobookings-client').replace(/\/+$/, '');
const FIXTURE_APP_BASE_URL = (process.env.FIXTURE_APP_BASE_URL ?? 'http://localhost:8180/astrobookings-client').replace(
  /\/+$/,
  ''
);
const API_BASE_URL = (process.env.API_BASE_URL ?? 'http://localhost:3000').replace(/\/+$/, '');
const FIXTURE_API_PORT = Number(process.env.FIXTURE_API_PORT ?? '3901');
const FIXTURE_API_URL = `http://127.0.0.1:${FIXTURE_API_PORT}`;

export const e2eConfig = {
  appBaseUrl: APP_BASE_URL,
  fixtureAppBaseUrl: FIXTURE_APP_BASE_URL,
  apiBaseUrl: API_BASE_URL,
  fixtureApiUrl: FIXTURE_API_URL,
};

export default defineConfig({
  testDir: './tests',
  outputDir: './test-results',
  fullyParallel: false,
  // The fixture API has a single mutable mode, so tests must not interleave.
  workers: 1,
  retries: 0,
  timeout: 45_000,
  expect: { timeout: 10_000 },
  reporter: [['list'], ['html', { open: 'never', outputFolder: 'playwright-report' }]],
  use: {
    baseURL: APP_BASE_URL,
    locale: 'en-US',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
  ],
  webServer: [
    {
      command: `node tests/support/fixture-api.mjs`,
      url: `${FIXTURE_API_URL}/health`,
      env: { FIXTURE_API_PORT: String(FIXTURE_API_PORT) },
      reuseExistingServer: !process.env.CI,
      timeout: 20_000,
      stdout: 'pipe',
      stderr: 'pipe',
    },
  ],
});
