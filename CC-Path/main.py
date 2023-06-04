from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.endpoints import login, register

app = FastAPI()

# Configure CORS settings
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Replace with the appropriate origins or ["*"] to allow all origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(login.router)
app.include_router(register.router)