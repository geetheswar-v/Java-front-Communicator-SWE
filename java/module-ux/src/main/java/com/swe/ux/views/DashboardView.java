package com.swe.ux.views;

import com.swe.ux.viewmodels.DashboardViewModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

class DashboardView extends VBox {
    /**
     * Default padding value.
     */
    private static final int DEFAULT_PADDING = 30;
    /**
     * Default spacing value.
     */
    private static final int DEFAULT_SPACING = 20;
    /**
     * Title font size.
     */
    private static final int TITLE_FONT_SIZE = 28;
    /**
     * Label font size.
     */
    private static final int LABEL_FONT_SIZE = 16;
    /**
     * Value font size.
     */
    private static final int VALUE_FONT_SIZE = 36;
    /**
     * Summary font size.
     */
    private static final int SUMMARY_FONT_SIZE = 14;
    /**
     * Summary card height.
     */
    private static final int SUMMARY_CARD_HEIGHT = 120;
    /**
     * Summary max width.
     */
    private static final int SUMMARY_MAX_WIDTH = 520;
    /**
     * Card spacing.
     */
    private static final int CARD_SPACING = 10;
    /**
     * Card padding.
     */
    private static final int CARD_PADDING = 20;
    /**
     * Card radius.
     */
    private static final int CARD_RADIUS = 8;

    /**
     * Label for raw payload value.
     */
    private Label rawPayloadValueLabel;

    DashboardView() {
        setPadding(new Insets(DEFAULT_PADDING));
        setSpacing(DEFAULT_SPACING);
        setAlignment(Pos.TOP_LEFT);
        setStyle("-fx-background-color: #f5f5f5;");

        // Title
        final Label titleLabel = new Label("Meet Analytics Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, TITLE_FONT_SIZE));
        titleLabel.setTextFill(Color.web("#333333"));

        // // Users Present Section (Card)
        // final VBox usersPresentCard = createCard();
        // final Label usersPresentLabel = new Label("Users Present");
        // usersPresentLabel.setFont(Font.font("System", FontWeight.NORMAL,
        // LABEL_FONT_SIZE));
        // usersPresentLabel.setTextFill(Color.web("#666666"));

        // usersPresentValueLabel = new Label();
        // usersPresentValueLabel.setFont(Font.font("System", FontWeight.BOLD,
        // VALUE_FONT_SIZE));
        // usersPresentValueLabel.setTextFill(Color.web("#2196F3"));

        // usersPresentCard.getChildren().addAll(usersPresentLabel,
        // usersPresentValueLabel);

        // // Users Logged Out Section (Card)
        // final VBox usersLoggedOutCard = createCard();
        // final Label usersLoggedOutLabel = new Label("Users Logged Out");
        // usersLoggedOutLabel.setFont(Font.font("System", FontWeight.NORMAL,
        // LABEL_FONT_SIZE));
        // usersLoggedOutLabel.setTextFill(Color.web("#666666"));

        // usersLoggedOutValueLabel = new Label();
        // usersLoggedOutValueLabel.setFont(Font.font("System", FontWeight.BOLD,
        // VALUE_FONT_SIZE));
        // usersLoggedOutValueLabel.setTextFill(Color.web("#F44336"));

        // usersLoggedOutCard.getChildren().addAll(usersLoggedOutLabel,
        // usersLoggedOutValueLabel);

        // // Previous Meeting Summary Section (Card)
        // final VBox meetingSummaryCard = createCard();
        // meetingSummaryCard.setPrefHeight(SUMMARY_CARD_HEIGHT);
        // final Label meetingSummaryLabel = new Label("Previous Meeting Summary");
        // meetingSummaryLabel.setFont(Font.font("System", FontWeight.NORMAL,
        // LABEL_FONT_SIZE));
        // meetingSummaryLabel.setTextFill(Color.web("#666666"));

        // meetingSummaryValueLabel = new Label();
        // meetingSummaryValueLabel.setFont(Font.font("System", FontWeight.NORMAL,
        // SUMMARY_FONT_SIZE));
        // meetingSummaryValueLabel.setTextFill(Color.web("#757575"));
        // meetingSummaryValueLabel.setWrapText(true);
        // meetingSummaryValueLabel.setMaxWidth(SUMMARY_MAX_WIDTH);

        // meetingSummaryCard.getChildren().addAll(meetingSummaryLabel,
        // meetingSummaryValueLabel);

        // Raw payload card
        final VBox rawPayloadCard = createCard();
        final Label rawPayloadLabel = new Label("Final AI summary");
        rawPayloadLabel.setFont(Font.font("System", FontWeight.NORMAL, LABEL_FONT_SIZE));
        rawPayloadLabel.setTextFill(Color.web("#666666"));

        rawPayloadValueLabel = new Label();
        rawPayloadValueLabel.setFont(Font.font("Monospaced", FontWeight.NORMAL, SUMMARY_FONT_SIZE));
        rawPayloadValueLabel.setTextFill(Color.web("#424242"));
        rawPayloadValueLabel.setWrapText(true);
        rawPayloadValueLabel.setMaxWidth(SUMMARY_MAX_WIDTH);

        rawPayloadCard.getChildren().addAll(rawPayloadLabel, rawPayloadValueLabel);

        getChildren().addAll(
                titleLabel,
                // usersPresentCard,
                // usersLoggedOutCard,
                // meetingSummaryCard,
                rawPayloadCard);
    }

    private VBox createCard() {
        final VBox card = new VBox(CARD_SPACING);
        card.setPadding(new Insets(CARD_PADDING));
        card.setStyle("-fx-background-color: white; "
                + "-fx-background-radius: " + CARD_RADIUS + "; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }

    public void setViewModel(final DashboardViewModel viewModel) {
        rawPayloadValueLabel.textProperty().bind(
                viewModel.rawPayloadProperty());
    }
}
