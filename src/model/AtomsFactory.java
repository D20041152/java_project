package model;

import model.AtomType;
import model.RoundAtom;
import model.StringAtom;

import java.awt.*;

public class AtomsFactory {
    public AtomsFactory() {
    }

    public static Atom create(AtomType type, Position position) {
        switch (type) {
            case H:
                return new RoundAtom(position, Color.CYAN, 30, AtomType.H);
            case N:
                return new RoundAtom(position, Color.MAGENTA, 40, AtomType.N);
            case C:
                return new RoundAtom(position, Color.YELLOW, 40, AtomType.C);
            case O:
                return new RoundAtom(position, Color.BLUE, 50, AtomType.O);
            case R:
                return new StringAtom(position, "R", AtomType.R);
            default:
                throw new RuntimeException("Unknown atom type in factory");
        }
    }
}
