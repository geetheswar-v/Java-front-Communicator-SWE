package com.swe.ux.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.swe.cloud.datastructures.CloudResponse;
import com.swe.cloud.datastructures.Entity;
import com.swe.cloud.datastructures.TimeRange;
import com.swe.cloud.functionlibrary.CloudFunctionLibrary;
import com.swe.ux.analytics.NetworkHeartbeatMonitor;
import com.swe.ux.model.analytics.DashboardModel;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Service for fetching dashboard data from the cloud.
 */
public class DashboardService {
    /** Default number of users present. */
    private static final int DEFAULT_USERS_PRESENT = 0;
    /** Default number of users logged out. */
    private static final int DEFAULT_USERS_LOGGED_OUT = 0;
    /** Default meeting summary message. */
    private static final String DEFAULT_SUMMARY =
            "We will surface historical insights once your meetings generate activity.";
    /** Cloud module name. */
    private static final String DASHBOARD_MODULE = "UX";
    /** Cloud table name. */
    private static final String DASHBOARD_TABLE = "Dashboard";
    /** Cloud type identifier. */
    private static final String DASHBOARD_TYPE = "Summary";

    /** Fetch function used to obtain dashboard data. */
    private final Function<Entity, CompletableFuture<CloudResponse>> fetchFunction;

    /**
     * Creates a new DashboardService using the real cloud library.
     */
    public DashboardService() {
        Function<Entity, CompletableFuture<CloudResponse>> function = null;
        try {
            final CloudFunctionLibrary library = new CloudFunctionLibrary();
            function = library::cloudGet;
        } catch (Throwable t) {
            System.err.println("DashboardService: Unable to initialize CloudFunctionLibrary - "
                    + t.getMessage());
        }
        this.fetchFunction = function;
    }

    /**
     * Creates a new DashboardService with a custom fetch function.
     * Intended for tests.
     *
     * @param fetcher function that executes the cloud request
     */
    DashboardService(final Function<Entity, CompletableFuture<CloudResponse>> fetcher) {
        this.fetchFunction = fetcher;
    }

    /**
     * Fetches dashboard data for the given user.
     *
     * @param userId the user identifier (typically email)
     * @return future emitting a DashboardModel
     */
    public CompletableFuture<DashboardModel> fetchDashboardData(final String userId) {
        if (userId == null || userId.isEmpty() || fetchFunction == null) {
            return CompletableFuture.completedFuture(buildDefaultModel());
        }

        final Entity request = new Entity(
                DASHBOARD_MODULE,
                DASHBOARD_TABLE,
                userId,
                DASHBOARD_TYPE,
                -1,
                new TimeRange(0, 0),
                null);

        try {
            return fetchFunction.apply(request)
                    .whenComplete((res, err) -> NetworkHeartbeatMonitor.getInstance().recordActivity())
                    .thenApply(this::convertToModel)
                    .exceptionally(ex -> {
                        System.err.println("DashboardService: cloud fetch failed - " + ex.getMessage());
                        return buildDefaultModel();
                    });
        } catch (Exception e) {
            System.err.println("DashboardService: exception while requesting cloud data - " + e.getMessage());
            return CompletableFuture.completedFuture(buildDefaultModel());
        }
    }

    private DashboardModel convertToModel(final CloudResponse response) {
        if (response == null || response.data() == null) {
            return buildDefaultModel();
        }
        final JsonNode root = response.data();
        final String rawPayload = root.toPrettyString();
        final JsonNode payload = extractPayload(root);
        if (payload == null || payload.isMissingNode()) {
            return new DashboardModel(DEFAULT_USERS_PRESENT, DEFAULT_USERS_LOGGED_OUT,
                    DEFAULT_SUMMARY, rawPayload);
        }

        final int usersPresent = payload.path("usersPresent").asInt(DEFAULT_USERS_PRESENT);
        final int usersLoggedOut = payload.path("usersLoggedOut").asInt(DEFAULT_USERS_LOGGED_OUT);
        final String summary = payload.path("meetingSummary").asText(DEFAULT_SUMMARY);
        return new DashboardModel(usersPresent, usersLoggedOut, summary, rawPayload);
    }

    private JsonNode extractPayload(final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.has("data")) {
            final JsonNode nested = node.get("data");
            if (nested != null && !nested.isNull()) {
                final JsonNode resolved = extractPayload(nested);
                if (resolved != null) {
                    return resolved;
                }
            }
        }
        if (node.isArray()) {
            for (JsonNode child : node) {
                final JsonNode candidate = extractPayload(child);
                if (candidate != null) {
                    return candidate;
                }
            }
            return null;
        }
        if (node.has("usersPresent") || node.has("usersLoggedOut") || node.has("meetingSummary")) {
            return node;
        }
        return null;
    }

    private DashboardModel buildDefaultModel() {
        return new DashboardModel(DEFAULT_USERS_PRESENT, DEFAULT_USERS_LOGGED_OUT,
                DEFAULT_SUMMARY, "No cloud data returned yet.");
    }
}
