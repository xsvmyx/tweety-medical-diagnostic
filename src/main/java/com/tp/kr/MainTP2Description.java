package com.tp.kr;

import org.tweetyproject.logics.dl.reasoner.NaiveDlReasoner;
import org.tweetyproject.logics.dl.syntax.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * TP2 — Système de Diagnostic Médical basé sur la Logique de Description (DL).
 * <p>
 * Utilise la bibliothèque TweetyProject (org.tweetyproject.logics.dl) pour :
 * <ul>
 *   <li>Modéliser une TBox (taxonomie de maladies, définitions de syndromes)</li>
 *   <li>Construire dynamiquement une ABox à partir des symptômes saisis par l'utilisateur</li>
 *   <li>Raisonner avec {@link NaiveDlReasoner} pour vérifier si un patient
 *       appartient à un concept donné (Flu, Covid, …)</li>
 * </ul>
 * Interface Swing conçue pour une présentation orale.
 *g
 * @author TP2-KR
 */
public class MainTP2Description extends JFrame {

    // ── UI Components ──────────────────────────────────────────────────────────
    private final JTextField txtPatientName;
    private final JCheckBox chkFever;
    private final JCheckBox chkCough;
    private final JCheckBox chkFatigue;
    private final JCheckBox chkLossOfTaste;
    private final JCheckBox chkSoreThroat;
    private final JCheckBox chkBodyAche;
    private final JTextPane outputArea;

    // ── Colors (dark medical theme) ────────────────────────────────────────────
    private static final Color BG_DARK       = new Color(18, 18, 30);
    private static final Color BG_PANEL      = new Color(28, 28, 48);
    private static final Color ACCENT_BLUE   = new Color(72, 145, 255);
    private static final Color ACCENT_GREEN  = new Color(80, 220, 140);
    private static final Color ACCENT_RED    = new Color(255, 95, 95);
    private static final Color ACCENT_YELLOW = new Color(255, 210, 80);
    private static final Color FG_LIGHT      = new Color(230, 230, 240);
    private static final Color FG_DIM        = new Color(160, 160, 180);

    // ═══════════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════════════
    public MainTP2Description() {
        super("Système de Diagnostic — Logique de Description");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // ── Header ─────────────────────────────────────────────────────────────
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // ── Main split ─────────────────────────────────────────────────────────
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setDividerSize(3);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_DARK);

        // Left: input
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(BG_PANEL);
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        // Patient name
        JLabel lblPatient = styledLabel("🧑 Nom du Patient :", 14, true);
        inputPanel.add(lblPatient);
        inputPanel.add(Box.createVerticalStrut(6));
        txtPatientName = new JTextField("John");
        styleTextField(txtPatientName);
        inputPanel.add(txtPatientName);
        inputPanel.add(Box.createVerticalStrut(18));

        // Symptoms section
        JLabel lblSymptoms = styledLabel("🩺 Sélectionner les symptômes :", 14, true);
        inputPanel.add(lblSymptoms);
        inputPanel.add(Box.createVerticalStrut(10));

        chkFever       = styledCheckBox("🌡️  Fièvre (Fever)");
        chkCough       = styledCheckBox("😷  Toux (Cough)");
        chkFatigue     = styledCheckBox("😴  Fatigue");
        chkLossOfTaste = styledCheckBox("👅  Perte de Goût (Loss of Taste)");
        chkSoreThroat  = styledCheckBox("🗣️  Mal de Gorge (Sore Throat)");
        chkBodyAche    = styledCheckBox("💪  Courbatures (Body Ache)");

        for (JCheckBox cb : new JCheckBox[]{chkFever, chkCough, chkFatigue,
                chkLossOfTaste, chkSoreThroat, chkBodyAche}) {
            inputPanel.add(cb);
            inputPanel.add(Box.createVerticalStrut(4));
        }

        inputPanel.add(Box.createVerticalStrut(20));

        // Button
        JButton btnRun = new JButton("▶  Lancer le Raisonneur DL");
        btnRun.setFont(new Font("SansSerif", Font.BOLD, 15));
        btnRun.setForeground(Color.WHITE);
        btnRun.setBackground(ACCENT_BLUE);
        btnRun.setFocusPainted(false);
        btnRun.setBorderPainted(false);
        btnRun.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRun.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRun.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnRun.setPreferredSize(new Dimension(300, 48));
        btnRun.addActionListener(this::runReasoner);
        inputPanel.add(btnRun);

        inputPanel.add(Box.createVerticalGlue());

        // Right: output
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(BG_DARK);
        outputPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lblOutput = styledLabel("📋  Journal de Raisonnement", 14, true);
        outputPanel.add(lblOutput, BorderLayout.NORTH);

        outputArea = new JTextPane();
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(12, 12, 22));
        outputArea.setForeground(FG_LIGHT);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setCaretColor(ACCENT_BLUE);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 80), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(inputPanel);
        splitPane.setRightComponent(outputPanel);
        add(splitPane, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────────────────────
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(BG_DARK);
        footerPanel.setBorder(new EmptyBorder(6, 0, 8, 0));
        JLabel lblFooter = new JLabel("TP2 KR — TweetyProject DL  •  Présentation Orale");
        lblFooter.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblFooter.setForeground(FG_DIM);
        footerPanel.add(lblFooter);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  DL REASONING LOGIC
    // ═══════════════════════════════════════════════════════════════════════════
    private void runReasoner(ActionEvent e) {
        // Clear output
        outputArea.setText("");

        String patientName = txtPatientName.getText().trim();
        if (patientName.isEmpty()) {
            appendStyled("❌  Erreur : veuillez saisir un nom de patient.\n", ACCENT_RED, true);
            return;
        }

        boolean hasFever   = chkFever.isSelected();
        boolean hasCough   = chkCough.isSelected();
        boolean hasFatigue = chkFatigue.isSelected();
        boolean hasLoss    = chkLossOfTaste.isSelected();
        boolean hasSore    = chkSoreThroat.isSelected();
        boolean hasAche    = chkBodyAche.isSelected();

        if (!hasFever && !hasCough && !hasFatigue && !hasLoss && !hasSore && !hasAche) {
            appendStyled("⚠️  Aucun symptôme sélectionné. Veuillez en cocher au moins un.\n",
                    ACCENT_YELLOW, true);
            return;
        }

        try {
            performDlReasoning(patientName, hasFever, hasCough, hasFatigue, hasLoss, hasSore, hasAche);
        } catch (Exception ex) {
            appendStyled("\n❌  Erreur durant le raisonnement :\n", ACCENT_RED, true);
            appendStyled(ex.getMessage() + "\n", ACCENT_RED, false);
            ex.printStackTrace();
        }
    }

    /**
     * Core DL reasoning pipeline.
     * <p>
     * Modeling strategy: Symptoms are modeled as atomic concepts (HasFever, HasCough, ...)
     * and the patient is directly asserted into these concepts. This keeps the DL signature
     * small (11 concepts × 1 individual = 2^11 = 2,048 interpretations) so the
     * NaiveDlReasoner can enumerate them all in milliseconds.
     * <p>
     * The TBox uses EquivalenceAxiom (⊑) to define:
     *   - Disease definitions: (HasFever ⊓ HasCough ⊓ HasBodyAche) ⊑ Flu
     *   - Taxonomy: Flu ⊑ Disease, Covid ⊑ Disease, Cold ⊑ Disease
     */
    private void performDlReasoning(String patientName,
                                    boolean hasFever, boolean hasCough, boolean hasFatigue,
                                    boolean hasLoss, boolean hasSore, boolean hasAche) {

        // ── 1. Define Concepts ─────────────────────────────────────────────────
        appendStyled("═══════════════════════════════════════════════════\n", FG_DIM, false);
        appendStyled("        SYSTÈME DE DIAGNOSTIC MÉDICAL — DL\n", ACCENT_BLUE, true);
        appendStyled("═══════════════════════════════════════════════════\n\n", FG_DIM, false);

        appendStyled("--- Définition des Concepts Atomiques ---\n", ACCENT_YELLOW, true);

        // Disease concepts
        AtomicConcept patient = new AtomicConcept("Patient");
        AtomicConcept disease = new AtomicConcept("Disease");
        AtomicConcept flu     = new AtomicConcept("Flu");
        AtomicConcept covid   = new AtomicConcept("Covid");
        AtomicConcept cold    = new AtomicConcept("Cold");

        // Symptom-indicator concepts (HasX = "patient presents symptom X")
        AtomicConcept hasFeverC       = new AtomicConcept("HasFever");
        AtomicConcept hasCoughC       = new AtomicConcept("HasCough");
        AtomicConcept hasFatigueC     = new AtomicConcept("HasFatigue");
        AtomicConcept hasLossOfTasteC = new AtomicConcept("HasLossOfTaste");
        AtomicConcept hasSoreThroatC  = new AtomicConcept("HasSoreThroat");
        AtomicConcept hasBodyAcheC    = new AtomicConcept("HasBodyAche");

        appendStyled("  Maladies  : Patient, Disease, Flu, Covid, Cold\n", FG_LIGHT, false);
        appendStyled("  Symptômes : HasFever, HasCough, HasFatigue,\n", FG_LIGHT, false);
        appendStyled("              HasLossOfTaste, HasSoreThroat, HasBodyAche\n\n", FG_LIGHT, false);

        // ── 2. Rôle (conceptuel) ───────────────────────────────────────────────
        appendStyled("--- Rôle défini (vocabulaire DL) ---\n", ACCENT_YELLOW, true);
        appendStyled("  ✓ hasSymptom (Patient × Symptom)\n", FG_LIGHT, false);
        appendStyled("    Note : encodé via concepts HasX pour optimiser\n", FG_DIM, false);
        appendStyled("    le raisonnement (NaiveDlReasoner = brute force).\n\n", FG_DIM, false);

        // ── 3. Build TBox ──────────────────────────────────────────────────────
        appendStyled("--- Chargement de la TBox ---\n", ACCENT_YELLOW, true);

        DlBeliefSet kb = new DlBeliefSet();

        // ─── Taxonomy: Disease ⊒ Flu, Covid, Cold ───
        kb.add(new EquivalenceAxiom(flu, disease));
        appendStyled("  T1: Flu ⊑ Disease\n", FG_LIGHT, false);

        kb.add(new EquivalenceAxiom(covid, disease));
        appendStyled("  T2: Covid ⊑ Disease\n", FG_LIGHT, false);

        kb.add(new EquivalenceAxiom(cold, disease));
        appendStyled("  T3: Cold ⊑ Disease\n", FG_LIGHT, false);

        // ─── Disease definitions (symptom pattern ⊑ Disease) ───
        // Flu ≡ HasFever ⊓ HasCough ⊓ HasBodyAche
        ComplexConcept fluDef = new Intersection(
                new Intersection(hasFeverC, hasCoughC),
                hasBodyAcheC
        );
        kb.add(new EquivalenceAxiom(fluDef, flu));   // symptoms → Flu
        kb.add(new EquivalenceAxiom(flu, fluDef));   // Flu → symptoms
        appendStyled("  T4: Flu ≡ HasFever ⊓ HasCough ⊓ HasBodyAche\n", FG_LIGHT, false);

        // Covid ≡ HasFever ⊓ HasLossOfTaste
        ComplexConcept covidDef = new Intersection(hasFeverC, hasLossOfTasteC);
        kb.add(new EquivalenceAxiom(covidDef, covid)); // symptoms → Covid
        kb.add(new EquivalenceAxiom(covid, covidDef)); // Covid → symptoms
        appendStyled("  T5: Covid ≡ HasFever ⊓ HasLossOfTaste\n", FG_LIGHT, false);

        // Cold ≡ HasCough ⊓ HasSoreThroat
        ComplexConcept coldDef = new Intersection(hasCoughC, hasSoreThroatC);
        kb.add(new EquivalenceAxiom(coldDef, cold));   // symptoms → Cold
        kb.add(new EquivalenceAxiom(cold, coldDef));   // Cold → symptoms
        appendStyled("  T6: Cold ≡ HasCough ⊓ HasSoreThroat\n", FG_LIGHT, false);

        appendStyled("  → TBox chargée avec " + kb.size() + " axiomes.\n\n", ACCENT_GREEN, false);

        // ── 4. Build ABox ──────────────────────────────────────────────────────
        appendStyled("--- Construction de la ABox (Patient: " + patientName + ") ---\n", ACCENT_YELLOW, true);

        Individual ind = new Individual(patientName);

        // Patient(patientName)
        kb.add(new ConceptAssertion(ind, patient));
        appendStyled("  A0: " + patientName + " : Patient\n", FG_LIGHT, false);

        int symptomCount = 0;
        if (hasFever) {
            kb.add(new ConceptAssertion(ind, hasFeverC));
            appendStyled("  A" + (++symptomCount) + ": " + patientName + " : HasFever\n", FG_LIGHT, false);
        }
        if (hasCough) {
            kb.add(new ConceptAssertion(ind, hasCoughC));
            appendStyled("  A" + (++symptomCount) + ": " + patientName + " : HasCough\n", FG_LIGHT, false);
        }
        if (hasFatigue) {
            kb.add(new ConceptAssertion(ind, hasFatigueC));
            appendStyled("  A" + (++symptomCount) + ": " + patientName + " : HasFatigue\n", FG_LIGHT, false);
        }
        if (hasLoss) {
            kb.add(new ConceptAssertion(ind, hasLossOfTasteC));
            appendStyled("  A" + (++symptomCount) + ": " + patientName + " : HasLossOfTaste\n", FG_LIGHT, false);
        }
        if (hasSore) {
            kb.add(new ConceptAssertion(ind, hasSoreThroatC));
            appendStyled("  A" + (++symptomCount) + ": " + patientName + " : HasSoreThroat\n", FG_LIGHT, false);
        }
        if (hasAche) {
            kb.add(new ConceptAssertion(ind, hasBodyAcheC));
            appendStyled("  A" + (++symptomCount) + ": " + patientName + " : HasBodyAche\n", FG_LIGHT, false);
        }

        appendStyled("  → ABox construite avec " + symptomCount + " symptôme(s).\n\n", ACCENT_GREEN, false);

        // ── 5. Reasoning ───────────────────────────────────────────────────────
        appendStyled("--- Lancement du Raisonneur DL (NaiveDlReasoner) ---\n", ACCENT_YELLOW, true);
        appendStyled("  Moteur : Raisonnement naïf ALC (énumération des interprétations)\n", FG_DIM, false);
        appendStyled("  Requêtes : vérification d'appartenance conceptuelle\n\n", FG_DIM, false);

        NaiveDlReasoner reasoner = new NaiveDlReasoner();

        long t0 = System.currentTimeMillis();

        // Query: Is patient a Flu patient?
        ConceptAssertion queryFlu = new ConceptAssertion(ind, flu);
        appendStyled("  ❓ Query 1 : KB ⊨ " + patientName + " : Flu ?  ", FG_LIGHT, false);
        Boolean isFlu = reasoner.query(kb, queryFlu);
        appendStyled(isFlu ? "✅ OUI\n" : "❌ NON\n", isFlu ? ACCENT_GREEN : ACCENT_RED, true);

        // Query: Is patient a Covid patient?
        ConceptAssertion queryCovid = new ConceptAssertion(ind, covid);
        appendStyled("  ❓ Query 2 : KB ⊨ " + patientName + " : Covid ?  ", FG_LIGHT, false);
        Boolean isCovid = reasoner.query(kb, queryCovid);
        appendStyled(isCovid ? "✅ OUI\n" : "❌ NON\n", isCovid ? ACCENT_GREEN : ACCENT_RED, true);

        // Query: Is patient a Cold patient?
        ConceptAssertion queryCold = new ConceptAssertion(ind, cold);
        appendStyled("  ❓ Query 3 : KB ⊨ " + patientName + " : Cold ?  ", FG_LIGHT, false);
        Boolean isCold = reasoner.query(kb, queryCold);
        appendStyled(isCold ? "✅ OUI\n" : "❌ NON\n", isCold ? ACCENT_GREEN : ACCENT_RED, true);

        // Query: Is patient a Disease patient?
        ConceptAssertion queryDisease = new ConceptAssertion(ind, disease);
        appendStyled("  ❓ Query 4 : KB ⊨ " + patientName + " : Disease ?  ", FG_LIGHT, false);
        Boolean isDisease = reasoner.query(kb, queryDisease);
        appendStyled(isDisease ? "✅ OUI\n" : "❌ NON\n", isDisease ? ACCENT_GREEN : ACCENT_RED, true);

        long elapsed = System.currentTimeMillis() - t0;
        appendStyled("  ⏱ Temps de raisonnement : " + elapsed + " ms\n", FG_DIM, false);

        // ── 6. Summary ─────────────────────────────────────────────────────────
        appendStyled("\n═══════════════════════════════════════════════════\n", FG_DIM, false);
        appendStyled("        RÉSULTAT DU DIAGNOSTIC\n", ACCENT_BLUE, true);
        appendStyled("═══════════════════════════════════════════════════\n\n", FG_DIM, false);

        appendStyled("  Patient : " + patientName + "\n", FG_LIGHT, true);
        appendStyled("  Symptômes : ", FG_LIGHT, false);

        List<String> symptomNames = new ArrayList<>();
        if (hasFever) symptomNames.add("Fièvre");
        if (hasCough) symptomNames.add("Toux");
        if (hasFatigue) symptomNames.add("Fatigue");
        if (hasLoss) symptomNames.add("Perte de Goût");
        if (hasSore) symptomNames.add("Mal de Gorge");
        if (hasAche) symptomNames.add("Courbatures");
        appendStyled(String.join(", ", symptomNames) + "\n\n", ACCENT_YELLOW, false);

        List<String> diagnostics = new ArrayList<>();
        if (Boolean.TRUE.equals(isFlu))   diagnostics.add("Grippe (Flu)");
        if (Boolean.TRUE.equals(isCovid)) diagnostics.add("Covid-19");
        if (Boolean.TRUE.equals(isCold))  diagnostics.add("Rhume (Cold)");

        if (diagnostics.isEmpty()) {
            appendStyled("  ⚕ Diagnostic : Aucune maladie identifiée avec certitude.\n", ACCENT_YELLOW, true);
            appendStyled("    → Le profil de symptômes ne correspond à aucune\n", FG_DIM, false);
            appendStyled("      définition de la TBox. Consultation recommandée.\n", FG_DIM, false);
        } else {
            appendStyled("  ⚕ Diagnostic : ", FG_LIGHT, true);
            appendStyled(String.join(" + ", diagnostics) + "\n", ACCENT_GREEN, true);
            if (Boolean.TRUE.equals(isDisease)) {
                appendStyled("    → Classification confirmée : Disease ✓\n", ACCENT_GREEN, false);
            }
        }

        appendStyled("\n═══════════════════════════════════════════════════\n", FG_DIM, false);
        appendStyled("  Raisonnement terminé avec succès.\n", ACCENT_GREEN, true);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  STYLED OUTPUT HELPERS
    // ═══════════════════════════════════════════════════════════════════════════
    private void appendStyled(String text, Color color, boolean bold) {
        StyledDocument doc = outputArea.getStyledDocument();
        Style style = outputArea.addStyle("custom", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, 13);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        outputArea.setCaretPosition(doc.getLength());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  UI FACTORY METHODS
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 25, 50));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel titleLabel = new JLabel("⚕  Système de Diagnostic — Logique de Description");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(ACCENT_BLUE);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel subtitleLabel = new JLabel("TweetyProject DL · NaiveDlReasoner  ");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(FG_DIM);
        header.add(subtitleLabel, BorderLayout.EAST);

        return header;
    }

    private JLabel styledLabel(String text, int size, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(FG_LIGHT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JCheckBox styledCheckBox(String text) {
        JCheckBox cb = new JCheckBox(text);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setForeground(FG_LIGHT);
        cb.setBackground(BG_PANEL);
        cb.setFocusPainted(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private void styleTextField(JTextField tf) {
        tf.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tf.setForeground(FG_LIGHT);
        tf.setBackground(new Color(40, 40, 65));
        tf.setCaretColor(ACCENT_BLUE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 90), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ═══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        // Use system look and feel for better native rendering (optional fallback)
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            MainTP2Description frame = new MainTP2Description();
            frame.setVisible(true);
        });
    }
}
