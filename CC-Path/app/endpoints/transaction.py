from pydantic import BaseModel
from fastapi import APIRouter, Depends, HTTPException, Path
from app.database.connection import get_database_connection
from datetime import datetime

router = APIRouter()

class TransactionCreate(BaseModel):
    partyName: str
    createdBy: str
    kind: str
    fromWallet: str
    toWallet: str = None
    amount: float
    stamp: datetime
    IncomeOutcomeKind: str = None

class GetTransaction(BaseModel):
    partyname: str
    month: int
    year: int

class TransactionDay(BaseModel):
    day: int
    transactions: list
    expense: int

@router.post("/addTransaction", tags=["Transactions"])
async def add_transaction(transaction: TransactionCreate, connection=Depends(get_database_connection)):
    query = "INSERT INTO transactions (partyName, createdBy, kind, fromWallet, toWallet, amount, stamp, IncomeOutcomeKind) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
    values = (
        transaction.partyName,
        transaction.createdBy,
        transaction.kind,
        transaction.fromWallet,
        transaction.toWallet,
        transaction.amount,
        transaction.stamp,
        transaction.IncomeOutcomeKind
    )
    cursor = connection.cursor()
    cursor.execute(query, values)
    connection.commit()

    # Update wallet balances based on transaction kind
    if transaction.kind == "transfer":
        update_wallet_balance(connection, transaction.fromWallet, -transaction.amount, partyName=transaction.partyName)
        update_wallet_balance(connection, transaction.toWallet, transaction.amount, partyName=transaction.partyName)
    elif transaction.kind == "outcome":
        update_wallet_balance(connection, transaction.fromWallet, -transaction.amount, partyName=transaction.partyName)
    elif transaction.kind == "balance":
        update_wallet_balance(connection, transaction.fromWallet, transaction.amount, partyName=transaction.partyName)

    cursor.close()
    return {"message": "Transaction added successfully"}

@router.get("/getTransactions/{partyName}/{month}/{year}", tags=["Transactions"])
async def get_transactions(
    partyName: str = Path(..., description="Party Name"),
    month: int = Path(..., description="Month"),
    year: int = Path(..., description="Year"),
    connection=Depends(get_database_connection)
):
    query = """
        SELECT *
        FROM transactions
        WHERE partyName = %s
            AND MONTH(stamp) = %s
            AND YEAR(stamp) = %s
    """
    cursor = connection.cursor(dictionary=True)
    cursor.execute(query, (partyName, month, year))
    transactions = cursor.fetchall()
    cursor.close()
    return transactions


@router.get("/getIncomeOutcome/{partyName}/{month}/{year}", tags=["Transactions"])
async def get_income_outcome(
    partyName: str = Path(..., description="Name of the party"),
    month: int = Path(..., description="Month"),
    year: int = Path(..., description="Year"),
    connection=Depends(get_database_connection)
):
    month = month + 1
    queryincome = f"SELECT SUM(amount) AS income FROM transactions WHERE partyName = '{partyName}' AND MONTH(stamp) = {month} AND YEAR(stamp) = {year} AND kind = 'income'"
    queryoutcome = f"SELECT SUM(amount) AS outcome FROM transactions WHERE partyName = '{partyName}' AND MONTH(stamp) = {month} AND YEAR(stamp) = {year} AND kind = 'outcome'"
    
    cursor = connection.cursor()
    cursor.execute(queryincome)
    income = cursor.fetchone()[0]
    
    cursor.execute(queryoutcome)
    outcome = cursor.fetchone()[0]
    
    cursor.close()
    
    return {"income": income, "outcome": outcome}

@router.delete("/deleteTransaction/{transactionId}", tags=["Transactions"])
async def delete_transaction_by_id(transactionId: int, connection=Depends(get_database_connection)):
    query = f"DELETE FROM transactions WHERE id = {transactionId}"
    cursor = connection.cursor()
    cursor.execute(query)
    connection.commit()
    cursor.close()
    return {"message": "Transaction deleted successfully"}

def update_wallet_balance(connection, wallet_name, amount, partyName):
    query = "UPDATE wallet SET balance = balance + %s WHERE walletName = %s AND partyName = %s"
    values = (amount, wallet_name, partyName)
    cursor = connection.cursor()
    cursor.execute(query, values)
    connection.commit()
    cursor.close()

