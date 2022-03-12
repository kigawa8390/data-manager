package net.kigawa.data.database;

import net.kigawa.data.data.Data;
import net.kigawa.kutil.log.log.Logger;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class Database {
    private final String url;
    private final String name;
    private final Set<Table> tableSet = new HashSet<>();
    private final Logger logger;
    private boolean migrate;
    private Connection connection;
    private int session;

    protected Database(Logger logger, String url, String name, boolean migrate) {
        this.url = url;
        this.name = name;
        this.logger = logger;
        this.migrate = migrate;

        if (migrate) migrate();

        close();
    }

    public void migrate() {
        logger.info("migrate DB: " + name);
        if (canUse()) return;
        createDB();
    }

    public void createDB() {
        logger.info("create DB: " + name);
        createConnection();
        executeUpdate("CREATE DATABASE IF NOT EXIST " + name);
        executeUpdate("use " + name);
        close();
    }

    public int executeUpdate(String sql,Data... data) {
        try {
            var st = getPreparedStatement(sql);
            if (st == null) return -1;
            for (Data data1 : data) {
                data1.addDataToStatement(st);
            }
            var result = st.executeUpdate();
            st.close();
            return result;
        } catch (SQLException e) {
            logger.warning(e);
            return -1;
        }
    }

    public ResultSet executeQuery(String sql, Data... data) {
        try {
            var st = getPreparedStatement(sql);
            if (st == null) return null;
            for (Data data1 : data) {
                data1.addDataToStatement(st);
            }
            var result = st.executeQuery();
            st.close();
            return result;
        } catch (SQLException e) {
            logger.warning(e);
            return null;
        }
    }

    public synchronized void close() {
        try {
            session--;
            if (session > 0) return;
            if (connection == null) return;
            if (!connection.isClosed()) connection.close();
            connection = null;
        } catch (SQLException e) {
            logger.warning(e);
        }
    }

    public PreparedStatement getPreparedStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (Exception e) {
            logger.warning(e);
            return null;
        }
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) logger.warning("connection is closed!");
            return connection;
        } catch (Exception e) {
            logger.warning(e);
            return null;
        }
    }


    private void createConnection() {
        session = 0;
        try {
            session++;
            if (connection == null || connection.isClosed())
                connection = DriverManager.getConnection(url);
        } catch (Exception e) {
            logger.warning(e);
            connection = null;
        }
    }

    public boolean canUse() {
        try {
            ResultSet resultSet = getPreparedStatement("SELECT database()").executeQuery();
            if (!resultSet.next()) return false;
            return (name.equalsIgnoreCase(resultSet.getString("database()")));
        } catch (SQLException e) {
            logger.warning(e);
            return false;
        }
    }

    public Table getTable(String name, Columns columns, boolean migrate) {
        for (Table table : tableSet) {
            if (table.getName().equals(name) && table.getDatabase().equals(this)) return table;
        }
        var table = new Table(logger, this, name, columns, migrate);
        tableSet.add(table);
        return table;
    }


    public void deleteDB() {
        logger.info("delete DB \"" + name + "\"");
        try {
            getPreparedStatement("DROP DATABASE IF EXIST " + name).executeUpdate();
            close();
        } catch (SQLException e) {
            logger.warning(e);
        }
    }


    public boolean equalsURL(String url) {
        return this.url.equals(url);
    }
}
