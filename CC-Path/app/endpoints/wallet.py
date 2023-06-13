from pydantic import BaseModel
from fastapi import APIRouter, Depends, HTTPException, Query, Path
from app.database.connection import get_database_connection

router = APIRouter()

from fastapi import APIRouter, Depends, Query
from app.database.connection import get_database_connection

router = APIRouter()

class Wallet(BaseModel):
    partyName: str
    walletName: str
    balance: float


class Member(BaseModel):
    partyName: str
    email: str


@router.post("/addWallet", tags=["Wallet"])
async def add_wallet(wallet: Wallet, connection=Depends(get_database_connection)):
    queryfinal = f"INSERT INTO wallet VALUES ('{wallet.partyName}', '{wallet.walletName}', {wallet.balance}, NULL)"
    cursor = connection.cursor()
    cursor.execute(queryfinal)
    connection.commit()
    cursor.close()
    return {"message": "Wallet added successfully"}

@router.put('/addMember', tags=["Wallet"])
async def add_member(member: Member, connection=Depends(get_database_connection)):
    queryfinal = f"insert into members values ('{member.partyName}', '{member.email}', NULL)"
    cursor = connection.cursor()
    cursor.execute(queryfinal)
    connection.commit()
    cursor.close()
    return {"message": "Member added successfully"}

@router.put("/deleteMember", tags=["Wallet"])
async def delete_member(member: Member, connection=Depends(get_database_connection)):
    queryfinal = f"DELETE FROM members WHERE partyName = '{member.partyName}' AND email = '{member.email}'"
    cursor = connection.cursor()
    cursor.execute(queryfinal)
    connection.commit()
    cursor.close()
    return {"message": "Member deleted successfully"}

@router.get("/getWallet/{partyName}", tags=["Wallet"])
async def get_wallet(
    partyName: str = Path(..., description="Party Name"),
    connection=Depends(get_database_connection)
):
    queryfinal = f"SELECT walletName, balance FROM wallet WHERE partyName = '{partyName}'"
    cursor = connection.cursor(dictionary=True)
    cursor.execute(queryfinal)
    read_row = cursor.fetchall()
    cursor.close()
    return read_row


@router.get("/getPartyList/{email}", tags=["Wallet"])
async def get_party_list(
    email: str = Path(..., description="Email"),
    connection=Depends(get_database_connection)
):
    data_party = get_party(email, connection)
    
    for party in data_party:
        party["members"] = get_member_list(party["partyName"], connection)
        party["balance"] = get_balance_from_party(party["partyName"], connection)
    
    return data_party

@router.get("/getMemberLists/{partyname}", tags=["Wallet"])
async def get_member_lists(
    partyname: str = Path(..., description="Party Name"),
    connection=Depends(get_database_connection)
):
    host_email = get_host_email(partyname, connection)
    data_member = get_member_list(partyname, connection)
    
    for member in data_member:
        if host_email == member['email']:
            member["isHost"] = 1
        else:
            member["isHost"] = 0
    
    return data_member

@router.get("/getBalance/{partyName}", tags=["Wallet"])
async def get_balance(partyName: str = Path(..., description="Party Name"), connection=Depends(get_database_connection)):
    return get_balance_from_party(partyName, connection)

def get_host_email(partyname, connection):
    queryfinal = f"SELECT createdBy FROM party WHERE partyName = '{partyname}'"
    cursor = connection.cursor(dictionary=True)
    cursor.execute(queryfinal)
    read_row = cursor.fetchone()
    cursor.close()
    return read_row

def get_party(email, connection):
    queryfinal = f"SELECT partyName FROM members WHERE email = '{email}'"
    cursor = connection.cursor(dictionary=True)
    cursor.execute(queryfinal)
    read_row = cursor.fetchall()
    cursor.close()
    return read_row

def get_member_list(partyName, connection):
    queryfinal = f"SELECT email FROM members WHERE partyName = '{partyName}'"
    cursor = connection.cursor(dictionary=True)
    cursor.execute(queryfinal)
    read_row = cursor.fetchall()
    cursor.close()
    return read_row

def get_balance_from_party(partyName, connection):
    queryfinal = f"SELECT IFNULL(SUM(balance), 0) AS balance FROM wallet WHERE partyName = '{partyName}'"
    cursor = connection.cursor(dictionary=True)
    cursor.execute(queryfinal)
    read_row = cursor.fetchall()
    cursor.close()
    return read_row
