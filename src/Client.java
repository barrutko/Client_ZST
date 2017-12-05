import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class Client
{
    public static void main(String[] args) throws IOException
    {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String userDecision;
        boolean programEnds = false;
        Random random = new Random();
        int clientID = 0;
        String messageType = "EMPTY";
        String eventType = "EMPTY";
        String messageBody = "EMPTY";
        int statusCode;
        CloseableHttpClient httpclient;
        HttpPost httpPost;
        HttpEntity stringEntity;
        CloseableHttpResponse response = null;
        Client client = new Client();
        String uri = "http://" + args[0] + ":14880";

        while(clientID < 100000)
        clientID = random.nextInt(999998) + 1;

        if (args.length>1)
        {
            try {
                if(Integer.parseInt(args[1])>=100000 && Integer.parseInt(args[1])<=999999)
                clientID = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {

            }
        }


        System.out.println(client.getCurrentTime());
        System.out.println("Welcome to ZST_Client! Your ID is " + clientID + " What are we doing today?");

        while (true)
        {
            httpclient = HttpClients.createDefault();
            httpPost = new HttpPost("http://localhost:14880");
            userDecision = "0";
            System.out.println("1. Send event log\n2. Create new event definition\n3. Show existing event definitions\n4. Exit");
            while (!userDecision.equals("1") && !userDecision.equals("2") && !userDecision.equals("3") && !userDecision.equals("4"))
            {
                System.out.println("Enter 1, 2, 3 or 4.");
                try {
                    userDecision = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            switch (userDecision)
            {
                case "1":

                    messageType = "EventLog";
                    httpPost.addHeader("X-Message-Type",messageType);
                    httpPost.addHeader("X-Client-ID", clientID + "");
                    httpPost.addHeader("Date", client.getCurrentTime());

                    System.out.println("Enter event type");
                    try {
                        eventType = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    httpPost.addHeader("X-Event-Type",eventType);

                    System.out.println("Enter proper data (separate each parameter with space)");
                    try {
                        messageBody = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stringEntity = new StringEntity(messageBody);
                    httpPost.setEntity(stringEntity);
                    try {
                    response = httpclient.execute(httpPost);

                    statusCode = response.getStatusLine().getStatusCode();
                    if(statusCode == 200)
                    {
                        System.out.println("\nNew event log was sent correctly (" + httpPost.getFirstHeader("Date") +")\n");
                    }
                    else
                    {
                        if (statusCode == 481)
                            System.out.println("\nAn error occurred. Event type is undefined.\n");
                        else if (statusCode == 482)
                            System.out.println("\nAn error occurred. Number of parameters does not match event definition\n");
                        else
                            System.out.println("\nAn error occurred. Reason unknown\n");

                    }
            }
                    catch (Exception e)
            {
                System.out.println("\nAn error occurred. Reason unknown. Probably server unreachable.\n");
            }
                    break;

                case "2":

                    messageType = "NewDefinition";
                    httpPost.addHeader("X-Message-Type",messageType);
                    httpPost.addHeader("X-Client-ID", clientID + "");
                    httpPost.addHeader("Date", client.getCurrentTime());

                    System.out.println("Enter event type");
                    try {
                        eventType = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    httpPost.addHeader("X-Event-Type",eventType);

                    System.out.println("Enter event label names (separate each parameter with space)");
                    try {
                        messageBody = bufferedReader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stringEntity = new StringEntity(messageBody);
                    httpPost.setEntity(stringEntity);
                    try {
                    response = httpclient.execute(httpPost);

                    statusCode = response.getStatusLine().getStatusCode();
                    if(statusCode == 200)
                        System.out.println("\nNew definition was send correctly (" + httpPost.getFirstHeader("Date") +")\n");
                    else if (statusCode == 400)
                        System.out.println("\nAn error occurred. Event already exists.\n");
                    else
                        System.out.println("\nAn error occurred. Reason unknown\n");
            }
                    catch (Exception e)
            {
                System.out.println("\nAn error occurred. Reason unknown. Probably server unreachable.\n");
            }
                    break;

                case "3":
                    messageType = "DefinitionInfo";
                    httpPost.addHeader("X-Message-Type",messageType);

                    try {
                        response = httpclient.execute(httpPost);



                    if (response.getStatusLine().getStatusCode()==200)
                        System.out.println("\nAlready existing definitions:\n" + EntityUtils.toString(response.getEntity()));
                    else if (response.getStatusLine().getStatusCode()==400)
                        System.out.println("\nAn error occurred. No definitions exist.\n");
                    else
                        System.out.println("\nAn error occurred. Reason unknown\n");
            }
                    catch (Exception e)
            {
                System.out.println("\nAn error occurred. Reason unknown. Probably server unreachable.\n");
            }

                    break;

                case "4":
                    programEnds = true;
                    break;

            }
            if (programEnds)
                break;
        }
        System.out.println("\nThank You for this session. Your ID was " + clientID + ". See You next time!\n");
    }

    String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        return dateFormat.format(calendar.getTime());
    }
}
