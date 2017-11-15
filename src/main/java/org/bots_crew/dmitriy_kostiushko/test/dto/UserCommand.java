package org.bots_crew.dmitriy_kostiushko.test.dto;

import java.util.regex.Pattern;


public class UserCommand {

    private Pattern commandPattern;
    private ResponsiveCommand commandResponse;


    public UserCommand() {

    }

    public UserCommand(Pattern commandPattern, ResponsiveCommand commandResponse) {
        this.commandPattern = commandPattern;
        this.commandResponse = commandResponse;
    }

    public Pattern getCommandPattern() {
        return commandPattern;
    }

    public void setCommandPattern(Pattern commandPattern) {
        this.commandPattern = commandPattern;
    }

    public ResponsiveCommand getCommandResponse() {
        return commandResponse;
    }

    public void setCommandResponse(ResponsiveCommand commandResponse) {
        this.commandResponse = commandResponse;
    }
}
