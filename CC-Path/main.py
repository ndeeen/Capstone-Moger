from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.endpoints import login, register, transaction, wallet

app = FastAPI(title="Moger API", description="API yang berisi segala kebutuhan aplikasi Moger")

@app.get("/", tags=["Root"])
async def welcome_text():
	return {"Selamat datang di Moger API"}


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
app.include_router(transaction.router)  
app.include_router(wallet.router)