import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.event.ActionEvent;

public class UndoTool {
    public class managerIgnoreStyles extends UndoManager{

        public synchronized boolean addEdit(UndoableEdit anEdit)
        {
            if (anEdit instanceof AbstractDocument.DefaultDocumentEvent)
            {
                AbstractDocument.DefaultDocumentEvent de =
                        (AbstractDocument.DefaultDocumentEvent)anEdit;
                if (de.getType() == DocumentEvent.EventType.CHANGE )
                {
                    return false;
                }
            }
            return super.addEdit(anEdit);
        }
    }
    private static final String REDO_KEY = "redo";
    private static final String UNDO_KEY = "undo";

    private JTextComponent component;
    private KeyStroke undo = KeyStroke.getKeyStroke("control Z");
    private KeyStroke redo = KeyStroke.getKeyStroke("control Y");

    public UndoTool(JTextComponent component) {
        this.component = component;
    }

    public void setUndo(KeyStroke undo) {
        this.undo = undo;
    }

    public void setRedo(KeyStroke redo) {
        this.redo = redo;
    }

    public static void addUndoFunctionality(JTextComponent component) {
        UndoTool tool = new UndoTool(component);
        managerIgnoreStyles undo = (managerIgnoreStyles)tool.createAndBindUndoManager();
        tool.bindUndo(undo);
        tool.bindRedo(undo);
    }

    public managerIgnoreStyles createAndBindUndoManager() {
//        Check.notNull(component); find out what this line means
        managerIgnoreStyles manager = new managerIgnoreStyles();
        Document document = component.getDocument();
        document.addUndoableEditListener(event -> {
            if(event.getEdit().isSignificant() ) {
                manager.addEdit(event.getEdit());
            }
        });
        return manager;
    }

    public void bindRedo(managerIgnoreStyles manager) {
        component.getActionMap().put(REDO_KEY, new AbstractAction(REDO_KEY) {

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (manager.canRedo()) {
                        manager.redo();
                    }
                } catch (CannotRedoException ignore) {
                }
            }
        });
        component.getInputMap().put(redo, REDO_KEY);
    }

    public void bindUndo(managerIgnoreStyles manager) {
        component.getActionMap().put(UNDO_KEY, new AbstractAction(UNDO_KEY) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (manager.canUndo()) {
                        manager.undo();
                    }
                } catch (CannotUndoException ignore) {
                }
            }
        });
        component.getInputMap().put(undo, UNDO_KEY);
    }
}