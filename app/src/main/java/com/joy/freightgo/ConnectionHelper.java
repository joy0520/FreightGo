package com.joy.freightgo;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Joy on 2016/9/7.
 */
public class ConnectionHelper {
    private static String ReflectionClassName = "net.sourceforge.jtds.jdbc.Driver";

    private static String ip = "60.249.197.60";
    private static String db = "SBIR";
    private static String userName = "SBIR";
    private static String password = "qwer2016@1";
    public static String dbSheetName = "car";

    @SuppressLint("NewApi")
    public static Connection connect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName(ReflectionClassName);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ";"
                    + "databaseName=" + db + ";user=" + userName + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
