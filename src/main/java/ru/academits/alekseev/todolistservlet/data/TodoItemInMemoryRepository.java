package ru.academits.alekseev.todolistservlet.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TodoItemInMemoryRepository implements TodoItemRepository {
    private static final List<TodoItem> todoItems = new ArrayList<>();
    private static final AtomicInteger currentId = new AtomicInteger(1);

    @Override
    public List<TodoItem> getAll() {
        synchronized (todoItems) {
            return todoItems;
        }
    }

    @Override
    public void create(String todoItemText) {
        synchronized (todoItems) {
            todoItems.add(new TodoItem(currentId.getAndIncrement(), todoItemText));
        }
    }

    @Override
    public void update(TodoItem todoItem) {
        synchronized (todoItems) {
            TodoItem repositoryTodoItem = todoItems.stream()
                    .filter(item -> item.getId() == todoItem.getId())
                    .findFirst()
                    .orElse(null);

            if (repositoryTodoItem == null) {
                throw new IllegalArgumentException("TodoItem with id = " + todoItem.getId() + " does not exist");
            }

            repositoryTodoItem.setText(todoItem.getText());
        }
    }

    @Override
    public void delete(int todoItemId) {
        synchronized (todoItems) {
            todoItems.removeIf(item -> item.getId() == todoItemId);
        }
    }
}
