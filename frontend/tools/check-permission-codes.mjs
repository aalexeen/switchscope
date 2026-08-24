#!/usr/bin/env node
// Usage: node tools/check-permission-codes.mjs   (from frontend/)
//
// Holds the frontend's permission codes to the backend's, the way
// `backend/tools/generate_permission_seed.py --check` holds the seed to the annotations. Together
// the two make one chain: @RequiresPermission -> 04-permissions.csv -> configs/permissions.js.
//
// It also checks the other direction, as far as it can be checked mechanically: a route that names
// a table must gate on that table's permission. What stays invisible is a page with no `tableKey`
// at all - there is nothing to compare it against.

import { readFileSync, readdirSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';

import { RESOURCE } from '../src/configs/permissions.js';

const here = dirname(fileURLToPath(import.meta.url));
const SEED = join(here, '../../backend/src/main/resources/db/changelog/csv/04-permissions.csv');
const TABLE_CONFIGS = join(here, '../src/configs/tables');
const ROUTER = join(here, '../src/router/index.js');

const ACTIONS = ['read', 'create', 'update', 'delete'];

const errors = [];
const notes = [];

/** The seed is semicolon-separated with single-quoted values; only two columns matter here. */
function seededResources() {
  const [header, ...rows] = readFileSync(SEED, 'utf8').trim().split('\n');
  const columns = header.split(';');
  const resourceAt = columns.indexOf('resource');
  const actionAt = columns.indexOf('action');
  const unquote = (value) => value.trim().replace(/^'|'$/g, '');

  const byResource = new Map();
  for (const row of rows) {
    const cells = row.split(';');
    const resource = unquote(cells[resourceAt]);
    if (!byResource.has(resource)) {
      byResource.set(resource, new Set());
    }
    byResource.get(resource).add(unquote(cells[actionAt]));
  }
  return byResource;
}

const seeded = seededResources();

// 1. Every code the frontend can build is a code the backend knows.
for (const [key, resource] of Object.entries(RESOURCE)) {
  const actions = seeded.get(resource);
  if (!actions) {
    errors.push(`RESOURCE.${key} = '${resource}' is in no permission the backend seeds`);
    continue;
  }
  const missing = ACTIONS.filter((action) => !actions.has(action));
  if (missing.length > 0) {
    errors.push(`RESOURCE.${key} = '${resource}' has no ${missing.join(', ')} permission seeded`);
  }
}

// 2. Resources the frontend does not name. Not an error: the UI has no page for every one of them.
const named = new Set(Object.values(RESOURCE));
for (const resource of seeded.keys()) {
  if (!named.has(resource)) {
    notes.push(`no page gates '${resource}' - the backend seeds it, the frontend never asks`);
  }
}

// 3. Every table declares its resource, and declares its own rather than a neighbour's. A config
//    that copied the line from the file next to it is the mistake nothing else would show.
for (const file of readdirSync(TABLE_CONFIGS).filter((name) => name.endsWith('.config.js'))) {
  const tableKey = file.replace('.config.js', '');
  const source = readFileSync(join(TABLE_CONFIGS, file), 'utf8');
  const declared = source.match(/permissionResource:\s*RESOURCE\.(\w+)/);
  if (!declared) {
    errors.push(`${file} declares no permissionResource, so its row actions are ungated`);
  } else if (declared[1] !== tableKey) {
    errors.push(`${file} declares RESOURCE.${declared[1]}, which belongs to another table`);
  } else if (!RESOURCE[tableKey]) {
    errors.push(`${file} declares RESOURCE.${tableKey}, which does not exist`);
  }
}

// 4. Every route that names a table gates on that table's read permission. A route that forgot
//    `meta.permission` opens a page whose every request will come back 403; one that names the
//    neighbouring table's resource gates on the wrong answer, which is worse because it works.
const routes = readFileSync(ROUTER, 'utf8').split(/(?=\n\s+path: ")/);
for (const route of routes) {
  const table = route.match(/tableKey:\s*'(\w+)'/);
  if (!table) {
    continue;
  }
  const name = (route.match(/name: "([^"]+)"/) || [, '?'])[1];
  const gate = route.match(/permission:\s*\w+\(RESOURCE\.(\w+)\)/);
  if (!gate) {
    errors.push(`route '${name}' has tableKey '${table[1]}' and no meta.permission`);
  } else if (gate[1] !== table[1]) {
    errors.push(`route '${name}' shows table '${table[1]}' but gates on RESOURCE.${gate[1]}`);
  }
}

for (const note of notes) {
  console.log(`note: ${note}`);
}
for (const error of errors) {
  console.error(`error: ${error}`);
}
const gated = routes.filter((route) => /tableKey:/.test(route)).length;
console.log(
  `${Object.keys(RESOURCE).length} resources named, ${seeded.size} seeded,`
  + ` ${gated} table routes checked, ${errors.length} errors`
);
process.exit(errors.length === 0 ? 0 : 1);
