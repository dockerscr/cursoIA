import { expect, type APIRequestContext } from '@playwright/test';
import { e2eConfig } from '../../playwright.config';

/** Subset of the ActivityBookings activity payload used by the catalog. */
export type Activity = {
  id: string;
  name: string;
  price: number;
  date: string;
  duration: number;
  location: string;
  maxParticipants: number;
  status: string;
};

const VISIBLE_STATUSES = ['published', 'confirmed'];

/** Reads every activity from the live API; the catalog must show the visible subset. */
export async function fetchActivities(request: APIRequestContext): Promise<Activity[]> {
  const response = await request.get(`${e2eConfig.apiBaseUrl}/activities`);
  expect(
    response.ok(),
    `GET ${e2eConfig.apiBaseUrl}/activities failed with HTTP ${response.status()}`
  ).toBeTruthy();
  return (await response.json()) as Activity[];
}

/** Applies the catalog visibility rule and the documented ordering. */
export function visibleActivities(activities: Activity[]): Activity[] {
  return activities
    .filter((activity) => VISIBLE_STATUSES.includes(normalizeStatus(activity.status)))
    .sort(compareActivities);
}

export function excludedActivities(activities: Activity[]): Activity[] {
  return activities.filter((activity) => !VISIBLE_STATUSES.includes(normalizeStatus(activity.status)));
}

export function normalizeStatus(status: string | null | undefined): string {
  return (status ?? '').trim().toLowerCase();
}

/** Mirrors the server contract: only the first 10 characters are significant. */
export function dateKey(raw: string | null | undefined): string {
  return (raw ?? '').trim().slice(0, 10);
}

function compareActivities(left: Activity, right: Activity): number {
  const leftDate = dateKey(left.date);
  const rightDate = dateKey(right.date);
  if (leftDate !== rightDate) {
    if (!leftDate) return 1;
    if (!rightDate) return -1;
    return leftDate < rightDate ? -1 : 1;
  }
  const leftName = (left.name ?? '').toLowerCase();
  const rightName = (right.name ?? '').toLowerCase();
  if (leftName !== rightName) {
    return leftName < rightName ? -1 : 1;
  }
  return (left.id ?? '').localeCompare(right.id ?? '');
}

/** Formats a launch date the way the catalog renders it, e.g. "25 Aug 2026". */
export function dateLabel(raw: string | null | undefined): string {
  const key = dateKey(raw);
  if (!/^\d{4}-\d{2}-\d{2}$/.test(key)) {
    return 'Date to be confirmed';
  }
  const date = new Date(`${key}T00:00:00Z`);
  return new Intl.DateTimeFormat('en-GB', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    timeZone: 'UTC',
  }).format(date);
}

/** Parses a rendered date label back to a sortable timestamp. */
export function parseDateLabel(label: string): number {
  const parsed = Date.parse(label.trim());
  expect(parsed, `unparseable date label: "${label}"`).not.toBeNaN();
  return parsed;
}

/** Converts a rendered money cell into a number without assuming a locale. */
export function parseMoney(text: string): number {
  const numeric = text.replace(/[^\d.,-]/g, '');
  if (!numeric) return Number.NaN;
  const lastComma = numeric.lastIndexOf(',');
  const lastDot = numeric.lastIndexOf('.');
  let normalized = numeric;
  if (lastComma >= 0 && lastComma > lastDot) {
    normalized = numeric.replace(/\./g, '').replace(',', '.');
  } else {
    normalized = numeric.replace(/,/g, '');
  }
  return Number(normalized);
}

export function contextPathOf(baseUrl: string): string {
  return new URL(baseUrl).pathname.replace(/\/+$/, '');
}

export function joinUrl(baseUrl: string, path: string): string {
  return `${baseUrl.replace(/\/+$/, '')}${path}`;
}
