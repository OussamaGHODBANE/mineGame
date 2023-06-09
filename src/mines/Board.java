package mines;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.SecureRandom;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {
    private static final long serialVersionUID = 6195235521361212179L;

    private static final int NUM_IMAGES = 13;
    private static final int CELL_SIZE = 15;

    private static final int COVER_FOR_CELL = 10;
    private static final int MARK_FOR_CELL = 10;
    private static final int EMPTY_CELL = 0;
    private static final int MINE_CELL = 9;
    private static final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    private static final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;

    private static final int DRAW_MINE = 9;
    private static final int DRAW_COVER = 10;
    private static final int DRAW_MARK = 11;
    private static final int DRAW_WRONG_MARK = 12;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private transient Image[] img;
    private int mines = 40;
    private int rows = 16;
    private int cols = 16;
    private int allCells;
    private JLabel statusbar;

    private SecureRandom random = new SecureRandom();

    public Board(JLabel statusbar) {

        this.statusbar = statusbar;

        img = new Image[NUM_IMAGES];

        for (int i = 0; i < NUM_IMAGES; i++) {

            img[i] = (new ImageIcon(getClass().getClassLoader().getResource("./images/" + i + ".gif"))).getImage();
        }

        setDoubleBuffered(true);

        addMouseListener(new MinesAdapter());
        newGame();
    }

    public void newGame() {

        int i = 0;
        int position = 0;

        inGame = true;
        minesLeft = mines;

        allCells = rows * cols;
        field = new int[allCells];

        for (i = 0; i < allCells; i++)
            field[i] = COVER_FOR_CELL;

        statusbar.setText(Integer.toString(minesLeft));

        i = 0;
        while (i < mines) {

            position = random.nextInt(0, allCells);

            if ((position < allCells) && (field[position] != COVERED_MINE_CELL)) {

                field[position] = COVERED_MINE_CELL;
                i++;
                putMines(position);

            }
        }
    }

    private void putMines(int position) {

        int cell;

        pmDepSt(position);

        cell = position - cols;
        if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
            field[cell] += 1;
        cell = position + cols;
        if (cell < allCells && field[cell] != COVERED_MINE_CELL)
            field[cell] += 1;

        pmDepNd(position);
    }

    private void pmDepSt(int position) {
        int currentCol = position % cols;
        if (currentCol > 0) {
            int cell;
            cell = position - 1 - cols;
            if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                field[cell] += 1;
            cell = position - 1;
            if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                field[cell] += 1;

            cell = position + cols - 1;
            if (cell < allCells && field[cell] != COVERED_MINE_CELL)
                field[cell] += 1;
        }
    }

    private void pmDepNd(int position) {
        int currentCol = position % cols;
        if (currentCol < (cols - 1)) {
            int cell;
            cell = position - cols + 1;
            if (cell >= 0 && field[cell] != COVERED_MINE_CELL)
                field[cell] += 1;
            cell = position + cols + 1;
            if (cell < allCells && field[cell] != COVERED_MINE_CELL)
                field[cell] += 1;
            cell = position + 1;
            if (cell < allCells && field[cell] != COVERED_MINE_CELL)
                field[cell] += 1;
        }
    }

    public void findEmptyCells(int j) {
        int cell;

        int currentCol = j % cols;
        if (currentCol > 0) {
            fecDepSt(j);
        }

        cell = j - cols;
        if (cell >= 0 && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

        cell = j + cols;
        if (cell < allCells && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

        if (currentCol < (cols - 1)) {
            fecDepNd(j);
        }
    }

    private void fecDepSt(int j) {

        int cell;
        cell = j - cols - 1;
        if (cell >= 0 && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

        cell = j - 1;
        if (cell >= 0 && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

        cell = j + cols - 1;
        if (cell < allCells && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

    }

    private void fecDepNd(int j) {

        int cell;
        cell = j - cols + 1;
        if (cell >= 0 && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

        cell = j + cols + 1;
        if (cell < allCells && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

        cell = j + 1;
        if (cell < allCells && field[cell] > MINE_CELL) {
            field[cell] -= COVER_FOR_CELL;
            if (field[cell] == EMPTY_CELL)
                findEmptyCells(cell);
        }

    }

    @Override
    public void paint(Graphics g) {

        int cell;
        int uncover = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cell = field[(i * cols) + j];
                if (inGame && cell == MINE_CELL)
                    inGame = false;

                cell = checkCell(cell);
                if (inGame && cell == DRAW_COVER)
                    uncover++;
                g.drawImage(img[cell], (j * CELL_SIZE), (i * CELL_SIZE), this);
            }
        }

        isWiner(uncover);
    }

    private int checkCell(int cell) {
        if (!inGame) {
            if (cell == COVERED_MINE_CELL)
                return DRAW_MINE;
            else if (cell == MARKED_MINE_CELL)
                return DRAW_MARK;
            else if (cell > COVERED_MINE_CELL)
                return DRAW_WRONG_MARK;
            else if (cell > MINE_CELL)
                return DRAW_COVER;
        } else {
            if (cell > COVERED_MINE_CELL) {
                return DRAW_MARK;
            } else if (cell > MINE_CELL) {
                return DRAW_COVER;
            }
        }
        return cell;
    }

    private void isWiner(int uncover) {
        if (uncover == 0 && inGame) {
            inGame = false;
            statusbar.setText("Game won");
        } else if (!inGame)
            statusbar.setText("Game lost");
    }

    class MinesAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {

            int x = e.getX();
            int y = e.getY();

            int cCol = x / CELL_SIZE;
            int cRow = y / CELL_SIZE;

            boolean rep = false;

            if (!inGame) {
                newGame();
                repaint();
            }

            if ((x < cols * CELL_SIZE) && (y < rows * CELL_SIZE)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    rep = mpdDepT(cCol, cRow);

                } else {

                    if (field[(cRow * cols) + cCol] > COVERED_MINE_CELL) {
                        return;
                    }
                    rep = mpdDepF(cCol, cRow);
                }

                if (rep)
                    repaint();

            }
        }

        private boolean mpdDepT(int cCol, int cRow) {
            if (field[(cRow * cols) + cCol] > MINE_CELL) {

                if (field[(cRow * cols) + cCol] <= COVERED_MINE_CELL) {
                    if (minesLeft > 0) {
                        field[(cRow * cols) + cCol] += MARK_FOR_CELL;
                        minesLeft--;
                        statusbar.setText(Integer.toString(minesLeft));
                    } else
                        statusbar.setText("No marks left");
                } else {

                    field[(cRow * cols) + cCol] -= MARK_FOR_CELL;
                    minesLeft++;
                    statusbar.setText(Integer.toString(minesLeft));
                }

                return true;
            }
            return false;
        }

        private boolean mpdDepF(int cCol, int cRow) {
            if ((field[(cRow * cols) + cCol] > MINE_CELL) &&
                    (field[(cRow * cols) + cCol] < MARKED_MINE_CELL)) {

                field[(cRow * cols) + cCol] -= COVER_FOR_CELL;

                if (field[(cRow * cols) + cCol] == MINE_CELL)
                    inGame = false;
                if (field[(cRow * cols) + cCol] == EMPTY_CELL)
                    findEmptyCells((cRow * cols) + cCol);
                return true;
            }
            return false;
        }
    }
}