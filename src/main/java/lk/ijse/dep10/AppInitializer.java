package lk.ijse.dep10;

import javafx.application.Application;
import javafx.stage.Stage;
import lk.ijse.dep10.db.DBConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                if (DBConnection.getInstance().getConnection()!=null &&
                        !DBConnection.getInstance().getConnection().isClosed()) {
                    System.out.println("Database connection is about to close");
                    DBConnection.getInstance().getConnection().close();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {
        generateSchemaIfNotExist();

    }

    private  void generateSchemaIfNotExist() {

            Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES ");

            HashSet<String> tableNameList = new HashSet<>();

            while (rst.next()) {
                tableNameList.add(rst.getString(1));
            }

            // this set.of method is introduced after the java9
            boolean tableExists = tableNameList.containsAll(Set.of("Attendance", "Picture", "Student", "User"));
            System.out.println(tableExists);
            if (!tableExists) {
                stm.execute(readSchemaScrip());
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String readSchemaScrip() {
        InputStream is = getClass().getResourceAsStream("/schema.sql");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            StringBuilder dbStringBuilder = new StringBuilder();

            if ((line = br.readLine()) != null) {
                dbStringBuilder.append(line);
            }
            System.out.println(dbStringBuilder);
            return dbStringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
