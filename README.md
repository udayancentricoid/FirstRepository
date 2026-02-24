# First Repository

## Purpose of DBMS_SQL.PARSE and DBMS_SQL.DESCRIBE_COLUMNS

The following Oracle PL/SQL code snippet uses the `DBMS_SQL` package to work with dynamic SQL queries at runtime:

```sql
DBMS_SQL.PARSE(v_cursor_id, p_query_text, DBMS_SQL.NATIVE);
DBMS_SQL.DESCRIBE_COLUMNS(v_cursor_id, v_col_count, v_desc_tab);
```

### Line-by-line explanation

**`DBMS_SQL.PARSE(v_cursor_id, p_query_text, DBMS_SQL.NATIVE)`**

- Opens a cursor identified by `v_cursor_id` and parses the SQL statement stored in `p_query_text`.
- `DBMS_SQL.NATIVE` tells Oracle to process the statement using the native SQL dialect of the database.
- After this call the query has been validated and compiled internally, but has not yet been executed.
- This is the first required step before you can bind variables, execute, or describe the query.

**`DBMS_SQL.DESCRIBE_COLUMNS(v_cursor_id, v_col_count, v_desc_tab)`**

- Inspects the parsed (but not yet executed) cursor to retrieve metadata about the columns in the SELECT list.
- `v_col_count` is populated with the total number of columns the query returns.
- `v_desc_tab` is populated with a `DBMS_SQL.DESC_TAB` collection where each element describes one column (name, data type, length, precision, scale, nullability, etc.).
- This is commonly used in dynamic SQL scenarios where the shape of the result set is not known at compile time, allowing the program to loop over `v_desc_tab` and define/fetch each column dynamically.

### Summary

Together these two calls **parse a dynamic SQL query** and then **introspect its column structure**, enabling code to process an arbitrary SELECT statement whose columns are only known at runtime.
