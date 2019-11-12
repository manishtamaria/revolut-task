# revolut-task
To Start app :

step 1: mvn clean install
step 2 : mvn exec:java
step 3 : open locahost:8081/api/accounts  => it will show default accounts in database

# API Model Structures :

Account :
{
    "id": <number>,
    "ownerName": <string>,
    "balance": <double>,
    "currency": <string>
}
  
  Transaction :
  
  {
    "id": <number>,
    "fromBankAccountId": <number>,
    "toBankAccountId": <number>,
    "amount": <double>,
    "currency": <string>,
    "creationDate": <timestamp>,
    "updateDate": <timestamp>,
    "status": <string - one from "NEW", "PROCESSING", "FAILED", "SUCCEED">
}
  
  # API Operations
  
  GET /accounts = to get all accounts 
  GET /accounts/1 = to get account detail by id in path variable
  
 GET  /transactions = to get all transactions list 
 POST /transactions =  to create transaction
{
    "fromBankAccountId": 1,
    "toBankAccountId": 2,
    "amount": 100,
    "currency": "PLN"
}
 
