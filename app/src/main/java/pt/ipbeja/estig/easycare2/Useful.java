package pt.ipbeja.estig.easycare2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * The type Useful.
 */
public class Useful {

    /**
     * Instantiates a new Useful.
     */
    public Useful() {
    }


    /******
     * receives address and context and returns latitude and longitude
     * @param context
     * @param address
     * @return
     * @throws IOException
     */
    private static LatLng getLatLon(Context context, String address) throws IOException {
        double latitude = 0;
        double longitude = 0;
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses;
        addresses = geocoder.getFromLocationName(address, 1);
        if (addresses.size() > 0) {
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();
        }

        return new LatLng(latitude, longitude);
    }

    /**
     * Convert address to lat lng lat lng.
     *
     * @param context the context
     * @param address the address
     * @return the lat lng
     */
    public static LatLng convertAddressToLatLng(Context context, String address) {

        LatLng location = null;
        if (isNullOrEmpty(address)) {
            try {
                location = getLatLon(context, address);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return location;
    }

    /**
     * Is null or empty boolean.
     * - Check if String with spaces is Empty or Null
     *
     * @param strValue the str value
     * @return the boolean
     */
    public static boolean isNullOrEmpty(String strValue) {
        return (TextUtils.isEmpty(strValue)) || strValue.trim().isEmpty();
    }

    /**
     * Convert string of int int.
     *
     * @param text the text
     * @return the int
     */
    public static int convertStringOfInt(String text) {
        return Integer.parseInt(text.trim().replaceAll("\\s", ""));
    }

    /**
     * check field is filled boolean and change focus
     *
     * @param editText the edit text
     * @param str      the str
     * @return true if for null or empty the boolean t
     */
    public static boolean validateFieldIsFilled(EditText editText, String str) {
        String text = editText.getText().toString();

        if (Useful.isNullOrEmpty(text)) {
            editText.requestFocus();
            editText.setError(str);
            return true;
        }
        return false;
    }

    /**
     * check field is filled boolean and change focus
     *
     * @param button      the button
     * @param textView    the text view
     * @param messageErro the message erro
     * @return true if for null or empty the boolean t
     */
    public static boolean validateFieldIsFilledTextView(Button button, TextView textView, String messageErro) {
        String text = textView.getText().toString();

        if (Useful.isNullOrEmpty(text)) {
            button.requestFocus();
            textView.setError(messageErro);
            return true;
        }
        return false;
    }

    /**
     * Gets the user information from file.
     *
     * @param fileDir Files directory
     * @return JSON object
     */
    public static JSONObject getUserInfoFromFile(File fileDir) {
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject object = new JSONObject();
        try {
            File filesDir = new File(fileDir, "userDir/userInfoFile");
            Scanner scanner = new Scanner(filesDir);
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine());
            }
            Log.d("####################", stringBuilder.toString());
            object = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }


    /**
     * Gets current date.
     *
     * @return the current date
     */
    public static long getCurrentDate() {

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        return Useful.createLongDate(today.year, today.month, today.monthDay
                , today.hour, today.minute);
    }

    @SuppressLint("SimpleDateFormat")
    // Definimos o formato da String para a data (https://developer.android.com/reference/java/text/SimpleDateFormat)
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Transforma um objecto Date numa String "dd/MM/yyyy HH:mm"
     *
     * @param date A data
     * @return Uma String legível pelo utilizador com a data e hora
     */
    public static String formatDate(Date date) {
        return formatter.format(date);
    }

    /**
     * Transforma um long numa String "dd/MM/yyyy HH:mm"
     *
     * @param date A data
     * @return Uma String legível pelo utilizador com a data e hora
     */
    public static String formatDate(long date) {
        return formatDate(new Date(date));
    }


    /*******
     *
     * Transforma um int numa String "MMM"
     *
     * @param numMonth Uma String legível pelo utilizador com a data e hora
     */
    public static void formatMonth(int numMonth) {
       // return  new SimpleDateFormat(numMonth);

    }
    /**
     * Cria uma Date a partir de um ano, mês e dia do mês
     *
     * @param year       Ano
     * @param month      Mês (Os meses começam em zero. 0 -> Janeiro, 1 -> Fevereiro, etc.)
     * @param dayOfMonth Dia do mês
     * @return Uma Date
     */
    public static Date createDate(int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return c.getTime();
    }

    /**
     * Cria uma representação em long de uma data a partir de um ano, mês e dia do mês
     *
     * @param year       Ano
     * @param month      Mês (Os meses começam em zero. 0 -> Janeiro, 1 -> Fevereiro, etc.)
     * @param dayOfMonth Dia do mês
     * @return Um long da data
     */
    public static long createLong(int year, int month, int dayOfMonth) {
        Date date = createDate(year, month, dayOfMonth);
        return date.getTime();
    }

    /**
     * Cria uma Date a partir de um ano, mês e dia do mês
     *
     * @param year       Ano
     * @param month      Mês (Os meses começam em zero. 0 -> Janeiro, 1 -> Fevereiro, etc.)
     * @param dayOfMonth Dia do mês
     * @param hour       the hour
     * @param min        the min
     * @return Uma Date
     */
    public static Date createDateTime(int year, int month, int dayOfMonth, int hour, int min) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.HOUR, hour);
        c.set(Calendar.MINUTE, min);
        return c.getTime();
    }

    /**
     * Create long date long.
     *
     * @param year       the year
     * @param month      the month
     * @param dayOfMonth the day of month
     * @param hour       the hour
     * @param min        the min
     * @return the long
     */
    public static long createLongDate(int year, int month, int dayOfMonth, int hour, int min) {
        Date date = createDateTime(year, month, dayOfMonth, hour, min);
        return date.getTime();
    }

    /**
     * Validates the postal code.
     *
     * @param editTextPostalCode the edit text postal code
     * @return boolean boolean
     */
    public static boolean validateCodPostal(EditText editTextPostalCode) {

        String text = editTextPostalCode.getText().toString().trim();

        if (text.length() > 8) {
            editTextPostalCode.setError("zip code too long");
            return true;
        }
        return false;
    }

    /**
     * Writes the user information to file.
     * @param context          Application context
     * @param userInfoFromFile JSON object
     */
    public static void writeUserTofile(Context context, JSONObject userInfoFromFile) {
        File filesDir = new File(context.getFilesDir(), "userDir");
        if (!filesDir.exists()) {
            filesDir.mkdir();
        }
        try {
            File file = new File(filesDir, "userInfoFile");
            FileWriter writer = new FileWriter(file);
            String user = userInfoFromFile.toString();
            Log.d("##### user:", user);
            writer.write(user);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
