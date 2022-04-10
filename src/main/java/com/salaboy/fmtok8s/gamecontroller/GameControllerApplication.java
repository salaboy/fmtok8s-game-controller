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
import org.springframework.http.*;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
@Slf4j
@SerializationHint(types = GameInfo.class, typeNames = "com.salaboy.fmtok8s.gamecontroller.GameInfo")
@EnableScheduling
public class GameControllerApplication {

    // State that needs to be stored somewhere (Redis) to make sure that this scales
    private Map<String, GameInfo> levelsPerSession = new ConcurrentHashMap<>();

//    private Set<String> availableLevels = new HashSet<>();

//    private Map<String, String> levelUIs = new ConcurrentHashMap<>();

    private RestTemplate restTemplate = new RestTemplate();

    //@TODO: game monitor endpoints..
    //      Rankings
    //         Number of game sessions and levels
    //      Create session with nickname so we can keep track of who is winning

    private ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(GameControllerApplication.class, args);
    }

//    @Scheduled(fixedRate = 10000)
//    void checkLevels() {
//        //@TODO:
//        // Check for level 1, if it is ready, check for level 2, if it is not ready, don't move forward
//        if (availableLevels.size() == 0) {
//            // get http://level-1.default.svc.cluster.local/actuator/health
//            //   if 200 then add `level-1`
//            availableLevels.add("level-1");
//            availableLevels.add("level-3");
//        } else { // check for the next level
//            // get http://level-(availableLevels.size() + 1).default.svc.cluster.local/actuator/health
//            //availableLevels.add("level-(availableLevels.size() + 1)");
//        }
//
//    }

    @SerializationHint(types = GameInfo.class, typeNames = "com.salaboy.fmtok8s.gamecontroller.GameInfo")
    @Configuration
    public static class CloudEventHandlerConfiguration implements CodecCustomizer {

        @Override
        public void customize(CodecConfigurer configurer) {
            configurer.customCodecs().register(new CloudEventHttpMessageReader());
            configurer.customCodecs().register(new CloudEventHttpMessageWriter());
        }

    }

    @PostMapping(path = "/levels/{sessionId}/level-{levelId}/answer")
    public String answer(@PathVariable() String sessionId, @PathVariable() String levelId, @RequestBody Answers answers) {
        System.out.println("Answers received in the game controller for level:  " + levelId + "\n" + answers);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Answers> request = new HttpEntity<>(answers, headers);
        answers.setSessionId(sessionId);
        ResponseEntity<String> response = restTemplate.postForEntity(
//                "http://" + levelName + ".default.svc.cluster.local/",
                "http://questions-" + levelId + ".default.svc.cluster.local/",
                request,
                String.class);

        System.out.println("Level response: " + response.getBody());
        return "OK!";
    }

//    @GetMapping("/levels")
//    public Set<String> getAvailableLevels() {
//        return availableLevels;
//    }

//    @GetMapping("/levels/{levelName}")
//    public boolean checkIfLevelIsAvailable(@PathVariable() String levelName) {
//        return availableLevels.contains(levelName);
//    }
//
//    @GetMapping("/levels/{levelName}/ui")
//    public String getLevelUI(@PathVariable() String levelName) {
//        //Get Level User Interface from Level that is active
//        // This should cache the level UI so we don't hit the level functions multiple times
//        if (checkIfLevelIsAvailable(levelName)) {
//            // GET LEVEL UI and CACHE
//            if (levelUIs.get(levelName) == null) {
//                System.out.println("Caching Level UIs: " + levelName);
//
//                RestTemplate restTemplate = new RestTemplate();
//                ResponseEntity<String> response = restTemplate.exchange(
//                        "http://" + levelName + ".default.svc.cluster.local/ui", HttpMethod.GET, null,
//                        String.class);
//
//                String levelUIString = response.getBody();
//                System.out.println("Cached for Level: " + levelName + " ->  \n " + levelUIString);
//                levelUIs.put(levelName, levelUIString);
//            }
//            return levelUIs.get(levelName);
//        }
//        return "<h1>No User Interface Available for Level</h1>";
//    }

    //UI call newGame to get a new sessionId
    // New requests are forwarded to the correct level, by using the gameInfo levelId
    @PostMapping("/{nickname}")
    public GameInfo newGame(@PathVariable() String nickname) {
        GameInfo gameInfo = new GameInfo("game-" + UUID.randomUUID().toString());
        gameInfo.setNickname(nickname);
        levelsPerSession.put(gameInfo.getSessionId(), gameInfo);
        return gameInfo;
    }

    @PostMapping("/sessions/{sessionId}/start")
    public void startLevel(@PathVariable() String sessionId) {
        levelsPerSession.get(sessionId).setCurrentLevelStarted(true);
        levelsPerSession.get(sessionId).setStartedDate(new Date());
    }

    @GetMapping("/sessions")
    public Map<String, GameInfo> getSessions() {
        return levelsPerSession;
    }

    @GetMapping("/leaderboard")
    public Leaderboard getLeaderboard() {
        ResponseEntity<Leaderboard> response = restTemplate.exchange(
                "http://leaderboard.default.svc.cluster.local/", HttpMethod.GET, null,
                Leaderboard.class);

        return response.getBody();

    }

    @GetMapping("/sessions/{sessionId}")
    public GameInfo getGameInfoForSessionId(@PathVariable() String sessionId) {
        return levelsPerSession.get(sessionId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public GameInfo deleteGameInfoForSessionId(@PathVariable() String sessionId) {
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
