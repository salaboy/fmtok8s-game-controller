# FMTOK8s :: Game Controller

This project is a simple game controller that is in charge of creating new Game Sessions, checking available levels and keep track of in which level each player is. 

## Build & Run

To build this project you can run: 
```
mvn package
```

To run this project you can run: 
```
mvn spring-boot:run
```

To deploy this project you can use the `mvn spring-boot:build-image` goal to create a container that you can tag and push to a docker registry:

```
mvn spring-boot:build-image

```

Then tag and push: 
```
docker tag fmtok8s-game-controller:0.0.1-SNAPSHOT salaboy/fmtok8s-game-controller:0.1.0
docker push salaboy/fmtok8s-game-controller:0.1.0

```

Finally, you can deploy this project to a Kubernetes Cluster (with Knative Serving and Eventing installed) by running: 

```
kubectl apply -f config/
```

Notice that this also install a Knative Eventing Trigger that will be listening for `LevelCompletedEvent` Cloud Events. 


## Example Requests

To create a new Session: 
```
curl -X POST http://localhost:8082/  
```

Use the `sessionId` for the CloudEvents payload: 

```
curl -v "http://localhost:8082/events" \                                     
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
curl http://localhost:8082/sessions/game-e71ac906-c4d3-4188-9c54-abc247ccbaa0
```

Get all sessions: 

```
curl http://localhost:8082/sessions/
```

To check available levels: 

```
curl http://localhost:8082/levels
```

To check specific level by name:

```
curl http://localhost:8082/levels/level-1
```