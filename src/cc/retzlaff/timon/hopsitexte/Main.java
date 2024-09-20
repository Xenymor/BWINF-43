package cc.retzlaff.timon.hopsitexte;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Main {
    //TODO change alarm sound
    public static final String ALARM_PATH = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\hopsitexte\\Alarm.wav";
    public static final String THESAURUS_PATH = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\hopsitexte\\synonyms.csv";
    static Highlighter.HighlightPainter bluePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
    static Highlighter.HighlightPainter greenPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
    static Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
    final static AudioFilePlayer player = new AudioFilePlayer();
    final static Map<String, List<String>> synonyms = new HashMap<>();

    public static void main(String[] args) throws IOException {
        readSynonyms();
        JFrame frame = new JFrame();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize(screenSize.width / 2, screenSize.height);
        frame.setLocation(screenSize.width / 4, 0);
        final JTextPane textPane = new JTextPane();
        frame.add(textPane);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        String oldText = null;
        final Highlighter highlighter = textPane.getHighlighter();
        boolean playSound = true;
        final AtomicReference<String> choice1 = new AtomicReference<>("");
        final AtomicInteger position1 = new AtomicInteger(-1);
        int length1 = -1;
        final AtomicBoolean isDialog1Open = new AtomicBoolean(false);
        AtomicReference<String> choice2 = new AtomicReference<>("");
        AtomicInteger position2 = new AtomicInteger(-1);
        int length2 = -1;
        final AtomicBoolean isDialog2Open = new AtomicBoolean(false);
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String text = textPane.getText();
            int position = position1.get();
            String choice = choice1.get();
            if (position > -1 && !choice.isEmpty()) {
                text = text.substring(0, position) + choice + text.substring(position + length1);
                textPane.setText(text);
                position1.set(-1);
                choice1.set("");
                isDialog1Open.set(false);
            }
            position = position2.get();
            choice = choice2.get();
            if (position > -1 && !choice.isEmpty()) {
                text = text.substring(0, position) + choice + text.substring(position + length2);
                textPane.setText(text);
                position2.set(-1);
                choice2.set("");
                isDialog2Open.set(false);
            }
            if (!text.equals(oldText)) {
                highlighter.removeAllHighlights();
                oldText = text;
                char[] inputChars = text.toLowerCase().toCharArray();
                List<Integer> originalIndex = new ArrayList<>();
                StringBuilder formattedTextBuilder = new StringBuilder();
                for (int i = 0; i < inputChars.length; i++) {
                    final char currChar = inputChars[i];
                    if (Character.isAlphabetic(currChar)) {
                        formattedTextBuilder.append(currChar);
                        originalIndex.add(i);
                    }
                }
                String formattedText = formattedTextBuilder.toString();
                List<Integer> positions1 = getPositions(formattedText, 0);
                List<Integer> positions2 = getPositions(formattedText, 1);
                final boolean isHopsiText = (positions1.isEmpty() || positions2.isEmpty()) || (!(positions1.get(positions1.size() - 1).equals(positions2.get(positions2.size() - 1))));
                if (isHopsiText) {
                    playSound = true;
                } else {
                    if (playSound) {
                        new Thread(() -> player.play(ALARM_PATH)).start();
                        playSound = false;
                    }
                    for (int i = 0; i < positions1.size(); i++) {
                        final Integer currPos = positions1.get(i);
                        if (positions2.contains(currPos)) {
                            int ogPosition = originalIndex.get(currPos);
                            int ogLastPos1 = -1;
                            int ogLastPos2 = -1;
                            try {
                                ogLastPos1 = originalIndex.get(positions1.get(i - 1));
                                ogLastPos2 = originalIndex.get(positions2.get(positions2.indexOf(currPos) - 1));
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                highlighter.addHighlight(ogPosition - 1, ogPosition, redPainter);
                                if (ogLastPos1 > -1) {
                                    if (!isDialog1Open.get()) {
                                        ValuePosition valuePosition = getWord(ogLastPos1, text);
                                        length1 = valuePosition.value.length();
                                        position1.set(valuePosition.position);
                                        if (synonyms.containsKey(valuePosition.value)) {
                                            openSynonymsWindow(choice1, position1, valuePosition, textPane, isDialog1Open);
                                            isDialog1Open.set(true);
                                        }
                                    }
                                    highlighter.addHighlight(ogLastPos1 - 1, ogLastPos1, bluePainter);
                                }
                                if (ogLastPos2 > -1) {
                                    if (!isDialog2Open.get()) {
                                        ValuePosition valuePosition = getWord(ogLastPos2, text);
                                        if (position1.get() != valuePosition.position) {
                                            length2 = valuePosition.value.length();
                                            position2.set(valuePosition.position);
                                            if (synonyms.containsKey(valuePosition.value)) {
                                                openSynonymsWindow(choice2, position2, valuePosition, textPane, isDialog2Open);
                                                isDialog2Open.set(true);
                                            }
                                        }
                                    }
                                    highlighter.addHighlight(ogLastPos2 - 1, ogLastPos2, greenPainter);
                                }
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void openSynonymsWindow(final AtomicReference<String> choiceOutput, final AtomicInteger positionOutput, final ValuePosition valuePosition, final JTextPane textPane, final AtomicBoolean isDialogOpen) throws BadLocationException {
        SwingUtilities.invokeLater(() -> {
            List<String> options = new ArrayList<>(synonyms.get(valuePosition.value));
            options.add("Close");

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            final int index = valuePosition.position;
            Rectangle2D startPos = null;
            Rectangle2D endPos = null;
            try {
                startPos = textPane.modelToView2D(index);
                endPos = textPane.modelToView2D(index + valuePosition.value.length());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            for (String option : options) {
                JButton button = new JButton(option);
                button.addActionListener(e -> {
                    System.out.println("You selected: " + option);
                    if (!option.equals("Close")) {
                        choiceOutput.set(option);
                    } else {
                        positionOutput.set(-1);
                    }
                    SwingUtilities.getWindowAncestor(panel).dispose();
                    isDialogOpen.set(false);
                });
                panel.add(button);
            }

            JDialog dialog = new JDialog();
            final Point locationOnScreen = textPane.getLocationOnScreen();
            dialog.setLocation((int) (startPos.getX()) + locationOnScreen.x, (int) endPos.getMaxY() + 5 + locationOnScreen.y);
            dialog.setSize((int) (endPos.getMaxX() - startPos.getMinX()), dialog.getHeight());
            dialog.setContentPane(panel);
            dialog.setUndecorated(true);
            dialog.pack();
            dialog.setVisible(true);
        });
    }

    private static ValuePosition getWord(final int position, final String text) {
        if (position < 0 || position >= text.length()) {
            return null;
        }

        int start = position;
        while (start > 0) {
            final char codePoint = text.charAt(start - 1);
            if (!Character.isWhitespace(codePoint) && Character.isAlphabetic(codePoint)) {
                start--;
            } else {
                break;
            }
        }

        int end = position;
        while (end < text.length()) {
            final char codePoint = text.charAt(end);
            if (!Character.isWhitespace(codePoint) && Character.isAlphabetic(codePoint)) {
                end--;
            } else {
                break;
            }
        }

        return new ValuePosition(text.substring(start, end), start);
    }

    private static void readSynonyms() throws IOException {
        List<String> lines = Files.readAllLines(Path.of(THESAURUS_PATH));
        for (String line : lines) {
            String[] words = line.split(",");
            String key = words[0];
            List<String> currSynonyms = new ArrayList<>(words.length - 1);
            currSynonyms.addAll(Arrays.asList(words).subList(1, words.length));
            synonyms.put(key, currSynonyms);
        }
    }

    private static List<Integer> getPositions(final String text, final int startPosition) {
        List<Integer> result = new ArrayList<>();
        char[] chars = text.toCharArray();
        int position = startPosition;
        while (position < chars.length) {
            result.add(position);
            position += getValue(chars[position]);
        }
        return result;
    }

    private static int getValue(final char letter) {
        if (letter < 128) {
            return letter - 96;
        } else {
            switch (letter) {
                case 'ä' -> {
                    return 27;
                }
                case 'ö' -> {
                    return 28;
                }
                case 'ü' -> {
                    return 29;
                }
                case 'ß' -> {
                    return 30;
                }
                default -> throw new IllegalArgumentException(letter + " is no lowercase letter");
            }
        }
    }

    private record ValuePosition(String value, int position) {
    }
}
