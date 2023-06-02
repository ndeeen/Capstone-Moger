from fastapi import FastAPI, Depends, HTTPException
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from passlib.context import CryptContext
from jose import JWTError, jwt
from datetime import datetime, timedelta
from pydantic import BaseModel
import mysql.connector
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
security = HTTPBearer()

# Configure CORS settings
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Replace with the appropriate origins or ["*"] to allow all origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# MySQL connection configuration
config = {
    "user": "root",
    "password": "password",
    "host": "34.101.247.215",
    "database": "moger"
}

# Create a MySQL connection pool
connection_pool = mysql.connector.pooling.MySQLConnectionPool(pool_name="mypool", pool_size=5, **config)

# User model
class User(BaseModel):
    email: str
    name: str
    password: str

class UserLogin(BaseModel):
    email: str
    password: str

# JWT configuration
SECRET_KEY = "your-secret-key"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30

# Password hashing
pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# Generate hashed password
def get_password_hash(password):
    return pwd_context.hash(password)

# Verify password
def verify_password(plain_password, hashed_password):
    return pwd_context.verify(plain_password, hashed_password)

# Generate access token
def create_access_token(data: dict, expires_delta: timedelta):
    to_encode = data.copy()
    expire = datetime.utcnow() + expires_delta
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt

# Decode access token
def decode_access_token(token: str):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")

# Register endpoint
@app.post("/register")
async def register(user: User):
    connection = connection_pool.get_connection()
    cursor = connection.cursor()
    
    # Check if the email is already registered
    query = "SELECT * FROM user WHERE email = %s"
    cursor.execute(query, (user.email,))
    result = cursor.fetchone()
    
    if result:
        cursor.close()
        connection.close()
        raise HTTPException(status_code=400, detail="Email already registered")
    
    # Hash the password
    hashed_password = get_password_hash(user.password)
    
    # Insert the new user into the database
    query = "INSERT INTO user (email, name, password) VALUES (%s, %s, %s)"
    cursor.execute(query, (user.email, user.name, hashed_password))
    connection.commit()
    
    cursor.close()
    connection.close()
    
    return {"message": "User registered successfully"}

# Login endpoint
@app.post("/login")
async def login(user: UserLogin):
    connection = connection_pool.get_connection()
    cursor = connection.cursor()
    
    # Find the user in the database
    query = "SELECT * FROM user WHERE email = %s"
    cursor.execute(query, (user.email,))
    result = cursor.fetchone()
    
    if result and verify_password(user.password, result[2]):
        access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
        access_token = create_access_token(data={"sub": user.email}, expires_delta=access_token_expires) 
        cursor.close()
        connection.close()
        
        return {"access_token": access_token, "token_type": "bearer"}
    
    cursor.close()
    connection.close()
    
    # User not found or password incorrect
    raise HTTPException(status_code=401, detail="Invalid email or password")

# Protected endpoint
@app.get("/protected")
async def protected(credentials: HTTPAuthorizationCredentials = Depends(security)):
    token = credentials.credentials
    payload = decode_access_token(token)
    return {"message": f"Hello, {payload['sub']}! This is a protected route."}