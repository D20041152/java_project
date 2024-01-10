package components;

import event.DragEventHandler;
import event.PressReleaseHandler;
import model.Atom;
import model.AtomsFactory;
import model.Molecule;
import model.Position;
import model.AtomType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class MoleculeConstructor extends JComponent {
    private final int MENU_LINE_HEIGHT = 120;
    private final int MENU_WIDTH = 200;
    private Molecule state = new Molecule();
    private Atom freeAtom;
    private Atom selectedAtom;
    private volatile double factor = 1.0;
    private final Object scrollLock = new Object();

    public MoleculeConstructor() {
        final Atom[] movingAtom = new Atom[]{null};
        final Position[] activeAtomShift = new Position[]{null};
        final int[] dragged = new int[]{0};
        this.addMouseListener(new PressReleaseHandler() {
            public void mousePressed(MouseEvent e) {
                Position position = new Position((double)e.getX(), (double)e.getY());
                if (MoleculeConstructor.this.intersectMenu(position)) {
                    int menuIndex = MoleculeConstructor.this.getMenuIndex(position);
                    MoleculeConstructor.this.freeAtom = AtomsFactory.create(AtomType.values()[menuIndex], position);
                } else {
                    dragged[0] = 0;
                    Iterator var5 = MoleculeConstructor.this.state.atoms.iterator();

                    while(var5.hasNext()) {
                        Atom atom = (Atom)var5.next();
                        if (atom.intersect(position, MoleculeConstructor.this.factor)) {
                            movingAtom[0] = atom;
                            activeAtomShift[0] = atom.position.minus(position);
                            return;
                        }
                    }
                }

            }

            public void mouseReleased(MouseEvent e) {
                Position position = new Position((double)e.getX(), (double)e.getY());
                if (MoleculeConstructor.this.intersectMenu(position)) {
                    MoleculeConstructor.this.freeAtom = null;
                } else if (MoleculeConstructor.this.freeAtom != null) {
                    MoleculeConstructor.this.state.addAtom(MoleculeConstructor.this.freeAtom);
                    MoleculeConstructor.this.freeAtom = null;
                } else {
                    if (dragged[0] < 5) {
                        if (movingAtom[0] != null) {
                            if (movingAtom[0] == MoleculeConstructor.this.selectedAtom) {
                                MoleculeConstructor.this.selectedAtom = null;
                            } else if (MoleculeConstructor.this.selectedAtom == null) {
                                MoleculeConstructor.this.selectedAtom = movingAtom[0];
                            } else {
                                MoleculeConstructor.this.state.addEdge(MoleculeConstructor.this.selectedAtom, movingAtom[0]);
                                MoleculeConstructor.this.selectedAtom = null;
                            }
                        } else {
                            MoleculeConstructor.this.selectedAtom = null;
                        }
                    }

                    movingAtom[0] = null;
                    dragged[0] = 0;
                }

                MoleculeConstructor.this.repaint();
            }
        });
        this.addMouseMotionListener((DragEventHandler)(it) -> {
            Position position = new Position((double)it.getX(), (double)it.getY());
            if (!this.outOfComponent(position)) {
                if (this.freeAtom != null) {
                    this.freeAtom.position = position;
                    this.repaint();
                } else if (movingAtom[0] != null) {
                    movingAtom[0].position = position.plus(activeAtomShift[0]);
                    int var10002 = dragged[0]++;
                    this.repaint();
                }

            }
        });
        this.addMouseWheelListener((it) -> {
            AtomicReference<Double> scrollAmount = new AtomicReference(it.getPreciseWheelRotation() * -0.25);
            double delta = (Double)scrollAmount.get() / 20.0;
            (new Thread(() -> {
                while(Math.abs((Double)scrollAmount.get()) > 0.1) {
                    scrollAmount.updateAndGet((v) -> {
                        return v - delta;
                    });
                    synchronized(this.scrollLock) {
                        double newFactor = this.factor * (delta + 1.0);
                        if (newFactor < 0.25 || newFactor > 4.0) {
                            return;
                        }

                        this.factor = newFactor;
                        Iterator var8 = this.state.atoms.iterator();

                        while(true) {
                            if (!var8.hasNext()) {
                                break;
                            }

                            Atom atom = (Atom)var8.next();
                            atom.position = atom.position.updateZoom(new Position((double)it.getX(), (double)it.getY()), delta + 1.0);
                        }
                    }

                    this.repaint();

                    try {
                        Thread.sleep(5L);
                    } catch (InterruptedException var11) {
                        throw new RuntimeException(var11);
                    }
                }

            })).start();
        });
        this.registerKeyboardAction((e) -> {
            if (this.selectedAtom != null) {
                this.state.removeAtom(this.selectedAtom);
                this.selectedAtom = null;
                this.repaint();
            }

        }, KeyStroke.getKeyStroke("DELETE"), 2);
    }

    public void saveToFile(File saveFile) {
        try {
            FileWriter fileWriter = new FileWriter(saveFile);

            try {
                this.state.saveTo(fileWriter);
                fileWriter.write(this.factor + "\n");
            } catch (Throwable var6) {
                try {
                    fileWriter.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            fileWriter.close();
        } catch (Exception var7) {
            throw new RuntimeException("Error while saving to file", var7);
        }
    }

    public void restoreFromFile(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            try {
                this.state = Molecule.readFrom(bufferedReader);
                this.factor = Double.parseDouble(bufferedReader.readLine());
            } catch (Throwable var6) {
                try {
                    bufferedReader.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            bufferedReader.close();
        } catch (Exception var7) {
            throw new RuntimeException("Error while reading from file", var7);
        }

        this.repaint();
    }

    protected void paintComponent(Graphics g) {
        if (!(g instanceof Graphics2D)) {
            throw new RuntimeException("graphics is not graphics2d");
        } else {
            Graphics2D canvas = (Graphics2D)g;
            configureCanvas(canvas);
            this.state.paintTo(canvas, this.selectedAtom, this.factor);
            this.paintMenu(canvas);
            if (this.freeAtom != null) {
                this.freeAtom.paintTo(canvas, true, this.factor);
            }

        }
    }

    private boolean intersectMenu(Position position) {
        return position.y >= 0.0 && position.y <= (double)this.getMenuHeight() && position.x <= (double)this.getWidth() && position.x >= (double)(this.getWidth() - 200);
    }

    private void paintMenu(Graphics2D canvas) {
        canvas.setColor(new Color(150, 150, 150, 150));
        canvas.fillRoundRect(this.getWidth() - 200, 0, 200, this.getMenuHeight(), 20, 20);
        int currentY = 60;
        AtomType[] atomTypes = AtomType.values();

        for(int i = 0; i < atomTypes.length; ++i) {
            Atom atom = AtomsFactory.create(atomTypes[i], new Position((double)(this.getWidth() - 200 + 60), (double)currentY));
            atom.paintTo(canvas, false, 1.0);
            canvas.setColor(Color.BLACK);
            canvas.setFont(new Font("TimesRoman", 0, 45));
            canvas.drawString(atomTypes[i].name(), this.getWidth() - 200 + 130, currentY + 15);
            currentY += 120;
            canvas.setStroke(new BasicStroke(1.0F));
            if (i < atomTypes.length - 1) {
                canvas.drawLine(this.getWidth() - 200 + 1, currentY - 60, this.getWidth(), currentY - 60);
            }
        }

    }

    private int getMenuIndex(Position position) {
        return Math.min((int)position.y / 120, AtomType.values().length - 1);
    }

    private int getMenuHeight() {
        return 120 * AtomType.values().length;
    }

    private boolean outOfComponent(Position position) {
        return position.x < 0.0 || position.x > (double)this.getWidth() || position.y < 0.0 || position.y > (double)this.getHeight();
    }

    private static void configureCanvas(Graphics2D canvas) {
        canvas.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        canvas.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        canvas.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        canvas.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        canvas.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }
}