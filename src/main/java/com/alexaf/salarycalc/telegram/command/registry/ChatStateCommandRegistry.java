package com.alexaf.salarycalc.telegram.command.registry;

import com.alexaf.salarycalc.telegram.command.abstracts.ChatStateCommand;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatStateCommandRegistry {
    private final Map<ChatState, ChatStateCommand> commandMap;

    @Autowired
    public ChatStateCommandRegistry(List<ChatStateCommand> commands) {
        Map<ChatState, ChatStateCommand> modifiable = new HashMap<>(commands.size());
        commands.forEach(command -> modifiable.put(command.getState(), command));
        commandMap = Collections.unmodifiableMap(modifiable);
    }

    public ChatStateCommand getCommand(ChatState commandName) {
        return commandMap.get(commandName);
    }

}
