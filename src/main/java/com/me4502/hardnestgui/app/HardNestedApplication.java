package com.me4502.hardnestgui.app;

import static freemarker.template.Configuration.VERSION_2_3_26;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.IOException;
import java.util.Map;

public class HardNestedApplication {

    public static final boolean DEBUG = System.getProperty("debug", "false").equals("true");

    private static final HardNestedApplication instance = new HardNestedApplication();

    // Gson
    private Gson gson = new GsonBuilder().create();

    public void load() throws IOException {

        loadWebServer();
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
        get("/", (request, response)
                -> render(Map.of(), "index.html"));
    }

    /**
     * Helper method to render Freemarker template files.
     *
     * @param model The model
     * @param templatePath The template path
     * @return The rendered template
     */
    private static String render(Map<String, Object> model, String templatePath) {
        freemarker.template.Configuration config = new Configuration(VERSION_2_3_26);
        config.setClassForTemplateLoading(HardNestedApplication.class, "/static/html/");

        return new FreeMarkerEngine(config).render(new ModelAndView(model, templatePath));
    }

    public static HardNestedApplication getInstance() {
        return HardNestedApplication.instance;
    }
}
