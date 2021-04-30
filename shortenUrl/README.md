### **Hello**

```
GET /hello
```

### **단축 URL 생성**

```
POST /shorten
```

Request body

```
{
    "url":{original-url}
}
```

### **단축 URL 조회**

```
GET /redirect
```

Request body

```
{
    "url":{shorten-url}
}
```

### **단축 URL 조회 (redirect)**

```
GET /redirect/{path}
```

Request example

```
GET http://localhost:8080/redirect/tqd3A
```

Response example
```
HTTP Status code: 302
Header: {
    "Location":"https://www.google.com",
    ...
}
```