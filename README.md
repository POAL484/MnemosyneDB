# MnemosyneDB

NoSQL key-value база данных на Java. Смотрите также https://github.com/POAL484/HermesDB

> Мнемосина — это древнегреческая богиня памяти и языка, титанида, дочь Урана и Геи

# Запуск

Только в Development режиме:
```sh
./gradlew run
```

# Поддерживаемые типы хранения

- String
- Java Long-size целочисленные
- Java Double-size числа с плавающей точкой
- boolean

# API Docs

Приложение запускает http сервер на локальном порту 5445

### Модели данных

Response:
```json
{
    "status": int, HTTP response status код,
    "error": "String?, при status != 200, описание ошибки",
    "data": Object?, при status == 200, данные ответа
}
```

CacheDTO:
```json
{
    "id": "String presented UUID4, идентификатор этого CacheDTO",
    "userId": "String presented UUID4, идентификатор пользователя",
    "ts": long presented timestamp, создания CacheDTO,
    "cacheObject": Object, объект хранения с одним из поддерживаемых типов данных
}
```


### [GET] /

***Request***

Headers
- String user: валидный UUID4

JSON Body
```json
{
    "key": "String, ключ для даныых"
}
```

***Response***

**OK 200**

JSON Body:
```json
{
    "status": 200,
    "data": CacheDTO
}
```

**Bad request 400**

Возникает при неверном формате JSON или отсутсвующем поле key, или при key == null 

JSON Body: Repsonse со статусом 400

**Unauthorized 401**

Возникает при отсуствии header 'user' или не соответсвия его стандарту UUID4

JSON Body: Response  со статусом 401

**Not found 404**

Возникает при отсуствии значения по ключу key в базе данных

JSON Body: Response со статусом 404

### [POST or PUT] /

***Request***

Headers
- String user: валидный UUID4

JSON Body
```json
{
    "key": "String, ключ для даныых"
    "val": Object, объект для хранения, с одним из поддерживаемых типов данных
}  
```

***Response***

**OK 200**

JSON Body:
```json
{
    "status": 200,
    "data": CacheDTO
}
```

**Bad request 400**

Возникает при неверном формате JSON, отсуствующих полях key или val, или при key == null или val == null

JSON Body: Repsonse со статусом 400

**Unauthorized 401**

Возникает при отсуствии header 'user' или не соответсвия его стандарту UUID4

JSON Body: Response со статусом 401

### [DELETE] /

***Request***

Headers
- String user: валидный UUID4

JSON Body
```json
{
    "key": "String, ключ для даныых"
}
```

***Response***

**OK 200**

JSON Body:
```json
{
    "status": 200,
    "data": CacheDTO
}
```

**Bad request 400**

Возникает при неверном формате JSON или отсутсвующем поле key, или при key == null

JSON Body: Response со статусом 400

**Unauthorized 401**

Возникает при отсуствии header 'user' или не соответсвия его стандарту UUID4

JSON Body: Response со статусом 401

**Not found 404**

Возникает при отсуствии значения по ключу key в базе данных

JSON Body: Response со статусом 404

