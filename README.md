# Transaction Service сервис для взаимодействия с транзакциями

## Запуск

В application.yml Необходимо изменить следующие строки всоответствии с вашим данными
```
  datasource:
    url: jdbc:postgresql://localhost:5432/<databaseName>
    username: <user>
    password: <password>
```
#

## Методы

### Получение транзакции по id
```
POST /transactions/get  
```
Формат тела запроса:
```
{
    "transactionId": "<UUID>"
}
```
Формат ответа:
```
{
    "transactionId": "<UUID>",
    "timestamp": "<Date>",
    "fromAccount": 111,
    "toAccount": 222,
    "amount": 50.99
}
```
Коды возможных ошибок:
* 404 - Транзакция с таким id не найдена
#

### Перевод между аккаунтами
```
POST /transactions/new
```
Формат тела запроса:
```
{
    "fromAccount": 1,
    "toAccount": 2,
    "amount": 33.44
}
```
Формат ответа:
```
{
    "transactionId": "<UUID>",
    "timestamp": "<Date>"
}
```
Коды возможных ошибок:
* 400 - Недостаточно денег на балансе у fromAccount
* 404 - Один из аккаунтов не найден
* 502 - Ошибка сохранения транзакции в базу данных
* 502 - Ошибка отправки сообщения в Kafka

### Получение истории транзакций аккаунта
```
GET /account/{id}/history
```
Формат ответа:
```
{
    "accountId": 
    transactions: [{
        "transactionId": "<UUID>",
        "timestamp": "<Date>",
        "fromAccount": 111,
        "toAccount": 222,
        "amount": 50.99
    }, {
        "transactionId": "<UUID>",
        "timestamp": "<Date>",
        "fromAccount": 333,
        "toAccount": 111,
        "amount": 150.99
    }]
}
```
Коды возможных ошибок:
* 404 - Запрашиваемый аккаунт не найден