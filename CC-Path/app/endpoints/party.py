from pydantic import BaseModel
from fastapi import APIRouter, Depends
from app.database.connection import get_database_connection
from datetime import datetime

router = APIRouter()

class Party(BaseModel):
    partyName: str
    createdBy: str

class Member(BaseModel):
    partyName: str
    email: str

@router.post("/createParty", tags=["Party"])
async def create_party(party: Party, connection=Depends(get_database_connection)):
    query = "INSERT INTO party (partyName, createdBy) VALUES (%s, %s)"
    cursor = connection.cursor()
    cursor.execute(query, (party.partyName, party.createdBy))
    connection.commit()
    query = "INSERT INTO members (partyName, email) VALUES (%s, %s)"
    cursor = connection.cursor()
    cursor.execute(query, (party.partyName, party.createdBy))
    connection.commit()
    cursor.close()
    return {"message": "Party added successfully"}

@router.post('/addMember', tags=["Party"])
async def add_member(member: Member, connection=Depends(get_database_connection)):
    query = "insert into members (partyName, email) values (%s, %s)"
    cursor = connection.cursor()
    cursor.execute(query, (member.partyName, member.email))
    connection.commit()
    cursor.close()
    return {"message": "Member added successfully"}

@router.post("/deleteMember", tags=["Party"])
async def delete_member(member: Member, connection=Depends(get_database_connection)):
    query = "DELETE FROM members WHERE partyName = %s AND email = %s"
    cursor = connection.cursor()
    cursor.execute(query, (member.partyName, member.email))
    connection.commit()
    cursor.close()
    return {"message": "Member deleted successfully"}

@router.post("/deleteParty/{partyName}", tags=["Party"])
async def delete_party(partyName: str, connection=Depends(get_database_connection)):
    query = "DELETE FROM party WHERE partyName = %s"
    cursor = connection.cursor()
    cursor.execute(query, (partyName,))
    connection.commit()
    cursor.close()
    return {"message": "Party deleted successfully"}