# Game Controller

Test requests: 

```
curl -X POST http://localhost:8080/  
```

Use the `sessionId` for the CloudEvents payload: 

```
curl -v "http://localhost:8080/events" \                                     
-H "Content-Type:application/json; charset=UTF-8" \
-H "Ce-Id:1" \
-H "Ce-Subject:test" \
-H "Ce-Time:2022-02-10T09:32:27.875Z" \
-H "Ce-Source:cloud-event-example" \
-H "Ce-Type:LevelCompletedEvent" \
-H "Ce-Specversion:1.0" \
-d "{\"sessionId\": \"game-e71ac906-c4d3-4188-9c54-abc247ccbaa0\", \"levelId\": \"1\"}\""

```

Get Single session: 
```
curl http://localhost:8080/sessions/game-e71ac906-c4d3-4188-9c54-abc247ccbaa0
```

Get all sessions: 

```
curl http://localhost:8080/sessions/
```