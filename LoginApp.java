import javafx.application.Application;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.sql.*;

public class LoginApp extends Application {

    static final String C_BG      = "#0f1117";
    static final String C_SURFACE = "#181c27";
    static final String C_PANEL   = "#1e2333";
    static final String C_BORDER  = "#2a3045";
    static final String C_GOLD    = "#c9a84c";
    static final String C_GOLD2   = "#e8c97a";
    static final String C_TEXT    = "#e8eaf0";
    static final String C_MUTED   = "#6b7280";
    static final String C_GREEN   = "#22c55e";
    static final String C_RED     = "#ef4444";
    static final String C_BLUE    = "#3b82f6";

    BankService bank = new BankService();
    String loggedUser = "";

    static TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(
            "-fx-background-color:#1e2333; " +
            "-fx-border-color:#2a3045; -fx-border-radius:6; -fx-background-radius:6; " +
            "-fx-text-fill:#e8eaf0; -fx-prompt-text-fill:#6b7280; " +
            "-fx-padding:10 14; -fx-font-size:13px;"
        );
        f.focusedProperty().addListener((obs, o, n) ->
            f.setStyle(f.getStyle().replace(
                n ? "-fx-border-color:#2a3045" : "-fx-border-color:#c9a84c",
                n ? "-fx-border-color:#c9a84c" : "-fx-border-color:#2a3045"
            ))
        );
        return f;
    }

    static PasswordField styledPass(String prompt) {
        PasswordField f = new PasswordField();
        f.setPromptText(prompt);
        f.setStyle(
            "-fx-background-color:#1e2333; " +
            "-fx-border-color:#2a3045; -fx-border-radius:6; -fx-background-radius:6; " +
            "-fx-text-fill:#e8eaf0; -fx-prompt-text-fill:#6b7280; " +
            "-fx-padding:10 14; -fx-font-size:13px;"
        );
        f.focusedProperty().addListener((obs, o, n) ->
            f.setStyle(f.getStyle().replace(
                n ? "-fx-border-color:#2a3045" : "-fx-border-color:#c9a84c",
                n ? "-fx-border-color:#c9a84c" : "-fx-border-color:#2a3045"
            ))
        );
        return f;
    }

    static Label fieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#6b7280; -fx-font-size:10px; -fx-font-weight:bold;");
        return l;
    }

    static Button goldBtn(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle(
            "-fx-background-color:#c9a84c; -fx-text-fill:#0f1117; " +
            "-fx-font-weight:bold; -fx-font-size:13px; " +
            "-fx-padding:11 20; -fx-background-radius:7; -fx-cursor:hand;"
        );
        b.setOnMouseEntered(e -> b.setStyle(b.getStyle().replace("#c9a84c", "#e8c97a")));
        b.setOnMouseExited(e  -> b.setStyle(b.getStyle().replace("#e8c97a", "#c9a84c")));
        return b;
    }

    static Label statusLabel() {
        Label l = new Label();
        l.setWrapText(true);
        l.setStyle("-fx-font-size:12px;");
        return l;
    }

    static void setStatus(Label l, String msg, boolean ok) {
        l.setText((ok ? "[OK]  " : "[!]   ") + msg);
        l.setStyle("-fx-font-size:12px; -fx-text-fill:" + (ok ? C_GREEN : C_RED) + ";");
    }

    @Override
    public void start(Stage stage) {

        VBox leftPanel = new VBox();
        leftPanel.setPrefWidth(220);
        leftPanel.setStyle("-fx-background-color:" + C_PANEL + ";");
        leftPanel.setAlignment(Pos.CENTER);
        leftPanel.setPadding(new Insets(40));

        StackPane logo = new StackPane();
        Circle outerRing = new Circle(44);
        outerRing.setFill(Color.TRANSPARENT);
        outerRing.setStroke(Color.web(C_GOLD));
        outerRing.setStrokeWidth(1.5);
        Circle inner = new Circle(36);
        inner.setFill(Color.web("#c9a84c22"));
        Label bankIcon = new Label("Rs.");
        bankIcon.setStyle("-fx-font-size:20px; -fx-text-fill:" + C_GOLD + "; -fx-font-weight:bold;");
        logo.getChildren().addAll(outerRing, inner, bankIcon);

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), outerRing);
        pulse.setFromX(1); pulse.setToX(1.08);
        pulse.setFromY(1); pulse.setToY(1.08);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.play();

        Label appName = new Label("VAULT");
        appName.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + C_GOLD + ";");

        Label appTag = new Label("B A N K I N G");
        appTag.setStyle("-fx-font-size:10px; -fx-text-fill:" + C_MUTED + ";");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a3045;");
        sep.setPadding(new Insets(10, 0, 10, 0));

        Label quote = new Label("Secure. Reliable.\nAlways with you.");
        quote.setStyle("-fx-text-fill:#6b7280; -fx-font-size:11px; -fx-text-alignment:center;");
        quote.setTextAlignment(TextAlignment.CENTER);

        leftPanel.getChildren().addAll(
            logo,
            new Region() {{ setMinHeight(16); }},
            appName, appTag,
            new Region() {{ setMinHeight(20); }},
            sep, quote
        );

        VBox rightPanel = new VBox(14);
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setPadding(new Insets(50, 50, 50, 50));
        rightPanel.setStyle("-fx-background-color:" + C_BG + ";");

        Label loginTitle = new Label("Welcome back");
        loginTitle.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + C_TEXT + ";");

        Label loginSub = new Label("Sign in to access your account");
        loginSub.setStyle("-fx-font-size:12px; -fx-text-fill:" + C_MUTED + ";");

        TextField userField = styledField("Enter your username");
        userField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passField = styledPass("Enter your password");
        passField.setMaxWidth(Double.MAX_VALUE);

        Button loginBtn = goldBtn("Sign In");
        Label errMsg = statusLabel();

        loginBtn.setOnAction(e -> {
            String u = userField.getText().trim();
            String p = passField.getText().trim();
            if (u.isEmpty() || p.isEmpty()) {
                setStatus(errMsg, "Please fill in all fields.", false);
                return;
            }
            if (bank.login(u, p)) {
                loggedUser = u;
                openDashboard(stage);
            } else {
                setStatus(errMsg, "Invalid username or password.", false);
                passField.clear();
            }
        });

        rightPanel.getChildren().addAll(
            loginTitle, loginSub,
            new Region() {{ setMinHeight(8); }},
            fieldLabel("USERNAME"), userField,
            fieldLabel("PASSWORD"), passField,
            loginBtn, errMsg
        );

        HBox root = new HBox(leftPanel, rightPanel);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        root.setStyle("-fx-background-color:" + C_BG + ";");

        stage.setScene(new Scene(root, 620, 420));
        stage.setTitle("Vault Banking");
        stage.setResizable(false);
        stage.show();
    }

    private void openDashboard(Stage stage) {

        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(200);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color:" + C_PANEL + ";");

        Label logoText = new Label("VAULT BANK");
        logoText.setStyle("-fx-font-size:14px; -fx-font-weight:bold; -fx-text-fill:" + C_GOLD + "; -fx-padding:0 0 16 8;");
        sidebar.getChildren().add(logoText);

        Separator s1 = new Separator();
        s1.setStyle("-fx-background-color:#2a3045;");
        sidebar.getChildren().addAll(s1, new Region() {{ setMinHeight(8); }});

        HBox userChip = new HBox(10);
        userChip.setAlignment(Pos.CENTER_LEFT);
        userChip.setPadding(new Insets(6, 10, 10, 10));
        Circle avatar = new Circle(16);
        avatar.setFill(Color.web(C_GOLD + "33"));
        avatar.setStroke(Color.web(C_GOLD));
        avatar.setStrokeWidth(1);
        Label userInitial = new Label(loggedUser.substring(0, 1).toUpperCase());
        userInitial.setStyle("-fx-text-fill:" + C_GOLD + "; -fx-font-size:12px; -fx-font-weight:bold;");
        StackPane avatarStack = new StackPane(avatar, userInitial);
        VBox userInfo = new VBox(1,
            new Label(loggedUser) {{ setStyle("-fx-text-fill:#e8eaf0; -fx-font-size:12px; -fx-font-weight:bold;"); }},
            new Label("Account Holder") {{ setStyle("-fx-text-fill:#6b7280; -fx-font-size:10px;"); }}
        );
        userChip.getChildren().addAll(avatarStack, userInfo);
        sidebar.getChildren().add(userChip);

        Separator s2 = new Separator();
        s2.setStyle("-fx-background-color:#2a3045;");
        sidebar.getChildren().addAll(s2, new Region() {{ setMinHeight(8); }});

        StackPane contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color:" + C_BG + ";");

        Node homeScreen     = buildHomeScreen();
        Node createScreen   = buildCreateScreen();
        Node depositScreen  = buildTransactScreen("DEPOSIT");
        Node withdrawScreen = buildTransactScreen("WITHDRAW");
        Node transferScreen = buildTransferScreen();
        Node balanceScreen  = buildBalanceScreen();
        Node historyScreen  = buildHistoryScreen();

        contentArea.getChildren().addAll(
            homeScreen, createScreen, depositScreen,
            withdrawScreen, transferScreen, balanceScreen, historyScreen
        );
        showScreen(contentArea, 0);

        String[][] navItems = {
            {"Home",           "0"},
            {"Create Account", "1"},
            {"Deposit",        "2"},
            {"Withdraw",       "3"},
            {"Transfer",       "4"},
            {"Balance",        "5"},
            {"History",        "6"},
        };

        for (String[] item : navItems) {
            Button navBtn = makeNavBtn(item[0]);
            int idx = Integer.parseInt(item[1]);
            navBtn.setOnAction(e -> showScreen(contentArea, idx));
            sidebar.getChildren().add(navBtn);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Sign Out");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
            "-fx-background-color:transparent; -fx-text-fill:#ef4444; " +
            "-fx-font-size:12px; -fx-padding:9 14; -fx-cursor:hand; " +
            "-fx-border-color:#ef444433; -fx-border-radius:7; -fx-background-radius:7;"
        );
        logoutBtn.setOnAction(e -> {
            try { start(stage); } catch (Exception ex) { ex.printStackTrace(); }
        });

        sidebar.getChildren().addAll(spacer, logoutBtn);

        HBox root = new HBox(sidebar, contentArea);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        stage.setScene(new Scene(root, 860, 560));
        stage.setTitle("Vault Banking - Dashboard");
        stage.setResizable(true);
    }

    private Button makeNavBtn(String label) {
        Button b = new Button(label);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(
            "-fx-background-color:transparent; -fx-text-fill:#9ca3af; " +
            "-fx-font-size:12px; -fx-padding:9 14; -fx-cursor:hand; -fx-background-radius:7;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-color:#c9a84c18; -fx-text-fill:#c9a84c; " +
            "-fx-font-size:12px; -fx-padding:9 14; -fx-cursor:hand; -fx-background-radius:7;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-color:transparent; -fx-text-fill:#9ca3af; " +
            "-fx-font-size:12px; -fx-padding:9 14; -fx-cursor:hand; -fx-background-radius:7;"
        ));
        return b;
    }

    private void showScreen(StackPane area, int idx) {
        for (int i = 0; i < area.getChildren().size(); i++) {
            area.getChildren().get(i).setVisible(i == idx);
        }
    }

    private Node buildHomeScreen() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Welcome, " + loggedUser);
        title.setStyle("-fx-font-size:22px; -fx-font-weight:bold; -fx-text-fill:" + C_TEXT + ";");

        Label sub = new Label("Select an option from the sidebar to get started.");
        sub.setStyle("-fx-font-size:12px; -fx-text-fill:" + C_MUTED + ";");

        HBox cards = new HBox(12);
        cards.getChildren().addAll(
            quickCard("Deposit",       "Add money to any account",    C_GREEN),
            quickCard("Withdraw",      "Access your cash anytime",    C_GOLD),
            quickCard("Transfer",      "Send between accounts",       C_BLUE),
            quickCard("Check Balance", "View current account balance","#a78bfa")
        );

        HBox strip = new HBox(16);
        strip.setPadding(new Insets(18));
        strip.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-background-radius:10; " +
                       "-fx-border-color:" + C_BORDER + "; -fx-border-radius:10;");
        strip.setAlignment(Pos.CENTER_LEFT);
        Label info = new Label("Note: Use the sidebar to perform any banking operation. All transactions are recorded securely.");
        info.setStyle("-fx-text-fill:" + C_MUTED + "; -fx-font-size:12px;");
        info.setWrapText(true);
        strip.getChildren().add(info);

        root.getChildren().addAll(title, sub, cards, strip);
        return root;
    }

    private VBox quickCard(String title, String sub, String accent) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setPrefWidth(160);
        card.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-border-color:" + C_BORDER + "; " +
                      "-fx-border-radius:10; -fx-background-radius:10;");

        Region bar = new Region();
        bar.setPrefHeight(3);
        bar.setStyle("-fx-background-color:" + accent + "; -fx-background-radius:2;");

        Label t = new Label(title);
        t.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:" + accent + ";");
        t.setWrapText(true);

        Label s = new Label(sub);
        s.setStyle("-fx-font-size:10px; -fx-text-fill:" + C_MUTED + ";");
        s.setWrapText(true);

        card.getChildren().addAll(bar, t, s);
        return card;
    }

    private Node buildCreateScreen() {
        VBox root = screenShell("Create New Account", "Fill in the details below to open a new account.");

        TextField nameF = styledField("e.g. Arjun Kumar");
        nameF.setMaxWidth(Double.MAX_VALUE);
        TextField balF = styledField("e.g. 5000.00");
        balF.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Savings", "Current");
        typeBox.setValue("Savings");
        typeBox.setMaxWidth(Double.MAX_VALUE);
        typeBox.setStyle(
            "-fx-background-color:#1e2333; -fx-border-color:#2a3045; -fx-border-radius:6; " +
            "-fx-background-radius:6; -fx-text-fill:#e8eaf0; -fx-font-size:13px; -fx-padding:4 0;"
        );

        Button submitBtn = goldBtn("Create Account");
        Label status = statusLabel();

        submitBtn.setOnAction(e -> {
            try {
                String name = nameF.getText().trim();
                double bal  = Double.parseDouble(balF.getText().trim());
                String type = typeBox.getValue();
                if (name.isEmpty()) { setStatus(status, "Name is required.", false); return; }
                Account acc = bank.createAcc(name, bal, type);
                if (acc != null) {
                    setStatus(status, "Account created.  ID: " + acc.getAccId() +
                        "  |  " + type + "  |  Rs. " + String.format("%.2f", bal), true);
                    nameF.clear(); balF.clear();
                } else {
                    setStatus(status, "Failed to create account. Please try again.", false);
                }
            } catch (NumberFormatException ex) {
                setStatus(status, "Invalid amount entered.", false);
            }
        });

        VBox form = formCard(
            fieldLabel("ACCOUNT HOLDER NAME"), nameF,
            fieldLabel("INITIAL DEPOSIT (Rs.)"), balF,
            fieldLabel("ACCOUNT TYPE"), typeBox,
            submitBtn, status
        );

        ((VBox) root).getChildren().add(form);
        return root;
    }

    private Node buildTransactScreen(String type) {
        boolean isDeposit = type.equals("DEPOSIT");
        String label = isDeposit ? "Deposit Funds" : "Withdraw Funds";
        String desc  = isDeposit
            ? "Add funds to an account instantly."
            : "Withdraw funds. Insufficient balance will be rejected.";

        VBox root = screenShell(label, desc);

        TextField idF  = styledField("Account ID");   idF.setMaxWidth(Double.MAX_VALUE);
        TextField amtF = styledField("Amount (Rs.)"); amtF.setMaxWidth(Double.MAX_VALUE);
        Button btn = goldBtn(label);
        Label status = statusLabel();

        btn.setOnAction(e -> {
            try {
                int    id  = Integer.parseInt(idF.getText().trim());
                double amt = Double.parseDouble(amtF.getText().trim());
                if (isDeposit) bank.deposit(id, amt);
                else           bank.withdraw(id, amt);
                setStatus(status,
                    (isDeposit ? "Deposited" : "Withdrawn") + " Rs. " +
                    String.format("%.2f", amt) + "  —  Account #" + id, true);
                idF.clear(); amtF.clear();
            } catch (NumberFormatException ex) {
                setStatus(status, "Enter valid numeric values.", false);
            }
        });

        VBox form = formCard(
            fieldLabel("ACCOUNT ID"), idF,
            fieldLabel("AMOUNT (Rs.)"), amtF,
            btn, status
        );

        ((VBox) root).getChildren().add(form);
        return root;
    }

    private Node buildTransferScreen() {
        VBox root = screenShell("Transfer Funds", "Move money between two accounts securely.");

        TextField fromF = styledField("From Account ID"); fromF.setMaxWidth(Double.MAX_VALUE);
        TextField toF   = styledField("To Account ID");   toF.setMaxWidth(Double.MAX_VALUE);
        TextField amtF  = styledField("Amount (Rs.)");    amtF.setMaxWidth(Double.MAX_VALUE);
        Button btn = goldBtn("Execute Transfer");
        Label status = statusLabel();

        btn.setOnAction(e -> {
            try {
                int    from = Integer.parseInt(fromF.getText().trim());
                int    to   = Integer.parseInt(toF.getText().trim());
                double amt  = Double.parseDouble(amtF.getText().trim());
                bank.transfer(from, to, amt);
                setStatus(status,
                    "Transferred Rs. " + String.format("%.2f", amt) +
                    "  from Account #" + from + "  to Account #" + to, true);
                fromF.clear(); toF.clear(); amtF.clear();
            } catch (NumberFormatException ex) {
                setStatus(status, "Enter valid numeric values.", false);
            }
        });

        VBox form = formCard(
            fieldLabel("FROM ACCOUNT ID"), fromF,
            fieldLabel("TO ACCOUNT ID"),   toF,
            fieldLabel("AMOUNT (Rs.)"),    amtF,
            btn, status
        );

        ((VBox) root).getChildren().add(form);
        return root;
    }

    private Node buildBalanceScreen() {
        VBox root = screenShell("Check Balance", "Look up the current balance of any account.");

        TextField idF = styledField("Account ID");
        idF.setMaxWidth(Double.MAX_VALUE);
        Button btn = goldBtn("Fetch Balance");
        Label status = statusLabel();

        VBox resultCard = new VBox(10);
        resultCard.setPadding(new Insets(18));
        resultCard.setMaxWidth(420);
        resultCard.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-border-color:" + C_BORDER + "; " +
                            "-fx-border-radius:10; -fx-background-radius:10;");
        resultCard.setVisible(false);

        Label accIdLbl   = new Label();
        accIdLbl.setStyle("-fx-text-fill:" + C_MUTED + "; -fx-font-size:11px;");
        Label accNameLbl = new Label();
        accNameLbl.setStyle("-fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:" + C_TEXT + ";");
        Label accTypeLbl = new Label();
        accTypeLbl.setStyle("-fx-text-fill:" + C_MUTED + "; -fx-font-size:11px;");
        Label balLbl     = new Label();
        balLbl.setStyle("-fx-font-size:26px; -fx-font-weight:bold; -fx-text-fill:" + C_GREEN + ";");
        Separator rs = new Separator();
        rs.setStyle("-fx-background-color:#2a3045;");

        resultCard.getChildren().addAll(accIdLbl, accNameLbl, accTypeLbl, rs, balLbl);

        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idF.getText().trim());
                Account acc = bank.findAcc(id);
                if (acc != null) {
                    accIdLbl.setText("Account  #" + acc.getAccId());
                    accNameLbl.setText(acc.getAccName());
                    accTypeLbl.setText(acc.getAccType() + " Account");
                    balLbl.setText("Rs. " + String.format("%,.2f", acc.getBalance()));
                    resultCard.setVisible(true);
                    status.setText("");
                } else {
                    setStatus(status, "Account #" + id + " not found.", false);
                    resultCard.setVisible(false);
                }
            } catch (NumberFormatException ex) {
                setStatus(status, "Enter a valid account ID.", false);
            }
        });

        VBox form = formCard(fieldLabel("ACCOUNT ID"), idF, btn, status);
        ((VBox) root).getChildren().addAll(form, resultCard);
        return root;
    }

    private Node buildHistoryScreen() {
        VBox root = screenShell("Transaction History", "View all transactions for an account.");

        TextField idF = styledField("Account ID");
        idF.setMaxWidth(Double.MAX_VALUE);
        Button btn = goldBtn("Load Transactions");
        Label status = statusLabel();

        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color:transparent; -fx-background:#0f1117;");
        scroll.setFitToWidth(true);
        scroll.setVisible(false);

        VBox tableWrap = new VBox(0);
        tableWrap.setStyle("-fx-background-color:" + C_SURFACE + "; -fx-border-color:" + C_BORDER + "; " +
                           "-fx-border-radius:10; -fx-background-radius:10;");

        HBox header = new HBox();
        header.setPadding(new Insets(10, 14, 10, 14));
        header.setStyle("-fx-background-color:#1e2333; -fx-border-radius:10 10 0 0; -fx-background-radius:10 10 0 0;");
        header.getChildren().addAll(
            tableCell("TXN ID", 70,  true),
            tableCell("TYPE",   130, true),
            tableCell("AMOUNT", 120, true),
            tableCell("DATE",   200, true)
        );
        tableWrap.getChildren().add(header);
        scroll.setContent(tableWrap);

        btn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idF.getText().trim());
                tableWrap.getChildren().remove(1, tableWrap.getChildren().size());

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT * FROM transactions WHERE acc_id = ? ORDER BY txn_id DESC";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();

                    boolean found = false;
                    boolean alt   = false;

                    while (rs.next()) {
                        found = true;
                        int    txnId  = rs.getInt("txn_id");
                        String type   = rs.getString("type");
                        double amount = rs.getDouble("amount");
                        String date   = rs.getTimestamp("date").toString().substring(0, 16);

                        boolean isCredit = type.equals("DEPOSIT") || type.equals("TRANSFER_IN");
                        String amtColor  = isCredit ? C_GREEN : C_RED;
                        String amtStr    = (isCredit ? "+" : "-") + "Rs. " + String.format("%,.2f", amount);

                        HBox row = new HBox();
                        row.setPadding(new Insets(9, 14, 9, 14));
                        row.setStyle("-fx-background-color:" + (alt ? "#181c27" : "#1a1f2e") + ";");
                        alt = !alt;

                        Label typeLabel = new Label(type);
                        typeLabel.setStyle(
                            "-fx-font-size:11px; -fx-text-fill:" + amtColor + "; " +
                            "-fx-background-color:" + amtColor + "22; " +
                            "-fx-padding:2 8; -fx-background-radius:4;"
                        );
                        typeLabel.setPrefWidth(118);

                        Label amtLabel = new Label(amtStr);
                        amtLabel.setStyle(
                            "-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:" + amtColor + ";"
                        );
                        amtLabel.setPrefWidth(120);

                        row.getChildren().addAll(
                            tableCell(String.valueOf(txnId), 70, false),
                            new HBox(typeLabel) {{ setPrefWidth(130); setAlignment(Pos.CENTER_LEFT); }},
                            new HBox(amtLabel)  {{ setPrefWidth(120); setAlignment(Pos.CENTER_LEFT); }},
                            tableCell(date, 200, false)
                        );
                        tableWrap.getChildren().add(row);
                    }

                    if (!found) {
                        Label empty = new Label("No transactions found for account #" + id);
                        empty.setStyle("-fx-text-fill:" + C_MUTED + "; -fx-font-size:12px; -fx-padding:16;");
                        tableWrap.getChildren().add(empty);
                    }

                    scroll.setVisible(true);
                    status.setText("");

                } catch (Exception ex) {
                    setStatus(status, "Database error: " + ex.getMessage(), false);
                    ex.printStackTrace();
                }

            } catch (NumberFormatException ex) {
                setStatus(status, "Enter a valid account ID.", false);
            }
        });

        VBox form = formCard(fieldLabel("ACCOUNT ID"), idF, btn, status);
        ((VBox) root).getChildren().addAll(form, scroll);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return root;
    }

    private Label tableCell(String text, double w, boolean isHeader) {
        Label l = new Label(text);
        l.setPrefWidth(w);
        l.setStyle(
            "-fx-text-fill:" + (isHeader ? C_MUTED : C_TEXT) + "; " +
            "-fx-font-size:" + (isHeader ? "10" : "12") + "px; " +
            (isHeader ? "-fx-font-weight:bold;" : "")
        );
        return l;
    }

    private VBox screenShell(String title, String sub) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(32));

        Label t = new Label(title);
        t.setStyle("-fx-font-size:19px; -fx-font-weight:bold; -fx-text-fill:" + C_TEXT + ";");

        Label s = new Label(sub);
        s.setStyle("-fx-font-size:12px; -fx-text-fill:" + C_MUTED + ";");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#2a3045;");

        root.getChildren().addAll(t, s, sep);
        return root;
    }

    private VBox formCard(Node... nodes) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color:" + C_SURFACE + "; -fx-border-color:" + C_BORDER + "; " +
            "-fx-border-radius:10; -fx-background-radius:10;"
        );
        card.getChildren().addAll(nodes);
        return card;
    }

    public static void main(String[] args) {
        launch();
    }
}

//javac -cp ".;lib/mysql-connector-j-9.6.0.jar" --module-path "C:\Users\athit\Downloads\openjfx-26_windows-x64_bin-sdk\javafx-sdk-26\lib" --add-modules javafx.controls *.java
//java  -cp ".;lib/mysql-connector-j-9.6.0.jar" --module-path "C:\Users\athit\Downloads\openjfx-26_windows-x64_bin-sdk\javafx-sdk-26\lib" --add-modules javafx.controls LoginApp