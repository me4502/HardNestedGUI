package com.me4502.hardnestgui.app;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.redirect;
import static spark.Spark.staticFiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.me4502.hardnestgui.broker.TaskBroker;
import com.me4502.hardnestgui.card.CardSector;
import com.me4502.hardnestgui.card.CardStatus;
import com.me4502.hardnestgui.json.StartPayload;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HardNestedApplication {

    public static final boolean DEBUG = System.getProperty("debug", "false").equals("true");

    private static final HardNestedApplication instance = new HardNestedApplication();

    // Gson
    private Gson gson = new GsonBuilder().create();

    private List<String> statusMessages = new ArrayList<>();
    private CardStatus cardStatus = new CardStatus();
    private String failError;

    private TaskBroker taskBroker;

    public void load() throws IOException {
        loadWebServer();
        Thread taskBrokerThread = new Thread(taskBroker = new TaskBroker());
        taskBrokerThread.setName("Task Broker Thread");
        taskBrokerThread.setDaemon(true);
        taskBrokerThread.start();
    }

    /**
     * Helper function to respond with a bad request.
     *
     * @param response The response object
     * @param message The error message
     * @return The json-ified error message
     */
    private String badRequest(Response response, String message) {
        response.status(400);
        response.header("Bad Request", message);
        return gson.toJson(Map.of("error", message));
    }

    /**
     * Setup the webserver configuration and routes.
     */
    private void loadWebServer() {
        port(Integer.parseInt(System.getProperty("hard_nested_gui.port", "5078")));
        if (DEBUG) {
            // During debug this allows hot-reloading the static changes.
            staticFiles.externalLocation("src/main/resources/static");
        } else {
            staticFiles.location("/static");
        }

        // Setup routes
        redirect.get("/", "/index.html");
        get("/status_messages/:lastMessage", (req, res) -> {
            String lastMessage = req.params("lastMessage");
            try {
                int lastMessageNum = Integer.parseInt(lastMessage);
                if (lastMessageNum < 0 || lastMessageNum > statusMessages.size() || statusMessages.size() == 0) {
                    return gson.toJson(Map.of("message_id", lastMessageNum, "messages", List.of()));
                }
                return gson.toJson(
                        Map.of(
                                "message_id", statusMessages.size(),
                                "messages", statusMessages.subList(lastMessageNum, statusMessages.size())
                        )
                );
            } catch (NumberFormatException e) {
                return badRequest(res, "Last message was invalid. Must be a number.");
            } catch (Exception e) {
                e.printStackTrace();
                return badRequest(res, "Failed to get log: " + e.getMessage());
            }
        });
        post("/start_application/", (req, res) -> {
            StartPayload body = gson.fromJson(req.body(), StartPayload.class);
            body.keys.forEach((key, value) -> cardStatus.setSectorKey(CardSector.valueOf(key), value));
            taskBroker.startTasks();
            return gson.toJson(Map.of("message", "Started Running"));
        });
        get("/get_application_state/", (req, res) -> {
            Map<String, String> knownKeys = new HashMap<>();
            int knownKeyCount = 0;
            for (CardSector sector : CardSector.values()) {
                knownKeys.put(sector.name(), cardStatus.getSectorKey(sector).orElse(""));
                if (cardStatus.getSectorKey(sector).isPresent()) {
                    knownKeyCount ++;
                }
            }
            var map = new HashMap<String, Object>();
            map.put("update", knownKeys);
            if (failError != null) {
                map.put("failMessage", failError);
                failError = null;
            }
            if (knownKeyCount == CardSector.values().length) {
                map.put("complete", "true");
            }
            return gson.toJson(map);
        });
        post("/reset_keys/", (req, res) -> {
            cardStatus.resetKeys();
            return gson.toJson(Map.of("message", "Reset Keys!"));
        });
    }

    public CardStatus getCardStatus() {
        return this.cardStatus;
    }

    public void addStatusMessage(String message) {
        statusMessages.add(message);
    }

    public void failWithError(String message) {
        this.failError = message;
    }

    public static HardNestedApplication getInstance() {
        return HardNestedApplication.instance;
    }
}
