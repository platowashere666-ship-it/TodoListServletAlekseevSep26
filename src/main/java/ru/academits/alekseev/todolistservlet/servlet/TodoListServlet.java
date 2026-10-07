package ru.academits.alekseev.todolistservlet.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;
import ru.academits.alekseev.todolistservlet.data.TodoItem;
import ru.academits.alekseev.todolistservlet.data.TodoItemInMemoryRepository;
import ru.academits.alekseev.todolistservlet.data.TodoItemRepository;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet("")
public class TodoListServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 34L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        HttpSession session = req.getSession();

        String createError = (String) session.getAttribute("createError");
        String createErrorHtml = "";

        if (createError != null) {
            createErrorHtml = "<div style=\"color: red;\">" + StringEscapeUtils.escapeHtml4(createError) + "</div>";
        }

        session.removeAttribute("createError");

        String saveError = (String) session.getAttribute("saveError");
        Integer saveErrorId = (Integer) session.getAttribute("saveErrorId");

        session.removeAttribute("saveError");
        session.removeAttribute("saveErrorId");

        String idError = (String) session.getAttribute("idError");
        String idErrorHtml = "";

        if (idError != null) {
            idErrorHtml = "<div style=\"color: red\">" + StringEscapeUtils.escapeHtml4(idError) + "</div>";
        }

        session.removeAttribute("idError");

        TodoItemRepository todoItemRepository = new TodoItemInMemoryRepository();
        List<TodoItem> todoItems = todoItemRepository.getAll();

        String baseUrl = req.getContextPath() + "/";

        StringBuilder todoItemsHtml = new StringBuilder();

        for (TodoItem todoItem : todoItems) {
            String saveErrorHtml = "";

            if (saveError != null && saveErrorId != null && saveErrorId.equals(todoItem.getId())) {
                saveErrorHtml = "<div style=\"color: red\">" + StringEscapeUtils.escapeHtml4(saveError) + "</div>";
            }

            todoItemsHtml.append("""
                    <li>
                        <form method="post" action="%s">
                            <input type="hidden" name="todoItemId" value="%s">
                    
                            <input type="text" name="todoItemText" value="%s">
                            <button type="submit" name="action" value="save">Save</button>
                            <button type="submit" name="action" value="delete">Delete</button>
                            %s
                        </form>
                    </li>
                    """.formatted(baseUrl, todoItem.getId(),
                    StringEscapeUtils.escapeHtml4(todoItem.getText()), saveErrorHtml));
        }

        resp.getWriter().println("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>TODO List</title>
                    <meta charset="UTF-8">
                </head>
                <body>
                    <h1>TODO List</h1>
                
                    %s
                
                    <form method="post" action="%s">
                        <input type="hidden" name="action" value="create">
                
                        <label>Enter text:</label>
                        <input type="text" name="todoItemText">
                        <button type="submit">Create</button>
                        %s
                    </form>
                
                    <ul>
                        %s
                    </ul>
                </body>
                </html>
                """.formatted(idErrorHtml, baseUrl, createErrorHtml, todoItemsHtml.toString()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        switch (action) {
            case "create" -> {
                String todoItemText = req.getParameter("todoItemText");

                if (todoItemText == null || todoItemText.isBlank()) {
                    HttpSession session = req.getSession();
                    session.setAttribute("createError", "Text must not be empty");
                } else {
                    TodoItemRepository todoItemRepository = new TodoItemInMemoryRepository();
                    todoItemRepository.create(todoItemText.trim());
                }
            }

            case "save" -> {
                try {
                    String todoItemText = req.getParameter("todoItemText");
                    String todoItemIdParameter = req.getParameter("todoItemId");
                    HttpSession session = req.getSession();

                    if (todoItemIdParameter == null || todoItemIdParameter.isBlank()) {
                        session.setAttribute("idError", "Item id must not be empty");
                    } else if (todoItemText == null || todoItemText.isBlank()) {
                        int todoItemId = Integer.parseInt(todoItemIdParameter);

                        session.setAttribute("saveError", "Text must not be empty");
                        session.setAttribute("saveErrorId", todoItemId);
                    } else {
                        int todoItemId = Integer.parseInt(todoItemIdParameter);

                        TodoItem todoItem = new TodoItem(todoItemId, todoItemText.trim());
                        TodoItemRepository todoItemRepository = new TodoItemInMemoryRepository();
                        todoItemRepository.update(todoItem);
                    }
                } catch (NumberFormatException e) {
                    HttpSession session = req.getSession();
                    session.setAttribute("idError", "Item id must be a number");
                }
            }

            case "delete" -> {
                try {
                    String todoItemIdParameter = req.getParameter("todoItemId");

                    if (todoItemIdParameter == null || todoItemIdParameter.isBlank()) {
                        HttpSession session = req.getSession();
                        session.setAttribute("idError", "Item id must not be empty");
                    } else {
                        int todoItemId = Integer.parseInt(todoItemIdParameter);

                        TodoItemRepository todoItemRepository = new TodoItemInMemoryRepository();
                        todoItemRepository.delete(todoItemId);
                    }
                } catch (NumberFormatException e) {
                    HttpSession session = req.getSession();
                    session.setAttribute("idError", "Item id must be a number");
                }
            }
        }

        resp.sendRedirect(req.getContextPath() + "/");
    }
}
