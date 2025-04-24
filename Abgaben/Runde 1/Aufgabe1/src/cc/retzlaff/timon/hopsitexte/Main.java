package cc.retzlaff.timon.round1.hopsitexte;

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

@SuppressWarnings("BusyWait")
public class Main {
    public static final String ALARM_PATH = "src\\cc\\retzlaff\\timon\\hopsitexte\\Alarm.wav";
    //https://www.openthesaurus.de
    public static final String THESAURUS_PATH = "src\\cc\\retzlaff\\timon\\hopsitexte\\synonyms.csv";
    static final Highlighter.HighlightPainter bluePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
    static final Highlighter.HighlightPainter greenPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
    static final Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
    final static AudioFilePlayer player = new AudioFilePlayer();
    final static Map<String, List<String>> synonyms = new HashMap<>();
    private static JDialog popUp1;
    private static JDialog popUp2;
    private static int length2;
    private static int length1;
    private static AtomicBoolean isDialog1Open;
    private static AtomicBoolean isDialog2Open;

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
        AtomicReference<String> choice1 = new AtomicReference<>("");
        AtomicInteger position1 = new AtomicInteger(-1);
        length1 = -1;
        popUp1 = null;
        isDialog1Open = new AtomicBoolean(false);
        AtomicReference<String> choice2 = new AtomicReference<>("");
        AtomicInteger position2 = new AtomicInteger(-1);
        length2 = -1;
        popUp2 = null;
        isDialog2Open = new AtomicBoolean(false);
        while (frame.isVisible()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setText(textPane, position1, choice1, popUp1);
            String text = setText(textPane, position2, choice2, popUp2);
            if (!text.equals(oldText)) {
                highlighter.removeAllHighlights();
                oldText = text;
                char[] inputChars = text.toLowerCase(Locale.GERMAN).toCharArray();
                List<Integer> originalIndex = new ArrayList<>();
                StringBuilder formattedTextBuilder = new StringBuilder();
                for (int i = 0; i < inputChars.length; i++) {
                    final char currChar = inputChars[i];
                    if (isAlphabetic(currChar)) {
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
                        boolean containsPos = false;
                        int otherIndex = -1;
                        for (int j = 0; j < positions2.size(); j++) {
                            if (positions2.get(j).equals(currPos)) {
                                containsPos = true;
                                otherIndex = j;
                                break;
                            }
                        }
                        if (containsPos) {
                            int ogPosition = originalIndex.get(currPos);
                            int ogLastPos1 = -1;
                            int ogLastPos2 = -1;
                            int lineBreakCount1 = 0;
                            int lineBreakCount2 = 0;
                            try {
                                final Integer ogIndex1 = originalIndex.get(positions1.get(i - 1));
                                lineBreakCount1 = getLineBreakCount(text, ogIndex1);
                                ogLastPos1 = ogIndex1;
                                final Integer ogIndex2 = originalIndex.get(positions2.get(otherIndex - 1));
                                lineBreakCount2 = getLineBreakCount(text, ogIndex2);
                                ogLastPos2 = ogIndex2;
                            } catch (IndexOutOfBoundsException ignored) {
                            }
                            try {
                                int lineBreakCount = getLineBreakCount(text, ogPosition);
                                highlighter.addHighlight(ogPosition - lineBreakCount, ogPosition - lineBreakCount + 1, redPainter);
                                if (ogLastPos1 > -1) {
                                    if (!isDialog1Open.get()) {
                                        ValuePosition valuePosition = getWord(ogLastPos1, text);
                                        assert valuePosition != null;
                                        length1 = valuePosition.value.length();
                                        position1.set(valuePosition.position);
                                        if (synonyms.containsKey(valuePosition.value)) {
                                            if (popUp1 != null) {
                                                popUp1.dispose();
                                            }
                                            popUp1 = openSynonymsWindow(choice1, position1, valuePosition, textPane, isDialog1Open);
                                            isDialog1Open.set(true);
                                        }
                                    }
                                    highlighter.addHighlight(ogLastPos1 - lineBreakCount1, ogLastPos1 - lineBreakCount1 + 1, bluePainter);
                                }
                                if (ogLastPos2 > -1) {
                                    if (!isDialog2Open.get()) {
                                        ValuePosition valuePosition = getWord(ogLastPos2, text);
                                        assert valuePosition != null;
                                        if (position1.get() != valuePosition.position) {
                                            length2 = valuePosition.value.length();
                                            position2.set(valuePosition.position);
                                            if (synonyms.containsKey(valuePosition.value)) {
                                                if (popUp2 != null) {
                                                    popUp2.dispose();
                                                }
                                                popUp2 = openSynonymsWindow(choice2, position2, valuePosition, textPane, isDialog2Open);
                                                isDialog2Open.set(true);
                                            }
                                        }
                                    }
                                    highlighter.addHighlight(ogLastPos2 - lineBreakCount2, ogLastPos2 - lineBreakCount2 + 1, greenPainter);
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

    private static int getLineBreakCount(final String text, final Integer ogIndex1) {
        return (int) text.substring(0, ogIndex1+1).codePoints().filter(ch -> ch == '\n').count();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static boolean isAlphabetic(final char c) {
        return "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZäöüÄÖÜß".indexOf(c) >= 0;
    }

    private static String setText(final JTextPane textPane, final AtomicInteger atomicPosition, final AtomicReference<String> atomicChoice, JDialog popUp) {
        String text = textPane.getText();
        int position = atomicPosition.get();
        String choice = atomicChoice.get();
        final boolean is1 = popUp == popUp1;
        if (position > -1 && !choice.isEmpty()) {
            text = text.substring(0, position) + choice + text.substring(position + (is1 ? length1 : length2));
            textPane.setText(text);
            atomicPosition.set(-1);
            atomicChoice.set("");
            isDialog1Open.set(false);
            isDialog2Open.set(false);
            if (is1) {
                if (popUp2 != null) {
                    popUp2.dispose();
                    popUp2 = null;
                }
                popUp1 = null;
            } else {
                if (popUp1 != null) {
                    popUp1.dispose();
                    popUp1 = null;
                }
                popUp2 = null;
            }
        }
        return text;
    }

    private static int getXOverlapSize(final int x1, final int width1, final int x2, final int width2) {
        if (x1 < x2) {
            return x1 + width1 - x2;
        } else {
            return x2 + width2 - x1;
        }
    }

    static boolean valueInRange(int value, int min, int max) {
        return (value >= min) && (value <= max);
    }

    static boolean isOverlapping(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2) {
        boolean isOverlappingX = valueInRange(x1, x2, x2 + width2) ||
                valueInRange(x2, x1, x1 + width1);

        boolean isOverlappingY = valueInRange(y1, y2, y2 + height2) ||
                valueInRange(y2, y1, y1 + height1);

        return isOverlappingX && isOverlappingY;
    }

    private static JDialog openSynonymsWindow(final AtomicReference<String> choiceOutput, final AtomicInteger positionOutput, final ValuePosition valuePosition, final JTextPane textPane, final AtomicBoolean isDialogOpen) {
        JDialog dialog = new JDialog();
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

            final Point locationOnScreen = textPane.getLocationOnScreen();
            assert startPos != null;
            assert endPos != null;
            dialog.setLocation((int) (startPos.getX()) + locationOnScreen.x, (int) endPos.getMaxY() + 5 + locationOnScreen.y);
            dialog.setSize((int) (endPos.getMaxX() - startPos.getMinX()), dialog.getHeight());
            dialog.setContentPane(panel);
            dialog.setUndecorated(true);
            dialog.pack();
            dialog.setVisible(true);
            if (popUp1 != null && popUp2 != null) {
                if (popUp1.isVisible() && popUp2.isVisible()) {
                    final Point pos1 = popUp1.getLocationOnScreen();
                    final Point pos2 = popUp2.getLocationOnScreen();
                    if (isOverlapping(pos1.x, pos1.y, popUp1.getWidth(), popUp1.getHeight(),
                            pos2.x, pos2.y, popUp2.getWidth(), popUp2.getHeight())) {
                        int overLapSize = (getXOverlapSize(pos1.x, popUp1.getWidth(), pos2.x, popUp2.getWidth()) + 10) / 2;
                        if (pos1.x < pos2.x) {
                            pos1.x -= overLapSize;
                            pos2.x += overLapSize;
                        } else {
                            pos1.x += overLapSize;
                            pos2.x -= overLapSize;
                        }
                        popUp1.setLocation(pos1);
                        popUp2.setLocation(pos2);
                    }
                }
            }
        });
        return dialog;
    }

    private static ValuePosition getWord(final int position, final String text) {
        if (position < 0 || position >= text.length()) {
            return null;
        }

        int start = position;
        while (start > 0) {
            final char codePoint = text.charAt(start - 1);
            if (!Character.isWhitespace(codePoint) && isAlphabetic(codePoint)) {
                start--;
            } else {
                break;
            }
        }

        int end = position;
        while (end < text.length()) {
            final char codePoint = text.charAt(end);
            if (!Character.isWhitespace(codePoint) && isAlphabetic(codePoint)) {
                end++;
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
