#!/usr/bin/env python3
"""Regenerate the permission seed CSV from the annotations in the controllers.

The seed has to list exactly the codes @RequiresPermission asks for: a code the table lacks makes
an operation ungrantable, a code no endpoint uses is a dead configuration row, and PermissionRegistry
reports both on startup. Deriving the CSV from the same annotations keeps that report quiet for the
right reason rather than by coincidence.

Static parsing is an approximation of what Spring actually maps - the registry, which reads
RequestMappingHandlerMapping, remains the authority. Run this, then start the application and check
the audit report.

Usage: python3 tools/generate_permission_seed.py [--check]
"""
import os
import re
import sys

WEB = 'src/main/java/net/switchscope/web'
CSV = 'src/main/resources/db/changelog/csv/04-permissions.csv'
UUID_TEMPLATE = '01932f00-0090-7000-8000-%012d'

# Actions AbstractCrudController declares once for every subclass that adds no mappings of its own.
INHERITED_ACTIONS = ['read', 'create', 'update', 'delete']

ACTION_VERB = {'read': 'View', 'create': 'Create', 'update': 'Update', 'delete': 'Delete'}


def human(resource):
    """catalog.component-type -> Component type; component -> Component."""
    leaf = resource.rsplit('.', 1)[-1]
    words = leaf.replace('-', ' ')
    return words[0].upper() + words[1:]


def scan():
    """Map resource -> ordered actions, from @PermissionResource and @RequiresPermission."""
    found = {}
    for root, _, files in os.walk(WEB):
        for name in sorted(files):
            if not name.endswith('Controller.java'):
                continue
            text = open(os.path.join(root, name)).read()
            resource = re.search(r'@PermissionResource\("([^"]+)"\)', text)
            if not resource:
                continue
            actions = re.findall(r'@RequiresPermission\("([^"]+)"\)', text)
            if not actions:
                actions = INHERITED_ACTIONS
            ordered = [a for a in INHERITED_ACTIONS if a in actions]
            ordered += [a for a in actions if a not in ordered]
            found[resource.group(1)] = ordered
    return found


def rows(found):
    out = []
    index = 0
    for resource in sorted(found):
        domain = resource.split('.', 1)[0]
        label = human(resource)
        for action in found[resource]:
            index += 1
            code = '%s:%s' % (resource, action)
            display = '%s: %s' % (label, action)
            description = '%s access to %s.' % (ACTION_VERB.get(action, action.capitalize()),
                                                label[0].lower() + label[1:])
            out.append([UUID_TEMPLATE % index, display, description, code, display,
                        'true', str(index), domain, resource, action])
    return out


def render(found):
    header = ('id;name;description;code;display_name;is_active;sort_order;'
              'domain;resource;action')
    lines = [header]
    for row in rows(found):
        quoted = ["'%s'" % v if i in (0, 1, 2, 3, 4, 7, 8, 9) else v
                  for i, v in enumerate(row)]
        for value in row:
            if ';' in value or "'" in value:
                raise SystemExit('value %r breaks the ;-separated, quote-delimited CSV' % value)
        lines.append(';'.join(quoted))
    return '\n'.join(lines) + '\n'


def main():
    found = scan()
    content = render(found)
    if '--check' in sys.argv:
        current = open(CSV).read() if os.path.exists(CSV) else ''
        if current != content:
            print('%s is out of date; rerun without --check' % CSV)
            return 1
        print('%s matches the annotations (%d permissions)' % (CSV, content.count('\n') - 1))
        return 0
    open(CSV, 'w').write(content)
    print('wrote %s: %d permissions across %d resources'
          % (CSV, content.count('\n') - 1, len(found)))
    return 0


if __name__ == '__main__':
    sys.exit(main())
