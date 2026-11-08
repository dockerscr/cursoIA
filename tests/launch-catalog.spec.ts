import { expect, test, type APIRequestContext, type Page } from '@playwright/test';
import { e2eConfig } from '../playwright.config';
import {
  contextPathOf,
  dateLabel,
  excludedActivities,
  fetchActivities,
  joinUrl,
  parseDateLabel,
  parseMoney,
  visibleActivities,
} from './support/catalog';

const APP = e2eConfig.appBaseUrl;
const FIXTURE_APP = e2eConfig.fixtureAppBaseUrl;
const CONTEXT = contextPathOf(APP);
const FIXTURE_CONTEXT = contextPathOf(FIXTURE_APP);

test.describe('Launch catalog - acceptance criteria against the live API', () => {
  test('AC1 - a request to /launches retrieves launches from the API and renders them server-side', async ({
    page,
    request,
  }) => {
    // Arrange: read the live API directly, applying the documented visibility rule.
    const visible = visibleActivities(await fetchActivities(request));
    expect(visible.length, 'the live API must expose at least one visible launch').toBeGreaterThan(0);

    // Act
    const response = await page.goto(joinUrl(APP, '/launches'));

    // Assert: HTTP contract...
    expect(response, 'the catalog page did not return a response').not.toBeNull();
    expect(response!.status()).toBe(200);
    expect(response!.headers()['content-type']).toContain('text/html');

    // ...and the rendered catalog reflects the API data (only a server-side fetch can produce it).
    const rows = page.locator('tbody tr');
    await expect(rows).toHaveCount(visible.length);
    await expect(page.getByRole('heading', { name: 'Upcoming rocket launches' })).toBeVisible();

    // Server-side rendering: the catalog ships no client-side JavaScript.
    await expect(page.locator('script')).toHaveCount(0);
  });

  test('AC2 - each listed launch shows name, date, location, price, duration and maximum seats', async ({
    page,
    request,
  }) => {
    const visible = visibleActivities(await fetchActivities(request));
    expect(visible.length, 'the live API must expose at least one visible launch').toBeGreaterThan(0);

    await openCatalog(page);

    const headers = page.locator('thead th');
    await expect(headers).toHaveText(['Launch', 'Date', 'Location', 'Price', 'Duration', 'Max seats']);

    const rows = page.locator('tbody tr');
    await expect(rows).toHaveCount(visible.length);

    for (const [index, activity] of visible.entries()) {
      const cells = rows.nth(index).locator('td');
      await expect(cells.nth(0), `name of ${activity.id}`).toContainText(activity.name);
      await expect(cells.nth(1), `date of ${activity.id}`).toHaveText(dateLabel(activity.date));
      await expect(cells.nth(2), `location of ${activity.id}`).toHaveText(activity.location);
      expect(parseMoney(await cells.nth(3).innerText()), `price of ${activity.id}`).toBeCloseTo(activity.price, 2);
      await expect(cells.nth(4), `duration of ${activity.id}`).toHaveText(`${activity.duration} min`);
      await expect(cells.nth(5), `max seats of ${activity.id}`).toHaveText(String(activity.maxParticipants));
    }
  });

  test('AC3 - launches with draft, done or cancelled status are excluded', async ({ page, request }) => {
    const activities = await fetchActivities(request);
    const visible = visibleActivities(activities);
    const excluded = excludedActivities(activities);
    expect(excluded.length, 'the live API must expose at least one non-visible launch').toBeGreaterThan(0);

    await openCatalog(page);

    // Only the visible subset is rendered: subtracting the excluded activities from the API list.
    const rows = page.locator('tbody tr');
    await expect(rows).toHaveCount(visible.length);
    expect(visible.length).toBeLessThan(activities.length);

    for (const activity of excluded) {
      await expect(page.locator('tbody'), `excluded launch name "${activity.name}" must not be shown`).not.toContainText(
        activity.name
      );
    }
  });

  test('AC4 - the catalog is ordered by launch date ascending', async ({ page, request }) => {
    const visible = visibleActivities(await fetchActivities(request));
    expect(visible.length).toBeGreaterThan(1, 'at least two launches are needed to prove ordering');

    await openCatalog(page);

    // The rendered order matches the ascending-date contract derived from the API.
    expect(await renderedLaunchIds(page)).toEqual(visible.map((activity) => activity.id));

    // Independently: the date column itself is non-decreasing.
    const labels = await page.locator('tbody tr td:nth-child(2)').allInnerTexts();
    expect(labels).toEqual(visible.map((activity) => dateLabel(activity.date)));
    const timestamps = labels.map(parseDateLabel);
    expect(timestamps).toEqual([...timestamps].sort((left, right) => left - right));
  });

  test('AC7 - selecting a launch opens the detail page for that launch', async ({ page, request }) => {
    const visible = visibleActivities(await fetchActivities(request));
    expect(visible.length).toBeGreaterThan(0, 'the live API must expose at least one visible launch');

    await openCatalog(page);

    const links = page.locator('tbody tr td:first-child a');
    await expect(links).toHaveCount(visible.length);
    for (const [index, activity] of visible.entries()) {
      await expect(links.nth(index)).toHaveAttribute('href', `${CONTEXT}/launches/${activity.id}`);
    }

    // The detail view belongs to feat-launch-detail; this criterion only requires the navigation target.
    const target = visible[0];
    const detailPath = `${CONTEXT}/launches/${target.id}`;
    await Promise.all([
      page.waitForURL((url) => url.pathname === detailPath),
      links.first().click(),
    ]);
    expect(new URL(page.url()).pathname).toBe(detailPath);
  });

  test('AC8 - anonymous visitors see log in and register links and no token leaks into the HTML', async ({ page }) => {
    await openCatalog(page);

    const navigation = page.getByRole('navigation', { name: 'Main navigation' });
    const login = navigation.getByRole('link', { name: 'Log in' });
    const register = navigation.getByRole('link', { name: 'Register' });

    await expect(login).toBeVisible();
    await expect(login).toHaveAttribute('href', `${CONTEXT}/login`);
    await expect(register).toBeVisible();
    await expect(register).toHaveAttribute('href', `${CONTEXT}/register`);

    const html = await page.content();
    expect(html).not.toMatch(/eyJ[A-Za-z0-9_-]{5,}\.[A-Za-z0-9_-]{5,}\.[A-Za-z0-9_-]{5,}/);
    expect(html).not.toContain('auth.token');
    expect(html).not.toContain('Signed in as');
  });
});

test.describe('Launch catalog - empty and error states (controlled API fixture)', () => {
  test.beforeEach(async ({ request }) => {
    await setFixtureMode(request, 'ok');
  });

  test.afterEach(async ({ request }) => {
    await setFixtureMode(request, 'ok');
  });

  test('AC5 - an empty API result shows the empty-state message', async ({ page, request }) => {
    // Arrange: the deployment pointed at the fixture API returns no activities at all.
    await setFixtureMode(request, 'empty');

    // Act
    const response = await page.goto(joinUrl(FIXTURE_APP, '/launches'));

    // Assert
    expect(response!.status()).toBe(200);
    await expect(page.getByRole('heading', { name: 'No launches available' })).toBeVisible();
    await expect(
      page.getByText('There are no launches open for booking right now. Please check back soon.')
    ).toBeVisible();
    await expect(page.locator('tbody tr')).toHaveCount(0);
    await expect(page.getByRole('link', { name: 'Refresh' })).toHaveAttribute(
      'href',
      `${FIXTURE_CONTEXT}/launches`
    );
  });

  test('AC6 - an API failure shows a friendly error and a working retry action', async ({ page, request }) => {
    // Arrange: the fixture API answers 503.
    await setFixtureMode(request, 'error');

    // Act
    const response = await page.goto(joinUrl(FIXTURE_APP, '/launches'));

    // Assert: friendly error, no stack trace, no catalog rows.
    expect(response!.status()).toBe(200);
    const alert = page.getByRole('alert');
    await expect(alert).toBeVisible();
    await expect(alert.getByRole('heading', { name: 'Launch catalog unavailable' })).toBeVisible();
    await expect(alert).toContainText('We could not load the launch catalog right now. Please try again in a moment.');
    await expect(page.locator('tbody tr')).toHaveCount(0);

    const html = await page.content();
    expect(html).not.toContain('java.');
    expect(html).not.toMatch(/ApiException|StackTrace|at com\.astrobookings/);

    const retry = alert.getByRole('link', { name: 'Try again' });
    await expect(retry).toHaveAttribute('href', `${FIXTURE_CONTEXT}/launches`);

    // The retry action re-requests the catalog and recovers once the API is reachable again.
    await setFixtureMode(request, 'ok');
    await retry.click();
    await expect(page.getByRole('alert')).toHaveCount(0);
    await expect(page.getByRole('heading', { name: 'Upcoming rocket launches' })).toBeVisible();
    await expect(page.locator('tbody tr')).toHaveCount(2);
  });
});

test.describe('Launch catalog - navigation shell', () => {
  test('the context root redirects to the catalog', async ({ page, request }) => {
    const response = await request.get(joinUrl(APP, '/'), { maxRedirects: 0 });
    expect([301, 302, 303, 307, 308]).toContain(response.status());
    expect(response.headers()['location']).toContain('/launches');

    await page.goto(joinUrl(APP, '/'));
    expect(new URL(page.url()).pathname).toBe(`${CONTEXT}/launches`);
    await expect(page.getByRole('heading', { name: 'Upcoming rocket launches' })).toBeVisible();
  });

  test('the catalog stylesheet is served by the default servlet', async ({ request }) => {
    const response = await request.get(joinUrl(APP, '/assets/css/main.css'));
    expect(response.status()).toBe(200);
    expect(response.headers()['content-type']).toContain('text/css');
    expect(await response.text()).toContain('.catalog');
  });
});

async function openCatalog(page: Page): Promise<void> {
  const response = await page.goto(joinUrl(APP, '/launches'));
  expect(response, 'the catalog page did not return a response').not.toBeNull();
  expect(response!.status()).toBe(200);
}

async function renderedLaunchIds(page: Page): Promise<string[]> {
  return page.locator('tbody tr td:first-child a').evaluateAll((links) =>
    links.map((link) => {
      const href = link.getAttribute('href') ?? '';
      return href.split('/').filter(Boolean).pop() ?? '';
    })
  );
}

async function setFixtureMode(request: APIRequestContext, mode: 'ok' | 'empty' | 'error'): Promise<void> {
  const response = await request.get(`${e2eConfig.fixtureApiUrl}/control/mode?mode=${mode}`);
  expect(response.ok(), `fixture API mode switch failed: ${response.status()} ${await response.text()}`).toBeTruthy();
}
