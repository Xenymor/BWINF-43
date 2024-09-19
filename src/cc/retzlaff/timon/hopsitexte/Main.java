package cc.retzlaff.timon.hopsitexte;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String ALARM_PATH = "C:\\Users\\timon\\Documents\\Programmieren\\Java\\BWINF-43\\src\\cc\\retzlaff\\timon\\hopsitexte\\Alarm.wav";
    static Highlighter.HighlightPainter bluePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
    static Highlighter.HighlightPainter greenPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);
    static Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
    final static AudioFilePlayer player = new AudioFilePlayer();

    public static void main(String[] args) {
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
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            final String text = textPane.getText();
            if (!text.equals(oldText)) {
                highlighter.removeAllHighlights();
                oldText = text;
                char[] inputChars = text.toLowerCase().toCharArray();
                List<Integer> originalIndex = new ArrayList<>();
                StringBuilder formattedTextBuilder = new StringBuilder();
                for (int i = 0; i < inputChars.length; i++) {
                    final char currChar = inputChars[i];
                    if (Character.isAlphabetic(currChar)) {
                        System.out.print(currChar);
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
                        new Thread(()-> player.play(ALARM_PATH)).start();
                        playSound = false;
                    }
                    for (int i = 0; i < positions1.size(); i++) {
                        final Integer position = positions1.get(i);
                        if (positions2.contains(position)) {
                            int ogPosition = originalIndex.get(position);
                            int ogLastPos1 = -1;
                            int ogLastPos2 = -1;
                            try {
                                ogLastPos1 = originalIndex.get(positions1.get(i-1));
                                ogLastPos2 = originalIndex.get(positions2.get(positions2.indexOf(position) - 1));
                            } catch (IndexOutOfBoundsException a) {
                            }
                            try {
                                highlighter.addHighlight(ogPosition-1, ogPosition, redPainter);
                                if (ogLastPos1 > -1) {
                                    highlighter.addHighlight(ogLastPos1-1, ogLastPos1, bluePainter);
                                }
                                if (ogLastPos2 > -1) {
                                    highlighter.addHighlight(ogLastPos2-1, ogLastPos2, greenPainter);
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
        if (letter == 'r') {
            System.out.println();
        }
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

    private static class MyArea extends TextArea {
    }
}
