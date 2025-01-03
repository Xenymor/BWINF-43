package cc.retzlaff.timon.round2.simultaneLabyrinthe;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Labyrinth {
    final int width;
    final int height;

    final Field[][] fields;
    final int holeCount;
    private final Vector2 finish;
    private final Vector2 start;

    MyFrame frame = null;

    public Labyrinth(final List<String> input) {
        String[] size = input.get(0).split(" ");
        width = Integer.parseInt(size[0]);
        height = Integer.parseInt(size[1]);

        finish = new Vector2(width - 1, height - 1);
        start = new Vector2(0, 0);

        fields = new Field[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                fields[x][y] = new Field();
            }
        }

        int offset = 1;
        for (int y = 0; y < height; y++) {
            String[] values = input.get(y + offset).split(" ");
            for (int x = 0; x < width - 1; x++) {
                boolean hasRightWall = Integer.parseInt(values[x]) == 1;
                fields[x][y].setRightWall(hasRightWall);
            }
        }
        offset += height;

        for (int y = 0; y < height - 1; y++) {
            String[] values = input.get(y + offset).split(" ");
            for (int x = 0; x < width; x++) {
                boolean hasLowerWall = Integer.parseInt(values[x]) == 1;
                fields[x][y].setLowerWall(hasLowerWall);
            }
        }
        offset += height - 1;

        holeCount = Integer.parseInt(input.get(offset));
        offset++;

        for (int i = 0; i < holeCount; i++) {
            String[] coords = input.get(i + offset).split(" ");
            final int x = Integer.parseInt(coords[0]);
            final int y = Integer.parseInt(coords[1]);
            fields[x][y].setIsHole(true);
        }
        offset += holeCount;
    }

    public Vector2 getFinishPos() {
        return finish;
    }

    public List<Vector2> getPossibleFields(final Vector2 curr) {
        List<Vector2> result = new ArrayList<>(4);
        final int y = curr.y;
        final int x = curr.x;
        if (y > 0) {
            if (!fields[x][y - 1].hasLowerWall) {
                result.add(new Vector2(x, y - 1));
            }
        }
        if (y < height) {
            if (!fields[x][y].hasLowerWall) {
                result.add(new Vector2(x, y + 1));
            }
        }
        if (x > 0) {
            if (!fields[x - 1][y].hasRightWall) {
                result.add(new Vector2(x - 1, y));
            }
        }
        if (x < width) {
            if (!fields[x][y].hasRightWall) {
                result.add(new Vector2(x + 1, y));
            }
        }
        return result;
    }

    public Vector2 getStartPos() {
        return start;
    }

    public void draw(final int xOffset, final int fieldSize) {
        if (frame != null) {
            frame.setVisible(false);
            frame.dispose();
        }
        frame = new MyFrame(this, fieldSize);
        final int pixelWidth = width * fieldSize;
        final int pixelHeight = height * fieldSize;
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setBounds(xOffset, 0, pixelWidth, pixelHeight);
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    public void drawSolution(final int xOffset, final int fieldSize, final List<Move> path) {
        if (frame == null) {
            draw(xOffset, fieldSize);
        }
        frame.path = path;
        frame.repaint(10);
    }

    public Vector2 getField(final Vector2 curr, final Move move) {
        if (curr.equals(finish)) {
            return curr;
        }

        Vector2 result;

        final int x = curr.x;
        final int y = curr.y;
        switch (move) {
            case LEFT -> {
                if (x > 0) {
                    if (fields[x - 1][y].hasRightWall) {
                        result = curr;
                    } else {
                        result = new Vector2(x - 1, y);
                    }
                } else {
                    result = curr;
                }
            }
            case RIGHT -> {
                if (x < width - 1) {
                    if (fields[x][y].hasRightWall) {
                        result = curr;
                    } else {
                        result = new Vector2(x + 1, y);
                    }
                } else {
                    result = curr;
                }
            }
            case UP -> {
                if (y > 0) {
                    if (fields[x][y - 1].hasLowerWall) {
                        result = curr;
                    } else {
                        result = new Vector2(x, y - 1);
                    }
                } else {
                    result = curr;
                }
            }
            case DOWN -> {
                if (y < height - 1) {
                    if (fields[x][y].hasLowerWall) {
                        result = curr;
                    } else {
                        result = new Vector2(x, y + 1);
                    }
                } else {
                    result = curr;
                }
            }
            default -> throw new IllegalArgumentException("Unknown move: " + move);
        }

        if (fields[result.x][result.y].isHole) {
            return getStartPos();
        }
        return result;
    }

    private static class MyFrame extends JFrame {
        final Labyrinth labyrinth;
        final int fieldSize;
        List<Move> path = null;

        public MyFrame(final Labyrinth labyrinth, final int fieldSize) {
            this.labyrinth = labyrinth;
            this.fieldSize = fieldSize;
        }

        @Override
        public void paint(final Graphics g) {
            Field[][] fields = labyrinth.fields;
            g.clearRect(0, 0, labyrinth.width * fieldSize, labyrinth.height * fieldSize);

            final int height = getHeight();
            final int width = getWidth();

            g.setColor(Color.GREEN);
            g.fillRect(0, 0, fieldSize / 20, height);
            g.fillRect(0, 0, width, fieldSize / 20);

            for (int x = 0; x < labyrinth.width; x++) {
                for (int y = 0; y < labyrinth.height; y++) {
                    Field field = fields[x][y];
                    if (field.hasRightWall) {
                        g.setColor(Color.GREEN);
                        g.fillRect((x + 1) * fieldSize - fieldSize / 20, y * fieldSize, fieldSize / 10, fieldSize);
                    }
                    if (field.hasLowerWall) {
                        g.setColor(Color.GREEN);
                        g.fillRect(x * fieldSize, (y + 1) * fieldSize - fieldSize / 20, fieldSize, fieldSize / 10);
                    }
                    if (field.isHole) {
                        g.setColor(Color.BLACK);
                        g.fillOval(x * fieldSize + fieldSize / 10, y * fieldSize + fieldSize / 10, fieldSize - fieldSize / 10 * 2, fieldSize - fieldSize / 10 * 2);
                    }
                }
            }

            if (path != null) {
                g.setColor(Color.RED);
                Vector2 currPos = labyrinth.getStartPos().clone();
                Vector2 next;
                for (int i = 0; i < path.size() - 1; i++) {
                    final Move currMove = path.get(i);
                    next = getField(currPos, currMove);

                    final int x = Math.min(currPos.x, next.x) * fieldSize + fieldSize / 2 - fieldSize / 20;
                    final int y = Math.min(currPos.y, next.y) * fieldSize + fieldSize / 2 - fieldSize / 20;
                    final int rectWidth = (Math.abs(next.x - currPos.x)) * fieldSize + fieldSize / 10;
                    final int rectHeight = (Math.abs(next.y - currPos.y)) * fieldSize + fieldSize / 10;

                    g.fillRect(x, y, rectWidth, rectHeight);

                    if (fields[next.x][next.y].isHole) {
                        next = labyrinth.getStartPos();
                    }
                    currPos = next;
                }
            }
        }

        Vector2 getField(Vector2 pos, Move move) {
            if (pos.equals(labyrinth.finish)) {
                return pos;
            }

            Vector2 result;

            final int x = pos.x;
            final int y = pos.y;
            switch (move) {
                case LEFT -> {
                    if (x > 0) {
                        if (labyrinth.fields[x - 1][y].hasRightWall) {
                            result = pos;
                        } else {
                            result = new Vector2(x - 1, y);
                        }
                    } else {
                        result = pos;
                    }
                }
                case RIGHT -> {
                    if (x < labyrinth.width - 1) {
                        if (labyrinth.fields[x][y].hasRightWall) {
                            result = pos;
                        } else {
                            result = new Vector2(x + 1, y);
                        }
                    } else {
                        result = pos;
                    }
                }
                case UP -> {
                    if (y > 0) {
                        if (labyrinth.fields[x][y - 1].hasLowerWall) {
                            result = pos;
                        } else {
                            result = new Vector2(x, y - 1);
                        }
                    } else {
                        result = pos;
                    }
                }
                case DOWN -> {
                    if (y < labyrinth.height - 1) {
                        if (labyrinth.fields[x][y].hasLowerWall) {
                            result = pos;
                        } else {
                            result = new Vector2(x, y + 1);
                        }
                    } else {
                        result = pos;
                    }
                }
                default -> throw new IllegalArgumentException("Unknown move: " + move);
            }
            return result;
        }
    }
}
