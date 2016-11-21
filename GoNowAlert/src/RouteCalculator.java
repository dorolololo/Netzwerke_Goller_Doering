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
        String arrivalTimeString = args[0];
        long arrivalTime = actualTime + 60 * 3;

        //final int timeInt = Integer.parseInt(time);

        // get input

        String inputString = "";
        String[] destinations = new String[3];


        for (int i = 1; i < args.length; i++) {
            destinations[i - 1] = args[i];
        }


        // create URL
        UrlCreator urlCreator = new UrlCreator();
        String generatedUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=" + home + "&destination=";
        generatedUrl += urlCreator.convert(args[1]);
        if(args.length > 2){
            generatedUrl += "&waypoints=optimize:true";
            for( int i = 2; i < args.length; i++){
                generatedUrl +=  "|" + urlCreator.convert(args[i]);
            }
        }


        System.out.println(generatedUrl);

        //String urlstring = "https://maps.googleapis.com/maps/api/directions/json?origin=Lothstraße+34,München&destination=Zieblandstraße,München&waypoints=optimize:true|Herzogstraße+34,München|stachus,München";


        try (
                InputStream in = new URL(generatedUrl).openStream();
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

            // todo Antword des Servers auf korrektheit prüfen - wenn Eingabe nicht stimmt, dann an Nutzer weiterleiten

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
