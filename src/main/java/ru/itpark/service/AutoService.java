package ru.itpark.service;

import ru.itpark.domain.Auto;
import ru.itpark.util.JdbcTemplate;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AutoService {
    private final DataSource ds;

    public AutoService() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        ds = (DataSource) context.lookup("java:/comp/env/jdbc/db");
        String sql = "CREATE TABLE IF NOT EXISTS autos (id TEXT PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, image TEXT);";
        JdbcTemplate.execute(ds, sql);
    }

    public List<Auto> getAll() throws SQLException {
        try (Connection connection = ds.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT id, name, description, image FROM autos")) {
                    List<Auto> list = new ArrayList<>();
                    while (resultSet.next()) {
                        list.add(new Auto(
                                resultSet.getString("id"),
                                resultSet.getString("name"),
                                resultSet.getString("description"),
                                resultSet.getString("image")
                        ));
                    }
                    return list;
                }
            }
        }
    }

    public void create(String name, String description, String image) throws SQLException {
        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO autos (id, name, description, image) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1, UUID.randomUUID().toString());
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, description);
                preparedStatement.setString(4, image);
                preparedStatement.execute();
            }
        }
    }
}
