package event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface PressReleaseHandler extends MouseListener {
    default void mouseClicked(MouseEvent e) {

    }

    default void mouseEntered(MouseEvent e) {

    }

    default void mouseExited(MouseEvent e) {

    }
}
