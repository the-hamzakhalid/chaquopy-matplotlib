package com.chaquo.myapplication.utils.view.paintview.listener;

public interface UndoCommand {
    void undo();
    void redo();
    boolean canUndo();
    boolean canRedo();
}
