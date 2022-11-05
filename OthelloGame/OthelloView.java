import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class OthelloView {
    private final JLabel k = new JLabel("1");
    private final int[][] mat = new int[8][8];
    private JButton jShow;
    private JButton jPass;
    private JLabel jStat;
    private JLabel jNote;
    private JButton[][] jb;
    private int game = 0;
    private boolean shown = false;

    public OthelloView() {
        JFrame f = this.initFrame();

        jb = initButton();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                f.add(jb[i][j]);
            }
        }

        this.jStat = initStat();
        f.add(this.jStat);

        this.jNote = initNote();
        f.add(this.jNote);

        this.jPass = initPassButton();
        f.add(jPass);

        this.jShow = initShowButton();
        f.add(this.jShow);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                jb[i][j].addActionListener(event -> {
                    for (int i1 = 0; i1 < 8; i1++) {
                        for (int j1 = 0; j1 < 8; j1++) {
                            if (event.getSource() == jb[i1][j1] && game == 0) {
                                if (shown) hide();
                                click(i1, j1, Integer.parseInt(k.getText()));
                                k.setText(String.valueOf(Integer.parseInt(k.getText()) + 1));
                            }
                        }
                    }
                });
            }
        }

        JLabel endLabel = new JLabel();
        f.add(endLabel);
        initialise();
    }

    public JFrame initFrame() {
        JFrame f = new JFrame("Othello PvP");
        f.getContentPane().setBackground(new Color(80, 48, 12));
        f.pack();
        f.setBackground(new Color(80, 48, 12));
        f.setSize(646, 830);
        f.setResizable(false);
        f.setVisible(true);
        return f;
    }

    public JButton[][] initButton() {
        this.jb = new JButton[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                jb[i][j] = new JButton("");
                jb[i][j].setMargin(new Insets(0, 0, 0, 0));
                jb[i][j].setOpaque(true);
                jb[i][j].setBackground(new Color(78, 146, 82));
                jb[i][j].setForeground(Color.white);
                jb[i][j].setBounds(40 + 70 * j, 40 + 70 * i, 70, 70);
                jb[i][j].setBorder(new LineBorder(new Color(0, 90, 0)));
                jb[i][j].setFont(new Font("Courier", Font.PLAIN, 80));
                jb[i][j].setPreferredSize(new Dimension(70, 70));
            }
        }

        return jb;
    }

    public JLabel initStat() {
        this.jStat = new JLabel("");
        jStat.setOpaque(true);
        jStat.setBounds(2, 640, 636, 46);
        jStat.setBackground(new Color(51, 0, 7));
        jStat.setForeground(new Color(240, 230, 181));
        jStat.setText("White - 2    Black - 2");
        jStat.setFont(new Font("Arial", Font.PLAIN, 40));
        jStat.setBorder(BorderFactory.createEmptyBorder());
        jStat.setHorizontalAlignment(SwingConstants.CENTER);

        return jStat;
    }

    public JLabel initNote() {
        this.jNote = new JLabel("");
        jNote.setOpaque(true);
        jNote.setBounds(2, 690, 636, 46);
        jNote.setBackground(new Color(51, 0, 7));
        jNote.setForeground(new Color(240, 230, 181));
        jNote.setText("Black's turn");
        jNote.setFont(new Font("Arial", Font.PLAIN, 24));
        jNote.setBorder(BorderFactory.createEmptyBorder());
        jNote.setHorizontalAlignment(SwingConstants.CENTER);

        return jNote;
    }

    public JButton initPassButton() {
        this.jPass = new JButton("");
        jPass.setMargin(new Insets(0, 0, 0, 0));
        jPass.setOpaque(true);
        jPass.setBounds(4, 740, 314, 46);
        jPass.setBackground(new Color(51, 0, 7));
        jPass.setForeground(new Color(240, 230, 181));
        jPass.setText("PASS MOVE");
        jPass.setFont(new Font("Arial", Font.BOLD, 24));
        jPass.setBorder(new LineBorder(new Color(240, 230, 181)));
        jPass.addActionListener(event -> {
            if (shown) hide();
            pass();
        });

        return jPass;
    }

    public JButton initShowButton() {
        this.jShow = new JButton("");
        jShow.setMargin(new Insets(0, 0, 0, 0));
        jShow.setOpaque(true);
        jShow.setBounds(322, 740, 314, 46);
        jShow.setBackground(new Color(51, 0, 7));
        jShow.setForeground(new Color(240, 230, 181));
        jShow.setText("SHOW MOVE");
        jShow.setFont(new Font("Arial", Font.BOLD, 24));
        jShow.setBorder(new LineBorder(new Color(240, 230, 181)));
        jShow.addActionListener(event -> {
            if (shown) hide();
            else show();
        });

        return jShow;
    }

    private void pass() {
        int z = Integer.parseInt(k.getText());
        if ((z % 2 != 0 && noValid(1, 0)) || (z % 2 == 0 && noValid(2, 0))) k.setText(String.valueOf(z + 1));
        else jNote.setText((jNote.getText()).substring(0, 12) + "  [Valid move possible]");
    }

    private boolean noValid(int p, int r) {
        int i, j, x, y, m, n, c;
        int q = 3 - p;
        boolean ret = true;
        for (x = 0; x < 8; x++) {
            for (y = 0; y < 8; y++) {
                for (i = 0; i < 8; i++) {
                    for (j = 0; j < 8; j++) {
                        if (mat[i][j] == p) {
                            if (j == y && i < x - 1) {
                                c = 0;
                                for (int k = i + 1; k < x; k++)
                                    if (mat[k][y] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (j == y && i > x + 1) {
                                c = 0;
                                for (int k = x + 1; k < i; k++)
                                    if (mat[k][y] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i == x && j < y - 1) {
                                c = 0;
                                for (int k = j + 1; k < y; k++)
                                    if (mat[x][k] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i == x && j > y + 1) {
                                c = 0;
                                for (int k = y + 1; k < j; k++)
                                    if (mat[x][k] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (x - i == y - j && x - i > 1 && y - j > 1) {
                                c = 0;
                                for (m = i + 1, n = j + 1; m < x && n < y; m++, n++)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (x - i == j - y && x - i > 1 && j - y > 1) {
                                c = 0;
                                for (m = i + 1, n = j - 1; m < x && n > y; m++, n--)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i - x == y - j && i - x > 1 && y - j > 1) {
                                c = 0;
                                for (m = i - 1, n = j + 1; m > x && n < y; m--, n++)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                            if (i - x == j - y && i - x > 1 && j - y > 1) {
                                c = 0;
                                for (m = i - 1, n = j - 1; m > x && n > y; m--, n--)
                                    if (mat[m][n] != q) c++;
                                if (c == 0) {
                                    if (r == 1 && mat[x][y] == 0) jb[x][y].setBackground(new Color(219, 221, 117));
                                    ret = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private void show() {
        int z = Integer.parseInt(k.getText());
        /*boolean val =*/
        noValid(2 - (z % 2), 1);
        this.shown = true;
        jShow.setText("HIDE MOVE");
    }

    private void hide() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                jb[i][j].setBackground(new Color(78, 146, 82));
        shown = false;
        jShow.setText("SHOW MOVE");
    }


    private void click(int x, int y, int z) {
        int f = mat[x][y];
        if (z % 2 != 0) {
            jb[x][y].setForeground(Color.black);
            mat[x][y] = 1;
        } else {
            jb[x][y].setForeground(Color.white);
            mat[x][y] = 2;
        }
        jb[x][y].setText("⚫");
        int e = 0;
        if (f == 0) e = flip(x, y, z);
        if (e == 0) {
            k.setText(String.valueOf(Integer.parseInt(k.getText()) - 1));
            mat[x][y] = f;
            if (f == 0) jb[x][y].setText("");
            if (f == 1) jb[x][y].setForeground(Color.black);
            if (f == 2) jb[x][y].setForeground(Color.white);
            jNote.setText((jNote.getText()).substring(0, 12) + "  [Invalid move, try again]");
        } else {
            if (z % 2 != 0) jNote.setText("White's turn");
            else jNote.setText("Black's turn");
        }
        count();
    }

    private int flip(int x, int y, int z) {
        int i, j, m, n, d = 0;
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                if (z % 2 != 0 && mat[i][j] == 1) {
                    if (j == y && i < x - 1) {
                        int c = 0;
                        for (int k = i + 1; k < x; k++)
                            if (mat[k][y] != 2) c++;
                        if (c == 0) {
                            for (int k = i + 1; k < x; k++) {
                                mat[k][y] = 1;
                                jb[k][y].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (j == y && i > x + 1) {
                        int c = 0;
                        for (int k = x + 1; k < i; k++)
                            if (mat[k][y] != 2) c++;
                        if (c == 0) {
                            for (int k = x + 1; k < i; k++) {
                                mat[k][y] = 1;
                                jb[k][y].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (i == x && j < y - 1) {
                        int c = 0;
                        for (int k = j + 1; k < y; k++)
                            if (mat[x][k] != 2) c++;
                        if (c == 0) {
                            for (int k = j + 1; k < y; k++) {
                                mat[x][k] = 1;
                                jb[x][k].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (i == x && j > y + 1) {
                        int c = 0;
                        for (int k = y + 1; k < j; k++)
                            if (mat[x][k] != 2) c++;
                        if (c == 0) {
                            for (int k = y + 1; k < j; k++) {
                                mat[x][k] = 1;
                                jb[x][k].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (x - i == y - j && x - i > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i + 1, n = j + 1; m < x && n < y; m++, n++)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j + 1; m < x && n < y; m++, n++) {
                                mat[m][n] = 1;
                                jb[m][n].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (x - i == j - y && x - i > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i + 1, n = j - 1; m < x && n > y; m++, n--)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j - 1; m < x && n > y; m++, n--) {
                                mat[m][n] = 1;
                                jb[m][n].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (i - x == y - j && i - x > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i - 1, n = j + 1; m > x && n < y; m--, n++)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j + 1; m > x && n < y; m--, n++) {
                                mat[m][n] = 1;
                                jb[m][n].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                    if (i - x == j - y && i - x > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i - 1, n = j - 1; m > x && n > y; m--, n--)
                            if (mat[m][n] != 2) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j - 1; m > x && n > y; m--, n--) {
                                mat[m][n] = 1;
                                jb[m][n].setForeground(Color.black);
                            }
                            d++;
                        }
                    }
                }
                if (z % 2 == 0 && mat[i][j] == 2) {
                    if (j == y && i < x - 1) {
                        int c = 0;
                        for (int k = i + 1; k < x; k++)
                            if (mat[k][y] != 1) c++;
                        if (c == 0) {
                            for (int k = i + 1; k < x; k++) {
                                mat[k][y] = 2;
                                jb[k][y].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (j == y && i > x + 1) {
                        int c = 0;
                        for (int k = x + 1; k < i; k++)
                            if (mat[k][y] != 1) c++;
                        if (c == 0) {
                            for (int k = x + 1; k < i; k++) {
                                mat[k][y] = 2;
                                jb[k][y].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (i == x && j < y - 1) {
                        int c = 0;
                        for (int k = j + 1; k < y; k++)
                            if (mat[x][k] != 1) c++;
                        if (c == 0) {
                            for (int k = j + 1; k < y; k++) {
                                mat[x][k] = 2;
                                jb[x][k].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (i == x && j > y + 1) {
                        int c = 0;
                        for (int k = y + 1; k < j; k++)
                            if (mat[x][k] != 1) c++;
                        if (c == 0) {
                            for (int k = y + 1; k < j; k++) {
                                mat[x][k] = 2;
                                jb[x][k].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (x - i == y - j && x - i > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i + 1, n = j + 1; m < x && n < y; m++, n++)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j + 1; m < x && n < y; m++, n++) {
                                mat[m][n] = 2;
                                jb[m][n].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (x - i == j - y && x - i > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i + 1, n = j - 1; m < x && n > y; m++, n--)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i + 1, n = j - 1; m < x && n > y; m++, n--) {
                                mat[m][n] = 2;
                                jb[m][n].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (i - x == y - j && i - x > 1 && y - j > 1) {
                        int c = 0;
                        for (m = i - 1, n = j + 1; m > x && n < y; m--, n++)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j + 1; m > x && n < y; m--, n++) {
                                mat[m][n] = 2;
                                jb[m][n].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                    if (i - x == j - y && i - x > 1 && j - y > 1) {
                        int c = 0;
                        for (m = i - 1, n = j - 1; m > x && n > y; m--, n--)
                            if (mat[m][n] != 1) c++;
                        if (c == 0) {
                            for (m = i - 1, n = j - 1; m > x && n > y; m--, n--) {
                                mat[m][n] = 2;
                                jb[m][n].setForeground(Color.white);
                            }
                            d++;
                        }
                    }
                }
            }
        }
        return d;
    }

    private void initialise() {
        jb[3][3].setForeground(Color.white);
        jb[3][3].setText("⚫");
        jb[4][4].setForeground(Color.white);
        jb[4][4].setText("⚫");
        jb[3][4].setForeground(Color.black);
        jb[3][4].setText("⚫");
        jb[4][3].setForeground(Color.black);
        jb[4][3].setText("⚫");
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                mat[i][j] = 0;
        mat[3][3] = 2;
        mat[4][4] = 2;
        mat[3][4] = 1;
        mat[4][3] = 1;
    }

    private void count() {
        int b = 0;
        int w = 0;
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (mat[i][j] == 1) b++;
                if (mat[i][j] == 2) w++;
            }
        jStat.setText("White - " + w + "    Black - " + b);
        if (b + w == 64) {
            if (b > w) jNote.setText("Black WINS (All squares filled)");
            else if (w > b) jNote.setText("White WINS (All squares filled)");
            else jNote.setText("Game TIED (All squares filled)");
            game = 1;
        }
        if (noValid(1, 0) && noValid(2, 0)) {
            if (b > w) jNote.setText("Black WINS (No valid moves left)");
            else if (w > b) jNote.setText("White WINS (No valid moves left)");
            else jNote.setText("Game TIED (No valid moves left)");
            game = 1;
        }
    }
}
