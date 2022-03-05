package com.salaboy.fmtok8s.gamecontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.cloudevents.spring.webflux.CloudEventHttpMessageReader;
import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
@Slf4j
@SerializationHint(types = GameInfo.class, typeNames = "com.salaboy.fmtok8s.gamecontroller.GameInfo")
public class GameControllerApplication {

    //@TODO: game monitor endpoints..
    //      Rankings
    //         Number of game sessions and levels
    //      Create session with nickname so we can keep track of who is winning

    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(GameControllerApplication.class, args);
    }

    @SerializationHint(types = GameInfo.class, typeNames = "com.salaboy.fmtok8s.gamecontroller.GameInfo")
    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }

    private Map<String, GameInfo> levelsPerSession = new ConcurrentHashMap<>();

    //UI call newGame to get a new sessionId
    // New requests are forwarded to the correct level, by using the gameInfo levelId
    @PostMapping("/")
    public GameInfo newGame() {
        GameInfo gameInfo = new GameInfo("game-" + UUID.randomUUID().toString());
        levelsPerSession.put(gameInfo.getSessionId(), gameInfo);
        return gameInfo;
    }

    @PostMapping("/sessions/{sessionId}/start")
    public void startLevel(@PathVariable() String sessionId){
        levelsPerSession.get(sessionId).setCurrentLevelStarted(true);
        levelsPerSession.get(sessionId).setStartedDate(new Date());
    }

    @GetMapping("/sessions")
    public Map<String, GameInfo> getSessions(){
        return levelsPerSession;
    }


    @GetMapping("/sessions/{sessionId}")
    public GameInfo getGameInfoForSessionId(@PathVariable() String sessionId){
        return levelsPerSession.get(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public GameInfo deleteGameInfoForSessionId(@PathVariable() String sessionId){
        return levelsPerSession.remove(sessionId);
    }



    @PostMapping(value = "/events")
    public ResponseEntity<Void> consumeLevelCloudEvents(@RequestBody CloudEvent cloudEvent) throws IOException {

        logCloudEvent(cloudEvent);
        if (!cloudEvent.getType().equals("LevelCompletedEvent")) {
            throw new IllegalStateException("Wrong Cloud Event Type, expected: 'LevelCompletedEvent' and got: " + cloudEvent.getType());
        }


        GameInfo gameInfo = objectMapper.readValue(cloudEvent.getData().toBytes(), GameInfo.class);
        // Here you can do whatever you want with your Application data:
        log.info("GameInfo SessionId: " + gameInfo.getSessionId());
        log.info("GameInfo LevelId: " + gameInfo.getCurrentLevelId());

        //Move to the next level
        levelsPerSession.get(gameInfo.getSessionId()).setNextLevelId(gameInfo.getCurrentLevelId() + 1);
        levelsPerSession.get(gameInfo.getSessionId()).setCurrentLevelCompleted(true);
        levelsPerSession.get(gameInfo.getSessionId()).setCompletedDate(new Date());

        //Send via websocket a CloudEvent to the UI ??


        return ResponseEntity.ok().build();

    }

    private void logCloudEvent(CloudEvent cloudEvent) {
        EventFormat format = EventFormatProvider
                .getInstance()
                .resolveFormat(JsonFormat.CONTENT_TYPE);

        log.info("Cloud Event: " + new String(format.serialize(cloudEvent)));

    }


}
