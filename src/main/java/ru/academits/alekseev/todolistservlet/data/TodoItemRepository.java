package ru.academits.alekseev.todolistservlet.data;

import java.util.List;

public interface TodoItemRepository {
    List<TodoItem> getAll();

    void create(String todoItemText);

    void update(TodoItem todoItem);

    void delete(int todoItemId);
}
