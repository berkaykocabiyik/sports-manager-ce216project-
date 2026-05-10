package game.gui;

import game.GameFactory;
import game.GameState;
import game.Formation;
import game.Match;
import game.MatchStatus;
import game.Player;
import game.SaveService;
import game.Tactic;
import game.Team;
import game.SportType;
import game.FootballPlayer;
import game.MatchSimulationEngine;
import game.VolleyballMatch;
import game.VolleyballPlayer;
import game.VolleyballTeam;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SportsManagerFrame extends JFrame {

    private static final Color BACKGROUND = new Color(239, 243, 247);
    private static final Color PANEL = new Color(255, 255, 255);
    private static final Color PRIMARY = new Color(18, 83, 112);
    private static final Color SECONDARY = new Color(231, 238, 245);
    private static final Color SECONDARY_DARK = new Color(203, 216, 229);
    private static final Color ACCENT = new Color(194, 118, 38);
    private static final Color TEXT = new Color(31, 41, 55);
    private static final Color MUTED = new Color(94, 107, 124);
    private static final Color BORDER = new Color(204, 214, 226);
    private static final Color TABLE_HEADER = new Color(222, 231, 240);
    private static final Color SELECTION = new Color(203, 225, 239);
    private static final DateTimeFormatter SAVE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private GameState state;

    public SportsManagerFrame() {
        super("Sports Manager - Voleybol");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignored) {
            // The default Swing look and feel is fine when the system one is unavailable.
        }
        configureSwingColors();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 760);
        setMinimumSize(new Dimension(1080, 720));
        setLocationByPlatform(true);
        showMainMenu();
    }

    private void showMainMenu() {
        JPanel root = basePanel("Sports Manager", "Voleybol Ligi Simülasyonu");

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.ipady = 12;
        constraints.insets.set(8, 0, 8, 0);

        JButton newFootballGame = primaryButton("Yeni Oyun: Futbol");
        newFootballGame.addActionListener(event -> showTeamSelection(SportType.FOOTBALL));
        JButton newVolleyballGame = primaryButton("Yeni Oyun: Voleybol");
        newVolleyballGame.addActionListener(event -> showTeamSelection(SportType.VOLLEYBALL));
        JButton load = secondaryButton("Oyunu Yükle");
        load.addActionListener(event -> loadGame());
        JButton quit = secondaryButton("Çıkış");
        quit.addActionListener(event -> dispose());

        center.add(newFootballGame, constraints);
        center.add(newVolleyballGame, constraints);
        center.add(load, constraints);
        center.add(quit, constraints);
        root.add(center, BorderLayout.CENTER);

        root.add(sportCalculationPanel(), BorderLayout.SOUTH);

        replaceContent(root);
    }

    private void showTeamSelection(SportType sportType) {
        state = GameFactory.createGameState(sportType);

        JPanel root = basePanel("Yeni Oyun", "Yönetilecek " + sportType.getDisplayName().toLowerCase() + " takımını seç");
        DefaultListModel<Team> model = new DefaultListModel<>();
        for (Team team : state.getLeague().getActiveTeams()) {
            model.addElement(team);
        }

        JList<Team> list = new JList<>(model);
        list.setBackground(PANEL);
        list.setForeground(TEXT);
        list.setSelectionBackground(SELECTION);
        list.setSelectionForeground(TEXT);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.setFixedCellHeight(38);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Team team = (Team) value;
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list,
                    team.getName() + " - " + team.getCity()
                        + " | Güç: " + String.format("%.1f", team.calculateTeamStrength()),
                    index,
                    isSelected,
                    cellHasFocus
                );
                label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                if (isSelected) {
                    label.setBackground(SELECTION);
                    label.setForeground(TEXT);
                } else {
                    label.setBackground(PANEL);
                    label.setForeground(TEXT);
                }
                return label;
            }
        });

        JTextArea teamPreview = textArea();
        Runnable refreshPreview = () -> teamPreview.setText(teamPreviewText(list.getSelectedValue()));
        list.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                refreshPreview.run();
            }
        });
        refreshPreview.run();

        JButton start = primaryButton("Lige Başla");
        start.addActionListener(event -> {
            Team selected = list.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Bir takım seçmelisin.");
                return;
            }
            state.setManagedTeam(selected);
            state.addRecentResult("Yönetilen takım: " + selected.getName());
            showHub();
        });

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(backButton("Ana Menü", this::showMainMenu));
        actions.add(start);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            wrap(new JScrollPane(list)),
            wrap(new JScrollPane(teamPreview))
        );
        split.setResizeWeight(0.55);
        split.setBorder(BorderFactory.createEmptyBorder());

        root.add(split, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showHub() {
        if (state == null) {
            showMainMenu();
            return;
        }

        JPanel root = basePanel("Lig Merkezi", hubSubtitle());

        JPanel actions = new JPanel(new GridLayout(3, 3, 12, 12));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JButton playWeek = primaryButton(state.isSeasonFinished() ? "Sezon Bitti" : "Haftayı Oyna");
        playWeek.setEnabled(!state.isSeasonFinished());
        playWeek.addActionListener(event -> playCurrentWeek());

        JButton standings = secondaryButton("Puan Tablosu");
        standings.addActionListener(event -> showStandings());
        JButton fixture = secondaryButton("Fikstür");
        fixture.addActionListener(event -> showFixture());
        JButton rules = secondaryButton("Lig Kuralları");
        rules.addActionListener(event -> showLeagueRules());
        JButton preMatch = secondaryButton("Maç Öncesi");
        preMatch.addActionListener(event -> showPreMatch());
        JButton squad = secondaryButton("Kadro / Antrenman");
        squad.addActionListener(event -> showSquad());
        JButton save = secondaryButton("Kaydet");
        save.addActionListener(event -> saveGame());
        JButton load = secondaryButton("Yükle");
        load.addActionListener(event -> loadGame());
        JButton seasonSummary = secondaryButton("Sezon Özeti");
        seasonSummary.addActionListener(event -> showSeasonSummary());
        JButton menu = secondaryButton("Ana Menü");
        menu.addActionListener(event -> showMainMenu());

        actions.add(playWeek);
        actions.add(standings);
        actions.add(fixture);
        actions.add(rules);
        actions.add(preMatch);
        actions.add(squad);
        actions.add(save);
        actions.add(load);
        actions.add(seasonSummary);
        actions.add(menu);

        JTextArea recent = textArea();
        if (state.getRecentResults().isEmpty()) {
            recent.setText("Son kayıt: " + lastSaveText() + "\nHenüz maç oynanmadı.");
        } else {
            recent.setText("Son kayıt: " + lastSaveText() + "\n\n"
                + String.join("\n", state.getRecentResults()));
            recent.setCaretPosition(recent.getDocument().getLength());
        }

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(actions, BorderLayout.NORTH);
        center.add(wrap(new JScrollPane(recent)), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        replaceContent(root);
    }

    private void playCurrentWeek() {
        if (state == null || state.isSeasonFinished()) {
            return;
        }

        int playedWeek = state.getCurrentWeek();
        List<Match> matches = state.playCurrentWeek();
        showWeekResults(playedWeek, matches);
    }

    private void showWeekResults(int week, List<Match> matches) {
        JPanel root = basePanel("Hafta " + week + " Sonuçları", hubSubtitle());

        DefaultTableModel tableModel = new DefaultTableModel(
            new String[] {"Ev Sahibi", "Skor", "Deplasman", "Setler", "Maçın Oyuncusu", "Kazanan"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        StringBuilder managedDetails = new StringBuilder();
        for (Match match : matches) {
            tableModel.addRow(new Object[] {
                match.getHomeTeam().getName(),
                match.getHomeScore() + "-" + match.getAwayScore(),
                match.getAwayTeam().getName(),
                setScoreText(match),
                manOfMatchText(match),
                winnerText(match)
            });

            if (state.getManagedTeam() != null
                    && (match.getHomeTeam().getId().equals(state.getManagedTeam().getId())
                    || match.getAwayTeam().getId().equals(state.getManagedTeam().getId()))) {
                managedDetails.append(managedMatchDetails(match));
            }
        }

        JTable table = table(tableModel);
        highlightRowsForManagedTeam(table, 0, 2);
        JTextArea details = textArea();
        details.setText(managedDetails.isEmpty()
            ? "Bu hafta yönetilen takımın maçı yok."
            : managedDetails.toString());

        JSplitPane split = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            wrap(new JScrollPane(table)),
            wrap(new JScrollPane(details))
        );
        split.setResizeWeight(0.45);
        split.setBorder(BorderFactory.createEmptyBorder());

        root.add(split, BorderLayout.CENTER);

        JPanel actions = bottomBar(backButton("Lig Merkezi", this::showHub));
        JButton nextWeek = primaryButton(state.isSeasonFinished() ? "Sezon Bitti" : "Sonraki Haftayı Oyna");
        nextWeek.setEnabled(!state.isSeasonFinished());
        nextWeek.addActionListener(event -> playCurrentWeek());
        actions.add(nextWeek);
        root.add(actions, BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showPreMatch() {
        JPanel root = basePanel("Maç Öncesi", hubSubtitle());
        Match nextMatch = state.getNextManagedMatch();
        if (nextMatch == null) {
            JTextArea empty = textArea();
            empty.setText("Planlanacak maç bulunamadı.");
            root.add(wrap(new JScrollPane(empty)), BorderLayout.CENTER);
            root.add(bottomBar(backButton("Lig Merkezi", this::showHub)), BorderLayout.SOUTH);
            replaceContent(root);
            return;
        }

        Formation[] formations = state.getLeague().getSport().getFormationFactory().getAllFormations();
        Tactic[] tactics = state.getLeague().getSport().getValidTactics();
        JComboBox<Formation> formationBox = formationCombo(formations);
        JComboBox<Tactic> tacticBox = tacticCombo(tactics);

        if (state.getPlannedFormation() != null) {
            selectFormation(formationBox, state.getPlannedFormation().getName());
        }
        if (state.getPlannedTactic() != null) {
            selectTactic(tacticBox, state.getPlannedTactic().getName());
        }

        JTextArea preview = textArea();
        Runnable refreshPreview = () -> preview.setText(preMatchPreview(
            nextMatch,
            (Formation) formationBox.getSelectedItem(),
            (Tactic) tacticBox.getSelectedItem()
        ));
        formationBox.addActionListener(event -> refreshPreview.run());
        tacticBox.addActionListener(event -> refreshPreview.run());
        refreshPreview.run();

        JPanel controls = new JPanel(new GridLayout(2, 2, 12, 8));
        controls.setOpaque(false);
        JLabel formationLabel = new JLabel("Diziliş");
        JLabel tacticLabel = new JLabel("Taktik");
        formationLabel.setForeground(TEXT);
        tacticLabel.setForeground(TEXT);
        controls.add(formationLabel);
        controls.add(tacticLabel);
        controls.add(formationBox);
        controls.add(tacticBox);

        JButton savePlan = primaryButton("Planı Kaydet");
        savePlan.addActionListener(event -> {
            String result = state.planManagedMatch(
                (Formation) formationBox.getSelectedItem(),
                (Tactic) tacticBox.getSelectedItem()
            );
            JOptionPane.showMessageDialog(this, result);
            showHub();
        });

        JPanel content = new JPanel(new BorderLayout(0, 14));
        content.setOpaque(false);
        content.add(controls, BorderLayout.NORTH);
        content.add(wrap(new JScrollPane(preview)), BorderLayout.CENTER);

        JPanel actions = bottomBar(backButton("Lig Merkezi", this::showHub));
        actions.add(savePlan);

        root.add(content, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showLeagueRules() {
        JPanel root = basePanel("Lig Kuralları", state.getLeague().getSport().getName());
        JTextArea rules = textArea();
        rules.setText(leagueRulesText());
        rules.setCaretPosition(0);

        root.add(wrap(new JScrollPane(rules)), BorderLayout.CENTER);
        root.add(bottomBar(backButton("Lig Merkezi", this::showHub)), BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showStandings() {
        JPanel root = basePanel("Puan Tablosu", state.getLeague().getSport().getTiebreakerRule());
        DefaultTableModel model = new DefaultTableModel(
            new String[] {"Sıra", "Takım", "O", "G", "M", "P", "Set", "Sayı", "Set Ort."}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Team[] ranked = state.getLeague().getStandings().getRankedTeams();
        for (int i = 0; i < ranked.length; i++) {
            Team team = ranked[i];
            model.addRow(new Object[] {
                i + 1,
                team.getName(),
                state.getLeague().getStandings().getTeamMatchesPlayed(team),
                team.getTotalWins(),
                team.getTotalLosses(),
                state.getLeague().getStandings().getTeamPoints(team),
                scoreFor(team),
                detailScoreFor(team),
                averageFor(team)
            });
        }

        JTable table = table(model);
        highlightRowsForManagedTeam(table, 1);
        root.add(wrap(new JScrollPane(table)), BorderLayout.CENTER);
        root.add(bottomBar(backButton("Lig Merkezi", this::showHub)), BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showFixture() {
        JPanel root = basePanel("Fikstür", "Toplam hafta: " + state.getLeague().getFixture().getTotalWeeks());
        JComboBox<Integer> weekSelector = new JComboBox<>();
        weekSelector.setBackground(PANEL);
        weekSelector.setForeground(TEXT);
        for (int i = 1; i <= state.getLeague().getFixture().getTotalWeeks(); i++) {
            weekSelector.addItem(i);
        }
        weekSelector.setSelectedItem(Math.min(state.getCurrentWeek(), state.getLeague().getFixture().getTotalWeeks()));

        DefaultTableModel model = new DefaultTableModel(
            new String[] {"Ev Sahibi", "Skor / Tarih", "Deplasman", "Durum"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = table(model);
        highlightRowsForManagedTeam(table, 0, 2);
        Runnable reload = () -> fillFixtureModel(model, (Integer) weekSelector.getSelectedItem());
        weekSelector.addActionListener(event -> reload.run());
        reload.run();

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        JLabel weekLabel = new JLabel("Hafta:");
        weekLabel.setForeground(TEXT);
        top.add(weekLabel);
        top.add(weekSelector);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(top, BorderLayout.NORTH);
        center.add(wrap(new JScrollPane(table)), BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(bottomBar(backButton("Lig Merkezi", this::showHub)), BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showSquad() {
        JPanel root = basePanel("Kadro / Antrenman", state.getManagedTeam().getName()
            + " | Haftalık antrenman hakkı: "
            + state.getTrainingSessionsRemaining() + "/" + state.getWeeklyTrainingSessions());
        DefaultTableModel model = squadModel();
        JTable table = table(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        highlightUnavailablePlayers(table, 5);

        JButton train = primaryButton("Seçili Oyuncuyu Çalıştır");
        train.addActionListener(event -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Antrenman için bir oyuncu seç.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            String playerId = (String) model.getValueAt(modelRow, 0);
            Player player = findManagedPlayer(playerId);
            if (player == null) {
                return;
            }
            String result = state.trainManagedPlayer(player);
            JOptionPane.showMessageDialog(this, result);
            showSquad();
        });

        JPanel actions = bottomBar(backButton("Lig Merkezi", this::showHub));
        actions.add(train);

        JLabel trainingInfo = new JLabel("Koç kalitesi yüksekse oyuncu gelişimi daha hızlı olur; sakat oyuncular antrenmana çıkamaz.");
        trainingInfo.setForeground(MUTED);
        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(trainingInfo, BorderLayout.NORTH);
        center.add(wrap(new JScrollPane(table)), BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void showSeasonSummary() {
        JPanel root = basePanel("Sezon Özeti",
            state.isSeasonFinished() ? "Sezon tamamlandı" : "Sezon devam ediyor");

        DefaultTableModel model = new DefaultTableModel(
            new String[] {"Sıra", "Takım", "O", "G", "M", "P", "Set", "Sayı"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Team[] ranked = state.getLeague().getStandings().getRankedTeams();
        for (int i = 0; i < Math.min(5, ranked.length); i++) {
            Team team = ranked[i];
            model.addRow(new Object[] {
                i + 1,
                team.getName(),
                state.getLeague().getStandings().getTeamMatchesPlayed(team),
                team.getTotalWins(),
                team.getTotalLosses(),
                state.getLeague().getStandings().getTeamPoints(team),
                scoreFor(team),
                detailScoreFor(team)
            });
        }

        JTable table = table(model);
        highlightRowsForManagedTeam(table, 1);

        JTextArea summary = textArea();
        Team champion = state.getChampion();
        String championText = champion == null ? "-" : champion.getName();
        String managedRank = state.getManagedTeam() == null
            ? "-"
            : String.valueOf(state.getLeague().getStandings().getTeamRank(state.getManagedTeam()));
        String managedTeamName = state.getManagedTeam() == null ? "-" : state.getManagedTeam().getName();
        summary.setText(
            "Şampiyon / Lider: " + championText + "\n"
                + "Yönetilen takım: " + managedTeamName
                + " | Sıra: " + managedRank + "\n"
                + "Oynanan maç: " + state.getLeague().getPlayedMatches().size()
                + "/" + state.getLeague().getFixture().getAllMatches().length + "\n"
                + "Kalan hafta: " + Math.max(0, state.getLeague().getFixture().getTotalWeeks() - state.getCurrentWeek() + 1)
                + "\n\nSezon istatistikleri:\n"
                + seasonStatsText()
                + "\n\nSon kayıtlar:\n"
                + String.join("\n", state.getRecentResults())
        );
        summary.setCaretPosition(0);

        JSplitPane split = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT,
            wrap(new JScrollPane(table)),
            wrap(new JScrollPane(summary))
        );
        split.setResizeWeight(0.35);
        split.setBorder(BorderFactory.createEmptyBorder());

        root.add(split, BorderLayout.CENTER);
        root.add(bottomBar(backButton("Lig Merkezi", this::showHub)), BorderLayout.SOUTH);
        replaceContent(root);
    }

    private void fillFixtureModel(DefaultTableModel model, Integer week) {
        model.setRowCount(0);
        if (week == null) {
            return;
        }
        for (Match match : state.getLeague().getFixture().getWeekMatches(week)) {
            model.addRow(new Object[] {
                match.getHomeTeam().getName(),
                match.getStatus() == MatchStatus.FINISHED
                    ? match.getHomeScore() + "-" + match.getAwayScore()
                    : match.getMatchDate().toLocalDate(),
                match.getAwayTeam().getName(),
                statusLabel(match)
            });
        }
    }

    private DefaultTableModel squadModel() {
        String[] statHeaders = state.getSelectedSport() == SportType.FOOTBALL
            ? new String[] {"Gol", "Asist", "Pas"}
            : new String[] {"Smaç", "Blok", "Ace"};
        DefaultTableModel model = new DefaultTableModel(
            new String[] {"ID", "Oyuncu", "Pozisyon", "Yaş", "OVR", "Durum",
                statHeaders[0], statHeaders[1], statHeaders[2]}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Player player : state.getManagedTeam().getSquad()) {
            if (player != null) {
                model.addRow(new Object[] {
                    player.getId(),
                    player.getName(),
                    player.getPosition().getDisplayName(),
                    player.getAge(),
                    overallRating(player),
                    player.canPlay()
                        ? "Hazır"
                        : "Sakat (" + player.getInjuredMatchesRemaining() + ")",
                    statOne(player),
                    statTwo(player),
                    statThree(player)
                });
            }
        }
        return model;
    }

    private Player findManagedPlayer(String playerId) {
        for (Player player : state.getManagedTeam().getSquad()) {
            if (player != null && player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    private JPanel sportCalculationPanel() {
        JTextArea area = textArea();
        area.setText(
            "Spor seçimi\n"
                + "İlk spor: Futbol\n"
                + "İkinci spor öğrenci numarası hesabına göre belirlenir.\n"
                + "Öğrenci numaraları: 20210602036, 20200602038, 20240602056, 20220602008\n"
                + "Son rakamlar: 6 + 8 + 6 + 8 = 28\n"
                + "28 mod 3 = 1 -> Voleybol\n"
                + "Eşleme: 0 Basketbol, 1 Voleybol, 2 Hentbol"
        );
        area.setRows(7);
        return wrap(new JScrollPane(area));
    }

    private String leagueRulesText() {
        StringBuilder builder = new StringBuilder();
        builder.append(state.getLeague().getName()).append("\n\n");
        builder.append("Spor: ").append(state.getLeague().getSport().getName()).append("\n");
        builder.append("Takım sayısı: ").append(state.getLeague().getTeamCount()).append("\n");
        builder.append("Oyuncu sayısı: ").append(state.getLeague().getSport().getPlayerCount())
            .append(" asil + ").append(state.getLeague().getSport().getSubstituteCount())
            .append(" yedek\n");
        builder.append("Hafta / maç: ").append(state.getLeague().getFixture().getTotalWeeks())
            .append(" hafta, ").append(state.getLeague().getFixture().getAllMatches().length)
            .append(" maç\n");
        builder.append("Puan sistemi: Galibiyet ")
            .append(state.getLeague().getSport().getWinPoints())
            .append(", Beraberlik ").append(state.getLeague().getSport().getDrawPoints())
            .append(", Mağlubiyet ").append(state.getLeague().getSport().getLossPoints())
            .append("\n");
        builder.append("Sıralama: ").append(state.getLeague().getSport().getTiebreakerRule()).append("\n");
        builder.append("Sakatlık: ").append(state.getLeague().getSport().getInjuryRule()).append("\n\n");

        builder.append("Pozisyonlar:\n");
        for (var position : state.getLeague().getSport().getValidPositions()) {
            builder.append("- ").append(position.getDisplayName()).append("\n");
        }

        builder.append("\nTaktikler:\n");
        for (Tactic tactic : state.getLeague().getSport().getValidTactics()) {
            builder.append("- ").append(tactic.getName())
                .append(" | Agresiflik: ").append(tactic.getAggressivenessLevel())
                .append(" | Savunma: ").append(tactic.getDefenseStrength())
                .append(" | ").append(tactic.getDescription()).append("\n");
        }

        builder.append("\nDizilişler:\n");
        for (Formation formation : state.getLeague().getSport().getFormationFactory().getAllFormations()) {
            builder.append("- ").append(formation.getName())
                .append(" | Hücum: ").append(formation.getOffensiveStrength())
                .append(" | Savunma: ").append(formation.getDefensiveStrength())
                .append(" | ").append(formation.getDescription()).append("\n");
        }

        if (state.getSelectedSport() == SportType.VOLLEYBALL) {
            builder.append("\nVoleybol set kuralı:\n")
                .append("- Maç 3 set alan takımın galibiyetiyle biter.\n")
                .append("- İlk 4 set 25 sayı, final seti 15 sayı hedeflidir.\n")
                .append("- Set kazanmak için en az 2 sayı fark gerekir.\n")
                .append("- Beraberlik yoktur.\n");
        } else {
            builder.append("\nFutbol maç kuralı:\n")
                .append("- Maç 2 devre, toplam 90 dakika simüle edilir.\n")
                .append("- Galibiyet, beraberlik ve mağlubiyet puan tablosuna işlenir.\n");
        }

        return builder.toString();
    }

    private String teamPreviewText(Team team) {
        if (team == null) {
            return "Takım seçilmedi.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(team.getName()).append(" - ").append(team.getCity()).append("\n");
        builder.append("Koç: ").append(team.getCoach().getName())
            .append(" | Uzmanlık: ").append(team.getCoach().getSpecialty())
            .append(" | Rating: ").append(String.format("%.1f", team.getCoach().getCoachingRating()))
            .append(" | Deneyim: ").append(team.getCoach().getExperience()).append(" yıl\n");
        builder.append("Takım gücü: ").append(String.format("%.2f", team.calculateTeamStrength()))
            .append(" | Kadro: ").append(squadSize(team))
            .append(" | Hazır oyuncu: ").append(availablePlayerCount(team)).append("\n\n");
        builder.append(state.getSelectedSport() == SportType.FOOTBALL ? "İlk 11 önerisi:\n" : "İlk 6 önerisi:\n");
        for (Player player : team.getLineup()) {
            if (player != null) {
                builder.append("- ").append(player.getName())
                    .append(" | ").append(player.getPosition().getDisplayName())
                    .append(" | OVR ").append(overallRating(player))
                    .append("\n");
            }
        }
        return builder.toString();
    }

    private String seasonStatsText() {
        Player bestPlayer = null;
        double bestPerformance = -1;
        Player leaderOne = null;
        Player leaderTwo = null;
        Player leaderThree = null;
        int maxOne = -1;
        int maxTwo = -1;
        int maxThree = -1;
        int injuredCount = 0;

        for (Team team : state.getLeague().getActiveTeams()) {
            for (Player player : team.getSquad()) {
                if (player == null) continue;
                double performance = player.calculatePerformance();
                if (performance > bestPerformance) {
                    bestPerformance = performance;
                    bestPlayer = player;
                }
                if (statOne(player) > maxOne) {
                    maxOne = statOne(player);
                    leaderOne = player;
                }
                if (statTwo(player) > maxTwo) {
                    maxTwo = statTwo(player);
                    leaderTwo = player;
                }
                if (statThree(player) > maxThree) {
                    maxThree = statThree(player);
                    leaderThree = player;
                }
                if (player.isInjured()) {
                    injuredCount++;
                }
            }
        }

        String[] labels = state.getSelectedSport() == SportType.FOOTBALL
            ? new String[] {"Gol", "Asist", "Pas"}
            : new String[] {"Smaç", "Blok", "Ace"};

        return "En iyi oyuncu: " + playerText(bestPlayer)
            + "\n" + labels[0] + " lideri: " + playerText(leaderOne) + " (" + Math.max(0, maxOne) + ")"
            + "\n" + labels[1] + " lideri: " + playerText(leaderTwo) + " (" + Math.max(0, maxTwo) + ")"
            + "\n" + labels[2] + " lideri: " + playerText(leaderThree) + " (" + Math.max(0, maxThree) + ")"
            + "\nSakat oyuncu sayısı: " + injuredCount + "\n";
    }

    private String playerText(Player player) {
        return player == null ? "-" : player.getName() + " | OVR " + overallRating(player);
    }

    private String preMatchPreview(Match match, Formation formation, Tactic tactic) {
        Team opponent = (
            match.getHomeTeam().getId().equals(state.getManagedTeam().getId())
                ? match.getAwayTeam()
                : match.getHomeTeam()
        );

        StringBuilder builder = new StringBuilder();
        builder.append("Sıradaki maç: ")
            .append(match.getHomeTeam().getName())
            .append(" - ")
            .append(match.getAwayTeam().getName())
            .append("\nTarih: ")
            .append(match.getMatchDate().toLocalDate())
            .append("\n\n");
        builder.append("Yönetilen takım\n")
            .append("- Koç: ").append(state.getManagedTeam().getCoach().getName())
            .append(" | Uzmanlık: ").append(state.getManagedTeam().getCoach().getSpecialty())
            .append("\n- Güç: ").append(String.format("%.2f", state.getManagedTeam().calculateTeamStrength()))
            .append(" | Hazır oyuncu: ").append(availablePlayerCount(state.getManagedTeam()))
            .append("\n\n");
        builder.append("Rakip\n")
            .append("- ").append(opponent.getName())
            .append(" | Koç: ").append(opponent.getCoach().getName())
            .append(" | Güç: ").append(String.format("%.2f", opponent.calculateTeamStrength()))
            .append("\n\n");
        builder.append("Seçilen maç planı\n")
            .append("- Diziliş: ").append(formation.getName())
            .append(" | Hücum: ").append(formation.getOffensiveStrength())
            .append(" | Savunma: ").append(formation.getDefensiveStrength())
            .append("\n- Taktik: ").append(tactic.getName())
            .append(" | Agresiflik: ").append(tactic.getAggressivenessLevel())
            .append(" | Savunma: ").append(tactic.getDefenseStrength())
            .append("\n- Açıklama: ").append(tactic.getDescription())
            .append("\n\n");
        builder.append("Beklenen ilk 6, maç başladığında seçilen plana göre koç tarafından kurulacak.");
        return builder.toString();
    }

    private JComboBox<Formation> formationCombo(Formation[] formations) {
        JComboBox<Formation> comboBox = new JComboBox<>(formations);
        comboBox.setBackground(PANEL);
        comboBox.setForeground(TEXT);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Formation formation = (Formation) value;
                return super.getListCellRendererComponent(
                    list,
                    formation == null ? "-" : formation.getName() + " - " + formation.getDescription(),
                    index,
                    isSelected,
                    cellHasFocus
                );
            }
        });
        return comboBox;
    }

    private JComboBox<Tactic> tacticCombo(Tactic[] tactics) {
        JComboBox<Tactic> comboBox = new JComboBox<>(tactics);
        comboBox.setBackground(PANEL);
        comboBox.setForeground(TEXT);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Tactic tactic = (Tactic) value;
                return super.getListCellRendererComponent(
                    list,
                    tactic == null ? "-" : tactic.getName() + " - " + tactic.getDescription(),
                    index,
                    isSelected,
                    cellHasFocus
                );
            }
        });
        return comboBox;
    }

    private void selectFormation(JComboBox<Formation> comboBox, String name) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).getName().equals(name)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectTactic(JComboBox<Tactic> comboBox, String name) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).getName().equals(name)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private int squadSize(Team team) {
        int count = 0;
        for (Player player : team.getSquad()) {
            if (player != null) count++;
        }
        return count;
    }

    private int availablePlayerCount(Team team) {
        int count = 0;
        for (Player player : team.getSquad()) {
            if (player != null && player.canPlay()) count++;
        }
        return count;
    }

    private int overallRating(Player player) {
        if (player instanceof VolleyballPlayer volleyballPlayer) {
            return volleyballPlayer.getOverallRating();
        }
        if (player instanceof FootballPlayer footballPlayer) {
            return footballPlayer.getOverallRating();
        }
        return (int) Math.round(player.getRating() * 10);
    }

    private int statOne(Player player) {
        if (player instanceof VolleyballPlayer volleyballPlayer) {
            return volleyballPlayer.getSpikes();
        }
        if (player instanceof FootballPlayer footballPlayer) {
            return footballPlayer.getGoals();
        }
        return 0;
    }

    private int statTwo(Player player) {
        if (player instanceof VolleyballPlayer volleyballPlayer) {
            return volleyballPlayer.getBlocks();
        }
        if (player instanceof FootballPlayer footballPlayer) {
            return footballPlayer.getAssists();
        }
        return 0;
    }

    private int statThree(Player player) {
        if (player instanceof VolleyballPlayer volleyballPlayer) {
            return volleyballPlayer.getAces();
        }
        if (player instanceof FootballPlayer footballPlayer) {
            return footballPlayer.getPasses();
        }
        return 0;
    }

    private String scoreFor(Team team) {
        return team.getGoalsFor() + "-" + team.getGoalsAgainst();
    }

    private String detailScoreFor(Team team) {
        if (team instanceof VolleyballTeam volleyballTeam) {
            return volleyballTeam.getTotalRallyPointsFor() + "-" + volleyballTeam.getTotalRallyPointsAgainst();
        }
        return String.valueOf(team.getGoalDifference());
    }

    private String averageFor(Team team) {
        if (team instanceof VolleyballTeam volleyballTeam) {
            return String.format("%.2f", volleyballTeam.getSetAverage());
        }
        return String.valueOf(team.getGoalDifference());
    }

    private String setScoreText(Match match) {
        if (match instanceof VolleyballMatch volleyballMatch) {
            String setScores = volleyballMatch.getFormattedSetScores();
            return setScores.isBlank() ? "-" : setScores;
        }
        return "-";
    }

    private String manOfMatchText(Match match) {
        Player player = match.getMatchResult().getManOfTheMatch();
        return player == null ? "-" : player.getName();
    }

    private String winnerText(Match match) {
        Team winner = match.getMatchResult().getWinner();
        return winner == null ? "Berabere" : winner.getName();
    }

    private String managedMatchDetails(Match match) {
        StringBuilder details = new StringBuilder();
        details.append(match.getMatchResult().getSummary()).append("\n");
        details.append("Maçın oyuncusu: ").append(manOfMatchText(match)).append("\n");

        if (match instanceof VolleyballMatch volleyballMatch) {
            details.append("Setler: ").append(volleyballMatch.getFormattedSetScores()).append("\n");
            details.append(match.getHomeTeam().getName()).append(" istatistikleri: ")
                .append(volleyballMatch.getTeamStatSummary(match.getHomeTeam())).append("\n");
            details.append(match.getAwayTeam().getName()).append(" istatistikleri: ")
                .append(volleyballMatch.getTeamStatSummary(match.getAwayTeam())).append("\n");
            if (!volleyballMatch.getInjuryEvents().isEmpty()) {
                details.append("Sakatlıklar:\n");
                for (String injury : volleyballMatch.getInjuryEvents()) {
                    details.append("- ").append(injury).append("\n");
                }
            }
            details.append("\nOlay akışı:\n");
            details.append(String.join("\n", volleyballMatch.getMatchEvents()));
        } else if (match instanceof MatchSimulationEngine engine) {
            details.append("Olay akışı:\n");
            details.append(String.join("\n", engine.getMatchEvents()));
        }

        details.append("\n\n");
        return details.toString();
    }

    private void saveGame() {
        if (state == null) {
            JOptionPane.showMessageDialog(this, "Kaydedilecek oyun yok.");
            return;
        }
        try {
            state.markSavedNow();
            Path sportSavePath = SaveService.getSavePath(state.getSelectedSport());
            SaveService.save(state, sportSavePath);
            // Also save to latest.ser for backward compatibility
            SaveService.save(state, SaveService.DEFAULT_SAVE_PATH);
            JOptionPane.showMessageDialog(this, "Oyun kaydedildi: " + sportSavePath.getFileName());
            showHub();
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Kayıt başarısız: " + exception.getMessage());
        }
    }

    private void loadGame() {
        List<Path> saves = SaveService.listSaves();
        if (saves.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kayıt dosyası bulunamadı.");
            return;
        }

        // Build display labels for each save
        String[] descriptions = new String[saves.size()];
        for (int i = 0; i < saves.size(); i++) {
            descriptions[i] = SaveService.describesSave(saves.get(i));
        }

        String selected = (String) JOptionPane.showInputDialog(
            this,
            "Yüklenecek kayıt dosyasını seçin:",
            "Oyunu Yükle",
            JOptionPane.QUESTION_MESSAGE,
            null,
            descriptions,
            descriptions[0]
        );

        if (selected == null) {
            return; // User cancelled
        }

        // Find the matching path
        int selectedIndex = -1;
        for (int i = 0; i < descriptions.length; i++) {
            if (descriptions[i].equals(selected)) {
                selectedIndex = i;
                break;
            }
        }
        if (selectedIndex < 0) {
            return;
        }

        Path chosenPath = saves.get(selectedIndex);
        try {
            state = SaveService.load(chosenPath);
            state.addRecentResult("Kayıt yüklendi: " + state.getSelectedSport().getDisplayName()
                + " | Takım: " + (state.getManagedTeam() == null ? "-" : state.getManagedTeam().getName())
                + " | Hafta: " + state.getCurrentWeek());
            JOptionPane.showMessageDialog(this, "Kayıt yüklendi: "
                + state.getSelectedSport().getDisplayName()
                + " / Hafta " + state.getCurrentWeek());
            showHub();
        } catch (IOException | ClassNotFoundException exception) {
            JOptionPane.showMessageDialog(this, "Yükleme başarısız: " + exception.getMessage());
        }
    }

    private String hubSubtitle() {
        String teamName = state.getManagedTeam() == null ? "Takım seçilmedi" : state.getManagedTeam().getName();
        String week = state.isSeasonFinished()
            ? "Sezon tamamlandı"
            : "Hafta " + state.getCurrentWeek() + "/" + state.getLeague().getFixture().getTotalWeeks();
        Match next = state.getNextManagedMatch();
        String nextText = next == null
            ? "Sıradaki maç yok"
            : next.getHomeTeam().getName() + " - " + next.getAwayTeam().getName();
        String planned = state.getPlannedFormation() == null || state.getPlannedTactic() == null
            ? "Plan yok"
            : state.getPlannedFormation().getName() + " / " + state.getPlannedTactic().getName();
        return teamName + " | " + week + " | " + nextText + " | " + planned;
    }

    private String statusLabel(Match match) {
        return switch (match.getStatus()) {
            case SCHEDULED -> "Planlandı";
            case ONGOING -> "Oynanıyor";
            case FINISHED -> "Bitti";
        };
    }

    private String lastSaveText() {
        return state == null || state.getLastSavedAt() == null
            ? "-"
            : state.getLastSavedAt().format(SAVE_TIME_FORMAT);
    }

    private JPanel basePanel(String title, String subtitle) {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 28f));

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(14f));

        JPanel header = new JPanel(new GridLayout(2, 1, 0, 4));
        header.setOpaque(false);
        header.add(titleLabel);
        header.add(subtitleLabel);
        root.add(header, BorderLayout.NORTH);
        return root;
    }

    private JPanel wrap(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(PANEL);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        component.setBackground(PANEL);
        wrapper.add(component, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel bottomBar(JButton backButton) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        panel.add(backButton);
        return panel;
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        styleButton(button, PRIMARY, Color.WHITE, PRIMARY);
        return button;
    }

    private JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(14f));
        styleButton(button, SECONDARY, TEXT, SECONDARY_DARK);
        return button;
    }

    private JButton backButton(String text, Runnable action) {
        JButton button = secondaryButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    private JTextArea textArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBackground(PANEL);
        area.setForeground(TEXT);
        area.setSelectionColor(SELECTION);
        area.setSelectedTextColor(TEXT);
        area.setCaretColor(PRIMARY);
        return area;
    }

    private JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(28);
        table.setAutoCreateRowSorter(true);
        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setGridColor(new Color(226, 232, 240));
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(TABLE_HEADER);
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        return table;
    }

    private void highlightRowsForManagedTeam(JTable table, int... teamNameColumns) {
        if (state == null || state.getManagedTeam() == null) {
            return;
        }

        String managedTeamName = state.getManagedTeam().getName();
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    boolean managedRow = false;
                    for (int teamNameColumn : teamNameColumns) {
                        if (teamNameColumn < table.getColumnCount()
                                && managedTeamName.equals(String.valueOf(table.getValueAt(row, teamNameColumn)))) {
                            managedRow = true;
                            break;
                        }
                    }
                    component.setBackground(managedRow ? new Color(229, 241, 236) : PANEL);
                    component.setForeground(managedRow ? PRIMARY : TEXT);
                }
                return component;
            }
        });
    }

    private void highlightUnavailablePlayers(JTable table, int statusColumn) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component component = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String status = String.valueOf(table.getValueAt(row, statusColumn));
                    boolean unavailable = status.startsWith("Sakat");
                    component.setBackground(unavailable ? new Color(250, 235, 229) : PANEL);
                    component.setForeground(unavailable ? new Color(128, 49, 34) : TEXT);
                }
                return component;
            }
        });
    }

    private void styleButton(JButton button, Color background, Color foreground, Color border) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void configureSwingColors() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Table.selectionBackground", SELECTION);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("TableHeader.background", TABLE_HEADER);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("List.selectionBackground", SELECTION);
        UIManager.put("List.selectionForeground", TEXT);
        UIManager.put("ComboBox.background", PANEL);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("Button.select", new Color(214, 226, 238));
        UIManager.put("Focus.color", ACCENT);
    }

    private void replaceContent(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
    }
}
