from pydantic import BaseModel
from fastapi import APIRouter, Depends, HTTPException, Path
from app.database.connection import get_database_connection
from datetime import datetime

router = APIRouter()

class AddTransaction(BaseModel):
    partyName: str
    createdBy: str
    kind: str
    fromWallet: str
    toWallet: str = None
    amount: float
    stamp: datetime
    incomeOutcomeKind: str = None

class GetTransaction(BaseModel):
    partyname: str
    month: int
    year: int

class TransactionDay(BaseModel):
    day: int
    transactions: list
    expense: int

@router.post("/addTransaction", tags=["Transactions"])
async def add_transaction(transaction: AddTransaction, connection=Depends(get_database_connection)):

    if transaction.kind == "transfer":
        queryfinal = f"insert into transactions (partyName, createdBy, kind, fromWallet, toWallet, amount, stamp, IncomeOutcomeKind) values ('{transaction.partyName}', '{transaction.createdBy}', '{transaction.kind}', '{transaction.fromWallet}', '{transaction.toWallet}', {transaction.amount}, '{transaction.stamp}')"
        queryminuswallet = f"update wallet set balance = balance - {transaction.amount} where walletName = '{transaction.fromWallet}' and partyName = '{transaction.partyName}'"
        querypluswallet = f"update wallet set balance = balance + {transaction.amount} where walletName = '{transaction.toWallet}' and partyName = '{transaction.partyName}'"
        cursor = connection.cursor()
        cursor.execute(queryfinal)
        cursor.execute(queryminuswallet)
        cursor.execute(querypluswallet)
        
    elif transaction.kind == "outcome":
        queryfinal = f"insert into transactions (partyName, createdBy, kind, fromWallet, amount, stamp, IncomeOutcomeKind) values ('{transaction.partyName}', '{transaction.createdBy}', '{transaction.kind}', '{transaction.fromWallet}', {transaction.amount}, '{transaction.stamp}', '{transaction.incomeOutcomeKind}')"
        queryminuswallet = f"update wallet set balance = balance - {transaction.amount} where walletName = '{transaction.fromWallet}' and partyName = '{transaction.partyName}'"
        cursor = connection.cursor()
        cursor.execute(queryfinal)
        cursor.execute(queryminuswallet)

    else:
        queryfinal = f"insert into transactions (partyName, createdBy, kind, fromWallet, amount, stamp, IncomeOutcomeKind) values ('{transaction.partyName}', '{transaction.createdBy}', '{transaction.kind}', '{transaction.fromWallet}', {transaction.amount}, '{transaction.stamp}', '{transaction.incomeOutcomeKind}')"
        querypluswallet = f"update wallet set balance = balance + {transaction.amount} where walletName = '{transaction.fromWallet}' and partyName = '{transaction.partyName}'"
        cursor = connection.cursor()
        cursor.execute(queryfinal)
        cursor.execute(querypluswallet)

    connection.commit()
    cursor.close()
    
    return {"message": "Transaction added successfully"}

@router.get("/getTransaction/{partyName}/{month}/{year}", tags=["Transactions"])
async def get_transaction(
    partyName: str = Path(..., description="Name of the party"),
    month: int = Path(..., description="Month"),
    year: int = Path(..., description="Year"),
    connection=Depends(get_database_connection)
):
    month = month + 1
    data_days = get_days(partyName, month, year, connection)
    
    for day in data_days:
        day["transactions"] = get_transactions_by_day(partyName, day['day'], month, year, connection)
        expense = 0
        for transaction in day["transactions"]:
            if transaction["kind"] == "outcome":
                expense -= transaction["amount"]
            elif transaction["kind"] == "income":
                expense += transaction["amount"]
        day["expense"] = expense
    
    return data_days

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

def get_days(partyname, month, year, connection):
    queryfinal = f"SELECT DISTINCT(DAY(stamp)) AS day FROM transactions WHERE partyName = '{partyname}' AND MONTH(stamp) = {month} AND YEAR(stamp) = {year}"
    cursor = connection.cursor()
    cursor.execute(queryfinal)
    read_rows = cursor.fetchall()
    cursor.close()
    return read_rows

def get_transactions_by_day(partyname, day, month, year, connection):
    queryfinal = f"SELECT kind, IncomeOutcomeKind, amount, fromWallet, toWallet, createdBy, id FROM transactions WHERE partyName = '{partyname}' AND DAY(stamp) = {day} AND MONTH(stamp) = {month} AND YEAR(stamp) = {year}"
    cursor = connection.cursor()
    cursor.execute(queryfinal)
    read_rows = cursor.fetchall()
    cursor.close()
    return read_rows