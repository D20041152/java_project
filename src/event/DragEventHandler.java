package event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public interface DragEventHandler extends MouseMotionListener {
    default void mouseMoved(MouseEvent e) {

    }
}
