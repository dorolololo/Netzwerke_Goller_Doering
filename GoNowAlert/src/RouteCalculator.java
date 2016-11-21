import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by Dorina on 12.11.2016.
 */
public class RouteCalculator {

    // args =
    public static void main(String... args) {
        final String home = "Lothstraße+64,München";


        // todo zeit einbauen - alles in millis umrechnen!!!

        long actualTime = System.currentTimeMillis();
        long timeToLeave = 5 * 60;
        String arrivalTimeString = "";
        long arrivalTime = actualTime + 60*3;


        // todo Eingabe einlesen..

        //final int timeInt = Integer.parseInt(time);
        // todo String selber bauen

        // get input

        String inputString = "";
        boolean readingTime = true;

        for (int i = 0; i < args.length; i++) {
            String input = args[i].replace(" ","");
            if (readingTime) {
                if (input.contains("|"))
                    readingTime = false;
                else
                    arrivalTimeString += input;
            } else {

                inputString += input + " ";
            }
        }

        // create URL



        String urlstring = "https://maps.googleapis.com/maps/api/directions/json?origin=Lothstraße+34,München&destination=Zieblandstraße,München&waypoints=optimize:true|Herzogstraße+34,München|stachus,München";


        try (
                InputStream in = new URL(urlstring).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        )

        {
            // get json file
            String response = "";
            String line = reader.readLine();

            while (line != null) {
                response += line + "\r\n";
                line = reader.readLine();
            }

            // parse json
            Gson gson = new Gson();
            RoutesSource parsedResponse = gson.fromJson(response, RoutesSource.class);

            // get complete duration in seconds
            int durationInSeconds = 0;
            for (Leg l : parsedResponse.routes[0].legs) {
                durationInSeconds += l.duration.value;
            }

            System.out.println("complete duration = " + durationInSeconds);
            int durationInMinutes = durationInSeconds / 60;
            System.out.println(durationInMinutes);

            // todo hier clienthandler mit threads einbauen ABER die threadklasse muss im try catch block bleiben

            Long timeToGo = actualTime + timeToLeave + (arrivalTime - actualTime) - durationInSeconds;

            // start with hue if it is time!!
            if (System.currentTimeMillis() >= timeToGo) {
                // get available lamps


                // create header


                // swich lamp on
            }

        } catch (
                Exception e
                )

        {
            System.out.println("sth failed"); // todo exception
        }
    }

}
