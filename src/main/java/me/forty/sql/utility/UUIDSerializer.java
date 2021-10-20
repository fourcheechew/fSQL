package me.forty.sql.utility;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Copyright (c) 2021 - Tranquil, LLC.
 *
 * @author 42 on Oct, 19, 2021 - 11:44 PM
 * @project fSQL
 */
public class UUIDSerializer implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

    public JsonElement serialize(UUID uuid, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(uuid.toString());
    }

    public UUID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return UUID.fromString(jsonElement.getAsString());
    }

}
