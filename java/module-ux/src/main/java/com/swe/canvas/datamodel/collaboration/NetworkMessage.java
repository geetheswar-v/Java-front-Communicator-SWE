/*
 * -----------------------------------------------------------------------------
 * File: NetworkMessage.java
 * Owner: B S S Krishna
 * Roll Number: 112201013
 * Module: Canvas
 * -----------------------------------------------------------------------------
 */

package com.swe.canvas.datamodel.collaboration;

import com.swe.canvas.datamodel.serialization.JsonUtils;
import java.nio.charset.StandardCharsets;

/**
 * A wrapper for data sent over the network.
 *
 * <p>This class encapsulates the type of message (e.g., NORMAL, UNDO),
 * the serialized binary data of an action, and an optional string payload
 * (used primarily for RESTORE operations).</p>
 */
public class NetworkMessage {

    /** The type of the message. */
    private final MessageType messageType;

    /** The serialized action data (can be null if payload is used). */
    private final byte[] serializedAction;

    /** Optional payload for operations like RESTORE (JSON String). */
    private final String payload;

    /**
     * Constructor for standard actions without a string payload.
     *
     * @param type   The type of the message.
     * @param action The serialized action bytes.
     */
    public NetworkMessage(final MessageType type, final byte[] action) {
        this(type, action, null);
    }

    /**
     * Constructor for messages with an optional string payload (e.g., RESTORE).
     *
     * @param type    The type of the message.
     * @param action  The serialized action bytes (can be null).
     * @param content The string payload (can be null).
     */
    public NetworkMessage(final MessageType type, final byte[] action, final String content) {
        this.messageType = type;
        if (action != null) {
            this.serializedAction = action.clone();
        } else {
            this.serializedAction = null;
        }
        this.payload = content;
    }

    /**
     * Gets the message type.
     *
     * @return The MessageType enum.
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Gets the serialized action data.
     *
     * @return A copy of the byte array, or null.
     */
    public byte[] getSerializedAction() {
        if (serializedAction != null) {
            return serializedAction.clone();
        }
        return null;
    }

    /**
     * Gets the string payload.
     *
     * @return The payload string, or null.
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Serializes this NetworkMessage into a JSON string.
     * Matches .NET CanvasSerializer.SerializeNetworkMessage format.
     *
     * @return A JSON representation of this message.
     */
    public String serialize() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        // Use "MessageType" with PascalCase enum value to match .NET
        sb.append(JsonUtils.jsonEscape("MessageType")).append(":")
            .append(JsonUtils.jsonEscape(toPascalCase(messageType.toString())));

        // Serialize action as nested JSON object (not Base64) to match .NET
        if (serializedAction != null) {
            final String actionJson = new String(serializedAction, StandardCharsets.UTF_8);
            sb.append(",").append(JsonUtils.jsonEscape("Action")).append(":")
                .append(actionJson);  // Direct JSON, not escaped string
        }

        // Append Payload string if present
        if (payload != null) {
            sb.append(",").append(JsonUtils.jsonEscape("Payload")).append(":")
                .append(JsonUtils.jsonEscape(payload));
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Converts UPPERCASE enum name to PascalCase to match .NET format.
     */
    private static String toPascalCase(final String uppercase) {
        if (uppercase == null || uppercase.isEmpty()) {
            return uppercase;
        }
        return uppercase.substring(0, 1).toUpperCase()
            + uppercase.substring(1).toLowerCase();
    }

    /**
     * Deserializes a JSON string back into a NetworkMessage.
     * Supports both .NET format (PascalCase) and Java format (lowercase) for compatibility.
     *
     * @param json The JSON string to deserialize.
     * @return The NetworkMessage object, or null if deserialization fails.
     */
    public static NetworkMessage deserialize(final String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            // 1. Extract Type - try both "MessageType" (.NET) and "type" (Java) keys
            String typeStr = JsonUtils.extractString(json, "MessageType");
            if (typeStr == null) {
                typeStr = JsonUtils.extractString(json, "type");
            }
            if (typeStr == null) {
                return null;
            }
            // Handle both PascalCase (.NET) and UPPERCASE (Java) enum values
            final MessageType type = MessageType.valueOf(typeStr.toUpperCase());

            // 2. Extract Action - try "Action" (.NET nested JSON) first
            byte[] actionBytes = null;
            final String actionJson = JsonUtils.extractNestedJson(json, "Action");
            if (actionJson != null && !"null".equals(actionJson)) {
                actionBytes = actionJson.getBytes(StandardCharsets.UTF_8);
            }

            // 3. Extract Payload - try both "Payload" (.NET) and "payload" (Java)
            String payloadStr = JsonUtils.extractString(json, "Payload");
            if (payloadStr == null) {
                payloadStr = JsonUtils.extractString(json, "payload");
            }

            return new NetworkMessage(type, actionBytes, payloadStr);

        } catch (final Exception e) {
            System.err.println("NetworkMessage deserialization failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}