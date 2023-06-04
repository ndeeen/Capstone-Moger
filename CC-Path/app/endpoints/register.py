from fastapi import APIRouter, Depends, HTTPException
from app.models.user import User
from app.database.connection import get_database_connection
from app.utils.password import get_password_hash

router = APIRouter()

@router.post("/register")
async def register(user: User, connection = Depends(get_database_connection)):
    # Check if the email is already registered
    query = "SELECT * FROM user WHERE email = %s"
    cursor = connection.cursor()
    cursor.execute(query, (user.email,))
    result = cursor.fetchone()
    
    if result:
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Hash the password
    hashed_password = get_password_hash(user.password)
    
    # Insert the new user into the database
    query = "INSERT INTO user (email, name, password) VALUES (%s, %s, %s)"
    cursor.execute(query, (user.email, user.name, hashed_password))
    connection.commit()
    
    return {"message": "User registered successfully"}
