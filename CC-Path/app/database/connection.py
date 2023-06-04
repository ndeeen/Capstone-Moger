import mysql.connector
from mysql.connector import pooling
from app.config import DATABASE_CONFIG

# Create a MySQL connection pool
connection_pool = pooling.MySQLConnectionPool(pool_name="mypool", pool_size=5, **DATABASE_CONFIG)

# Dependency to get a database connection
def get_database_connection():
    connection = connection_pool.get_connection()
    try:
        yield connection
    finally:
        connection.close()
