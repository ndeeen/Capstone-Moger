from app.database.connection import get_database_connection, connection_pool

# Dependency to get a database connection
def get_database_connection():
    connection = connection_pool.get_connection()
    try:
        yield connection
    finally:
        connection.close()
