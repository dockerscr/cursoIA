import { createServer } from 'node:http';

/**
 * Minimal ActivityBookings API fixture.
 *
 * The live API only exposes fixed data, so the empty and error catalog states cannot be
 * produced on demand. A second deployment of the WAR is pointed at this server through
 * ASTRO_API_BASE_URL; every other acceptance test runs against the real API.
 *
 * Modes:
 *  - ok:      returns a mixed dataset (published, confirmed, draft) with mixed date formats
 *  - empty:   returns an empty array
 *  - error:   returns HTTP 503
 *
 * Control endpoint: GET /control/mode?mode=ok|empty|error
 */
const port = Number(process.env.FIXTURE_API_PORT ?? '3901');

let mode = 'ok';

const activities = [
  {
    id: 'fixture-draft',
    name: 'Draft Fixture Launch',
    slug: 'draft-fixture-launch',
    price: 10,
    date: '2026-01-01',
    duration: 30,
    location: 'Nowhere',
    minParticipants: 1,
    maxParticipants: 5,
    status: 'draft',
    userId: 'user-fixture',
    createdAt: '2026-01-01T00:00:00.000Z',
  },
  {
    id: 'fixture-confirmed',
    name: 'Confirmed Fixture Launch',
    slug: 'confirmed-fixture-launch',
    price: 120.5,
    date: '2026-09-10T08:00:00.000Z',
    duration: 90,
    location: 'Low Earth Orbit',
    minParticipants: 1,
    maxParticipants: 9,
    status: 'confirmed',
    userId: 'user-fixture',
    createdAt: '2026-01-02T00:00:00.000Z',
  },
  {
    id: 'fixture-published',
    name: 'Published Fixture Launch',
    slug: 'published-fixture-launch',
    price: 99,
    date: '2026-07-04',
    duration: 60,
    location: 'Moon',
    minParticipants: 2,
    maxParticipants: 7,
    status: 'published',
    userId: 'user-fixture',
    createdAt: '2026-01-03T00:00:00.000Z',
  },
];

const server = createServer((req, res) => {
  const url = new URL(req.url ?? '/', `http://127.0.0.1:${port}`);

  if (url.pathname === '/health') {
    sendJson(res, 200, { status: 'ok', mode });
    return;
  }

  if (url.pathname === '/control/mode') {
    const requested = url.searchParams.get('mode') ?? 'ok';
    if (!['ok', 'empty', 'error'].includes(requested)) {
      sendJson(res, 400, { error: `unknown mode: ${requested}` });
      return;
    }
    mode = requested;
    sendJson(res, 200, { mode });
    return;
  }

  if (url.pathname === '/activities') {
    if (mode === 'error') {
      sendJson(res, 503, { error: 'fixture failure' });
      return;
    }
    if (mode === 'empty') {
      sendJson(res, 200, []);
      return;
    }
    sendJson(res, 200, activities);
    return;
  }

  sendJson(res, 404, { error: 'not found' });
});

function sendJson(res, status, body) {
  const payload = JSON.stringify(body);
  res.writeHead(status, { 'Content-Type': 'application/json; charset=utf-8' });
  res.end(payload);
}

server.listen(port, '127.0.0.1', () => {
  console.log(`[fixture-api] listening on http://127.0.0.1:${port} (mode=${mode})`);
});

for (const signal of ['SIGINT', 'SIGTERM']) {
  process.on(signal, () => {
    server.close(() => process.exit(0));
  });
}
