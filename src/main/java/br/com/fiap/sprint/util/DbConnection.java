package br.com.fiap.sprint.util;

import javax.management.RuntimeErrorException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class DbConnection {
    public static Connection getConnection() {

        Properties props = new Properties();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("Arquivo db.properties não encontrado em resources");
            }
            props.load(in);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Falha ao ler db.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");
        String driver = props.getProperty("db.driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC não encontrado: " + driver, e);
        }

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar no banco: " + url, e);
        }

    }

    public static boolean ping() {

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM dual");
             ResultSet rs = ps.executeQuery()) {

            return rs.next();
        }catch (Exception e) {
            System.err.println("Ping falhou: " + e.getMessage());
            return false;
        }
    }

}
