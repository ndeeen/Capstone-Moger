from fastapi import APIRouter, Depends, HTTPException
from fastapi.responses import JSONResponse
from datetime import timedelta
from pydantic import BaseModel
from app.database.connection import get_database_connection
from app.utils.password import verify_password
from app.security.auth import create_access_token
from app.config import ACCESS_TOKEN_EXPIRE_MINUTES

router = APIRouter()

class User(BaseModel):
    email: str
    password: str

@router.post("/login", tags=["Login"])
async def login(user: User, connection = Depends(get_database_connection)):
    # Find the user in the database
    query = "SELECT * FROM user WHERE email = %s"
    cursor = connection.cursor()
    cursor.execute(query, (user.email,))
    result = cursor.fetchone()
    statusLogin = False
    
    if result and verify_password(user.password, result[2]):
        statusLogin = True
        access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
        access_token = create_access_token(data={"sub": user.email}, expires_delta=access_token_expires)
        return {"status_login":  str(statusLogin),  "access_token": access_token, "name": result[1]}


    # User not found or password incorrect
    # raise HTTPException(status_code=401, return {"status_login":  str(statusLogin),  "access_token": access_token, "name": result[1]})
    return JSONResponse(status_code=401, content={"status_login":  str(statusLogin),  "access_token": "", "name": ""})