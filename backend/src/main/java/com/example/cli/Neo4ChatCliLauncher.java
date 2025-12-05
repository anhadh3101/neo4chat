package com.example.cli;

import com.example.Neo4ChatApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Separate entrypoint for running the Neo4Chat CLI without starting the web server.
 */
public class Neo4ChatCliLauncher {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(Neo4ChatApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);

        try {
            Neo4ChatCLI cli = context.getBean(Neo4ChatCLI.class);
            cli.run();
        } finally {
            context.close();
        }
    }
}


