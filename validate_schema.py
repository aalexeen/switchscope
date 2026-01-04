#!/usr/bin/env python3
"""
Database Schema Validation Script
Compares Liquibase YAML definitions with actual PostgreSQL database schema
"""

import psycopg2
import yaml
import os
import sys
from collections import defaultdict
from typing import Dict, List, Tuple, Set

# Load configuration from .local.props
def load_config(props_file):
    config = {}
    # Resolve file path relative to the script location
    script_dir = os.path.dirname(os.path.abspath(__file__))
    props_path = os.path.join(script_dir, props_file)
    
    if not os.path.exists(props_path):
        print(f"Error: {props_path} not found. Please create it with database credentials.")
        sys.exit(1)
        
    with open(props_path, 'r') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            if '=' in line:
                key, value = line.split('=', 1)
                config[key.strip()] = value.strip()
    return config

# Load config
_config = load_config('.local.props')

# Database connection parameters
DB_CONFIG = {
    'host': _config.get('host'),
    'port': _config.get('port'),
    'database': _config.get('database'),
    'user': _config.get('user'),
    'password': _config.get('password'),
    'options': _config.get('options')
}

# Directory containing Liquibase YAML files
INIT_DIR = _config.get('init_dir')

# PostgreSQL type mappings
TYPE_MAPPINGS = {
    'UUID': ['uuid'],
    'VARCHAR': ['character varying'],
    'TEXT': ['text'],
    'BOOLEAN': ['boolean'],
    'TIMESTAMP': ['timestamp without time zone', 'timestamp'],
    'INTEGER': ['integer', 'int4'],
    'BIGINT': ['bigint', 'int8'],
    'SMALLINT': ['smallint', 'int2'],
    'NUMERIC': ['numeric'],
    'DECIMAL': ['numeric'],
    'DATE': ['date'],
    'TIME': ['time without time zone'],
    'REAL': ['real'],
    'DOUBLE PRECISION': ['double precision'],
    'BYTEA': ['bytea'],
}

class ValidationResult:
    def __init__(self):
        self.tables_validated = 0
        self.tables_passing = 0
        self.tables_with_issues = 0
        self.issues = defaultdict(list)
        self.critical_count = 0
        self.warning_count = 0
        self.info_count = 0

    def add_issue(self, table: str, severity: str, description: str, expected: str = '', actual: str = '', remediation: str = ''):
        issue = {
            'severity': severity,
            'description': description,
            'expected': expected,
            'actual': actual,
            'remediation': remediation
        }
        self.issues[table].append(issue)

        if severity == 'CRITICAL':
            self.critical_count += 1
        elif severity == 'WARNING':
            self.warning_count += 1
        else:
            self.info_count += 1

def normalize_type(db_type: str, liquibase_type: str) -> bool:
    """Check if database type matches Liquibase type"""
    db_type = db_type.lower()
    liquibase_base = liquibase_type.upper().split('(')[0]

    if liquibase_base in TYPE_MAPPINGS:
        return db_type.split('(')[0] in TYPE_MAPPINGS[liquibase_base]

    # Direct match
    return db_type.startswith(liquibase_type.lower())

def parse_liquibase_yaml(file_path: str) -> List[Dict]:
    """Parse a Liquibase YAML file and extract table definitions"""
    with open(file_path, 'r') as f:
        data = yaml.safe_load(f)

    tables = []
    if 'databaseChangeLog' in data:
        for change_set in data['databaseChangeLog']:
            if 'changeSet' in change_set:
                cs = change_set['changeSet']
                if 'changes' in cs:
                    for change in cs['changes']:
                        if 'createTable' in change:
                            tables.append({
                                'file': os.path.basename(file_path),
                                'changeSetId': cs.get('id'),
                                'table': change['createTable']
                            })
    return tables

def get_all_table_definitions() -> Dict[str, Dict]:
    """Read all YAML files and build table definitions"""
    table_defs = {}

    for filename in sorted(os.listdir(INIT_DIR)):
        if filename.endswith('.yaml'):
            file_path = os.path.join(INIT_DIR, filename)
            tables = parse_liquibase_yaml(file_path)

            for table_info in tables:
                table_def = table_info['table']
                table_name = table_def['tableName']
                table_defs[table_name] = {
                    'source_file': table_info['file'],
                    'columns': {},
                    'constraints': [],
                    'indexes': [],
                    'foreign_keys': []
                }

                # Extract columns
                for col in table_def.get('columns', []):
                    col_def = col['column']
                    col_name = col_def['name']
                    col_type = col_def.get('type', 'UNKNOWN')
                    constraints = col_def.get('constraints', {})

                    table_defs[table_name]['columns'][col_name] = {
                        'type': col_type,
                        'nullable': not constraints.get('nullable', True),
                        'primary_key': constraints.get('primaryKey', False),
                        'unique': constraints.get('unique', False),
                        'default': col_def.get('defaultValueComputed') or col_def.get('defaultValueBoolean') or col_def.get('defaultValue') or col_def.get('defaultValueNumeric')
                    }

    # Parse additional changesets for constraints, indexes, FK
    for filename in sorted(os.listdir(INIT_DIR)):
        if filename.endswith('.yaml'):
            file_path = os.path.join(INIT_DIR, filename)
            with open(file_path, 'r') as f:
                data = yaml.safe_load(f)

            if 'databaseChangeLog' in data:
                for change_set in data['databaseChangeLog']:
                    if 'changeSet' in change_set:
                        cs = change_set['changeSet']
                        if 'changes' in cs:
                            for change in cs['changes']:
                                # Unique constraints
                                if 'addUniqueConstraint' in change:
                                    uc = change['addUniqueConstraint']
                                    table_name = uc['tableName']
                                    if table_name in table_defs:
                                        table_defs[table_name]['constraints'].append({
                                            'type': 'UNIQUE',
                                            'name': uc.get('constraintName'),
                                            'columns': uc.get('columnNames')
                                        })

                                # Foreign keys
                                if 'addForeignKeyConstraint' in change:
                                    fk = change['addForeignKeyConstraint']
                                    table_name = fk['baseTableName']
                                    if table_name in table_defs:
                                        table_defs[table_name]['foreign_keys'].append({
                                            'name': fk.get('constraintName'),
                                            'columns': fk.get('baseColumnNames'),
                                            'ref_table': fk.get('referencedTableName'),
                                            'ref_columns': fk.get('referencedColumnNames'),
                                            'on_delete': fk.get('onDelete')
                                        })

                                # Indexes
                                if 'createIndex' in change:
                                    idx = change['createIndex']
                                    table_name = idx['tableName']
                                    if table_name in table_defs:
                                        cols = [c['column']['name'] for c in idx.get('columns', [])]
                                        table_defs[table_name]['indexes'].append({
                                            'name': idx.get('indexName'),
                                            'columns': cols
                                        })

                                # Primary keys
                                if 'addPrimaryKey' in change:
                                    pk = change['addPrimaryKey']
                                    table_name = pk['tableName']
                                    if table_name in table_defs:
                                        table_defs[table_name]['constraints'].append({
                                            'type': 'PRIMARY KEY',
                                            'name': pk.get('constraintName'),
                                            'columns': pk.get('columnNames')
                                        })

    return table_defs

def get_database_schema(conn) -> Dict[str, Dict]:
    """Query PostgreSQL to get actual database schema"""
    cursor = conn.cursor()
    db_schema = {}

    # Get all tables
    cursor.execute("""
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'switchscope'
        AND table_type = 'BASE TABLE'
        AND table_name NOT LIKE 'databasechange%'
        ORDER BY table_name
    """)

    tables = [row[0] for row in cursor.fetchall()]

    for table in tables:
        db_schema[table] = {
            'columns': {},
            'constraints': [],
            'indexes': [],
            'foreign_keys': []
        }

        # Get columns
        cursor.execute("""
            SELECT
                column_name,
                data_type,
                character_maximum_length,
                is_nullable,
                column_default
            FROM information_schema.columns
            WHERE table_schema = 'switchscope'
            AND table_name = %s
            ORDER BY ordinal_position
        """, (table,))

        for row in cursor.fetchall():
            col_name, data_type, max_len, is_nullable, col_default = row

            # Format type with length
            if max_len and data_type == 'character varying':
                full_type = f"{data_type}({max_len})"
            else:
                full_type = data_type

            db_schema[table]['columns'][col_name] = {
                'type': full_type,
                'nullable': is_nullable == 'YES',
                'default': col_default
            }

        # Get constraints
        cursor.execute("""
            SELECT
                tc.constraint_name,
                tc.constraint_type,
                string_agg(kcu.column_name, ', ' ORDER BY kcu.ordinal_position) as columns
            FROM information_schema.table_constraints tc
            JOIN information_schema.key_column_usage kcu
                ON tc.constraint_name = kcu.constraint_name
                AND tc.table_schema = kcu.table_schema
            WHERE tc.table_schema = 'switchscope'
            AND tc.table_name = %s
            AND tc.constraint_type IN ('PRIMARY KEY', 'UNIQUE')
            GROUP BY tc.constraint_name, tc.constraint_type
        """, (table,))

        for row in cursor.fetchall():
            constraint_name, constraint_type, columns = row
            db_schema[table]['constraints'].append({
                'name': constraint_name,
                'type': constraint_type,
                'columns': columns
            })

        # Get foreign keys
        cursor.execute("""
            SELECT
                tc.constraint_name,
                kcu.column_name,
                ccu.table_name AS foreign_table_name,
                ccu.column_name AS foreign_column_name,
                rc.delete_rule
            FROM information_schema.table_constraints AS tc
            JOIN information_schema.key_column_usage AS kcu
                ON tc.constraint_name = kcu.constraint_name
                AND tc.table_schema = kcu.table_schema
            JOIN information_schema.constraint_column_usage AS ccu
                ON ccu.constraint_name = tc.constraint_name
                AND ccu.table_schema = tc.table_schema
            JOIN information_schema.referential_constraints AS rc
                ON rc.constraint_name = tc.constraint_name
                AND rc.constraint_schema = tc.table_schema
            WHERE tc.constraint_type = 'FOREIGN KEY'
            AND tc.table_schema = 'switchscope'
            AND tc.table_name = %s
        """, (table,))

        for row in cursor.fetchall():
            fk_name, col_name, ref_table, ref_col, delete_rule = row
            db_schema[table]['foreign_keys'].append({
                'name': fk_name,
                'column': col_name,
                'ref_table': ref_table,
                'ref_column': ref_col,
                'delete_rule': delete_rule
            })

        # Get indexes
        cursor.execute("""
            SELECT
                i.relname as index_name,
                array_agg(a.attname ORDER BY k.ordinality) as columns
            FROM pg_class t
            JOIN pg_index ix ON t.oid = ix.indrelid
            JOIN pg_class i ON i.oid = ix.indexrelid
            JOIN pg_namespace n ON n.oid = t.relnamespace
            CROSS JOIN LATERAL unnest(ix.indkey) WITH ORDINALITY AS k(attnum, ordinality)
            JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = k.attnum
            WHERE n.nspname = 'switchscope'
            AND t.relname = %s
            AND NOT ix.indisprimary
            GROUP BY i.relname
        """, (table,))

        for row in cursor.fetchall():
            index_name, columns = row
            db_schema[table]['indexes'].append({
                'name': index_name,
                'columns': columns
            })

    cursor.close()
    return db_schema

def validate_schema(liquibase_defs: Dict, db_schema: Dict) -> ValidationResult:
    """Compare Liquibase definitions with database schema"""
    result = ValidationResult()

    # Get all table names from both sources
    liquibase_tables = set(liquibase_defs.keys())
    db_tables = set(db_schema.keys())

    # Check for missing tables
    missing_in_db = liquibase_tables - db_tables
    missing_in_liquibase = db_tables - liquibase_tables

    for table in missing_in_db:
        result.add_issue(
            table,
            'CRITICAL',
            f'Table defined in Liquibase but missing in database',
            expected=f'Table {table} should exist',
            actual='Table does not exist',
            remediation=f'Run Liquibase migration from {liquibase_defs[table]["source_file"]}'
        )

    for table in missing_in_liquibase:
        result.add_issue(
            table,
            'WARNING',
            f'Table exists in database but not defined in Liquibase YAML files',
            expected='No definition',
            actual=f'Table {table} exists',
            remediation='Either add Liquibase definition or drop table if obsolete'
        )

    # Validate tables that exist in both
    common_tables = liquibase_tables & db_tables
    result.tables_validated = len(common_tables) + len(missing_in_db) + len(missing_in_liquibase)

    for table in sorted(common_tables):
        table_issues_before = len(result.issues[table])
        liq_def = liquibase_defs[table]
        db_def = db_schema[table]

        # Validate columns
        liq_columns = set(liq_def['columns'].keys())
        db_columns = set(db_def['columns'].keys())

        # Missing columns
        for col in liq_columns - db_columns:
            result.add_issue(
                table,
                'CRITICAL',
                f'Column "{col}" defined in Liquibase but missing in database',
                expected=f'{col}: {liq_def["columns"][col]["type"]}',
                actual='Column does not exist',
                remediation=f'ALTER TABLE switchscope.{table} ADD COLUMN {col} {liq_def["columns"][col]["type"]};'
            )

        # Extra columns
        for col in db_columns - liq_columns:
            result.add_issue(
                table,
                'WARNING',
                f'Column "{col}" exists in database but not defined in Liquibase',
                expected='Column not defined',
                actual=f'{col}: {db_def["columns"][col]["type"]}',
                remediation=f'Add column definition to Liquibase or remove with: ALTER TABLE switchscope.{table} DROP COLUMN {col};'
            )

        # Validate common columns
        for col in liq_columns & db_columns:
            liq_col = liq_def['columns'][col]
            db_col = db_def['columns'][col]

            # Type validation
            if not normalize_type(db_col['type'], liq_col['type']):
                result.add_issue(
                    table,
                    'CRITICAL',
                    f'Column "{col}" has incompatible type',
                    expected=liq_col['type'],
                    actual=db_col['type'],
                    remediation=f'ALTER TABLE switchscope.{table} ALTER COLUMN {col} TYPE {liq_col["type"]};'
                )

            # Nullable validation
            if liq_col['nullable'] != (not db_col['nullable']):
                severity = 'WARNING'
                if liq_col['nullable']:  # Should be NOT NULL but is nullable
                    severity = 'WARNING'
                    expected_null = 'NOT NULL'
                    actual_null = 'NULL'
                    fix = f'ALTER TABLE switchscope.{table} ALTER COLUMN {col} SET NOT NULL;'
                else:
                    expected_null = 'NULL'
                    actual_null = 'NOT NULL'
                    fix = f'ALTER TABLE switchscope.{table} ALTER COLUMN {col} DROP NOT NULL;'

                result.add_issue(
                    table,
                    severity,
                    f'Column "{col}" has different nullability constraint',
                    expected=expected_null,
                    actual=actual_null,
                    remediation=fix
                )

        # Track if table has issues
        if len(result.issues[table]) > table_issues_before:
            result.tables_with_issues += 1
        else:
            result.tables_passing += 1

    return result

def print_report(result: ValidationResult):
    """Print formatted validation report"""
    print("=" * 80)
    print("DATABASE SCHEMA VALIDATION REPORT")
    print("=" * 80)
    print()
    print("=== VALIDATION SUMMARY ===")
    print(f"Tables Validated: {result.tables_validated}")
    print(f"Tables Passing: {result.tables_passing}")
    print(f"Tables with Issues: {result.tables_with_issues}")
    print(f"Total Issues Found: {result.critical_count + result.warning_count + result.info_count} "
          f"({result.critical_count} critical, {result.warning_count} warnings, {result.info_count} informational)")
    print()

    if not result.issues:
        print("=== ALL TABLES PASS VALIDATION ===")
        print("Database schema perfectly matches Liquibase definitions!")
        return

    print("=== DETAILED FINDINGS ===")
    print()

    # Print tables with issues
    for table in sorted(result.issues.keys()):
        issues = result.issues[table]
        if issues:
            has_critical = any(i['severity'] == 'CRITICAL' for i in issues)
            status = 'FAIL' if has_critical else 'WARN'

            print(f"Table: {table}")
            print(f"Status: {status}")
            print()
            print("Issues:")

            for idx, issue in enumerate(issues, 1):
                print(f"{idx}. [{issue['severity']}] {issue['description']}")
                if issue['expected']:
                    print(f"   Expected: {issue['expected']}")
                if issue['actual']:
                    print(f"   Actual: {issue['actual']}")
                if issue['remediation']:
                    print(f"   Remediation: {issue['remediation']}")
                print()

            print("-" * 80)
            print()

    # Print recommendations
    print("=== RECOMMENDATIONS ===")
    print()

    critical_tables = [t for t, issues in result.issues.items()
                      if any(i['severity'] == 'CRITICAL' for i in issues)]

    if critical_tables:
        print("PRIORITY 1 - Critical Issues (Schema Mismatch):")
        for table in sorted(critical_tables):
            critical_issues = [i for i in result.issues[table] if i['severity'] == 'CRITICAL']
            print(f"  - {table}: {len(critical_issues)} critical issue(s)")
        print()
        print("  Action: Review and apply Liquibase migrations or manually fix schema discrepancies")
        print()

    warning_tables = [t for t, issues in result.issues.items()
                     if any(i['severity'] == 'WARNING' for i in issues) and t not in critical_tables]

    if warning_tables:
        print("PRIORITY 2 - Warnings (Minor Discrepancies):")
        for table in sorted(warning_tables):
            warning_issues = [i for i in result.issues[table] if i['severity'] == 'WARNING']
            print(f"  - {table}: {len(warning_issues)} warning(s)")
        print()
        print("  Action: Review and update Liquibase definitions or adjust database as needed")
        print()

def main():
    print("Starting database schema validation...")
    print()

    # Connect to database
    try:
        conn = psycopg2.connect(**DB_CONFIG)
        print(f"Connected to database: {DB_CONFIG['database']}")
    except Exception as e:
        print(f"ERROR: Failed to connect to database: {e}")
        sys.exit(1)

    try:
        # Load Liquibase definitions
        print("Parsing Liquibase YAML files...")
        liquibase_defs = get_all_table_definitions()
        print(f"Found {len(liquibase_defs)} table definitions in Liquibase YAML files")
        print()

        # Load database schema
        print("Querying database schema...")
        db_schema = get_database_schema(conn)
        print(f"Found {len(db_schema)} tables in database")
        print()

        # Validate
        print("Performing validation...")
        result = validate_schema(liquibase_defs, db_schema)
        print()

        # Print report
        print_report(result)

        # Return exit code based on results
        if result.critical_count > 0:
            sys.exit(1)
        elif result.warning_count > 0:
            sys.exit(2)
        else:
            sys.exit(0)

    finally:
        conn.close()

if __name__ == '__main__':
    main()
