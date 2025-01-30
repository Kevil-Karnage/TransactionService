# Transaction Service

## методы

### Получение транзакции по id
```
POST /transactions/get  
```
request body:
```
{
    'transactionId': <UUID>
}
```

### Перевод между аккаунтами
```
POST /transactions/new
```
request body:
```
{
    "fromAccount": 1,
    "toAccount": 2,
    "amount": 33.44
}
```

### Получение истории транзакций аккаунта
```
GET /account/{id}/history
```