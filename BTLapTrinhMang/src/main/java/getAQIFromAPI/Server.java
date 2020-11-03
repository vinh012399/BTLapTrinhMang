package getAQIFromAPI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("ALL")
public class Server {
    private DatagramSocket socket;
    private int size = 2048;
    private int port = 12399;
    private DatagramPacket dpreceive, dpsend;
    private String in,out;
    private String key = "e15ee2fa-1385-4839-83a3-e60cff2ae183";
    private String url = "https://api.airvisual.com/v2/";
    private String country, state, city;
    private ArrayList<String> content ;

    public Server() {
    }

    private void closeServer() {
        System.out.println("Server socket closed");
        socket.close( );
    }

    //get all countries in database of api
    private ArrayList<String> getAllCountries() throws ParseException {
        ArrayList<String> countries = new ArrayList<String>( );
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36";
        try {
            Document doc = Jsoup.connect(url + "countries?key=" + key)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .get( );
            String allcountries = doc.text( );

            // parsing file "JSONExample.json"
            Object obj = new JSONParser( ).parse(allcountries);

            // typecasting obj to JSONObject
            JSONObject jo = (JSONObject) obj;

            JSONArray ja = (JSONArray) jo.get("data");
            Iterator i = ja.iterator( );
            while (i.hasNext( )) {
                JSONObject slide = (JSONObject) i.next();
                String temp = (String) slide.get("country");
                countries.add(temp);
            }

        } catch (HttpStatusException e){
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace( );
        }

        return countries;
    }

    //get all states in $country
    private ArrayList<String> getStates(String country){

        ArrayList<String> states = new ArrayList<String>(  );

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36";

        try {
            Document doc = Jsoup.connect(url + "states?country="+country+"&key=" + key)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .get( );

            String allstates = doc.text().toString();
            Object obj = new JSONParser().parse(allstates);

            JSONObject jo = (JSONObject) obj;
            String status = (String) jo.get("status");
            if(status.equals("success")) {
                JSONArray ja = (JSONArray) jo.get("data");
                Iterator i = ja.iterator( );
                while (i.hasNext( )) {
                    JSONObject slide = (JSONObject) i.next( );
                    String temp = (String) slide.get("state");
                    states.add(temp);
                }
            }
            else{
                JSONObject data = (JSONObject) jo.get("data");
                String message = (String) data.get("message");
                states.add("\n"+status+"\n"+message+"\nBạn đã nhập sai tên Country\nNhập 'Hello' để xem tất cả các country");
            }

        } catch (HttpStatusException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace( );
        } catch (ParseException e) {
            e.printStackTrace( );
        }
        return states;
    }

    //get all cities of $state in $country
    private ArrayList<String> getCities(String country, String state) {

        ArrayList<String> cities = new ArrayList<String>( );

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36";

        try {
            Document doc = Jsoup.connect(url + "cities?state="+state+"&country=" + country + "&key=" + key)
                    .ignoreContentType(true)
                    .ignoreHttpErrors(true)
                    .get();

            String allcities = doc.text( );
            Object obj = new JSONParser( ).parse(allcities);

            JSONObject jo = (JSONObject) obj;

            String status = (String) jo.get("status");
            if(status.equals("success")) {
                JSONArray ja = (JSONArray) jo.get("data");
                Iterator i = ja.iterator( );
                while (i.hasNext( )) {
                    JSONObject slide = (JSONObject) i.next( );
                    String temp = (String) slide.get("city");
                    cities.add(temp);
                }
            }
            else{
                JSONObject data = (JSONObject) jo.get("data");
                String message = (String) data.get("message");
                cities.add(status+"\n"+message+"\nBạn đã nhập sai hoặc bạn phải nhập đúng cú pháp Country;State");
            }

        }catch (HttpStatusException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace( );
        } catch (IOException e) {
            e.printStackTrace( );
        }
        return cities;
    }

    private String AQI(String country, String state, String city){

        String result="";

        String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36";

        try{
            Document doc = Jsoup.connect(url+"city?city=" +city+ "&state=" +state+"&country="+country+"&key="+key).ignoreContentType(true).ignoreHttpErrors(true).get();
            Object obj = new JSONParser().parse(doc.text());

            JSONObject jo = (JSONObject) obj;

            String status = (String) jo.get("status");
            if(status.equals("success")) {
                JSONObject data = (JSONObject) jo.get("data");
                JSONObject current = (JSONObject) data.get("current");
                JSONObject pollution = (JSONObject) current.get("pollution");
                result += "Chỉ số không khí AQI: " + String.valueOf(pollution.get("aqius"));
            }
            else{
                JSONObject data = (JSONObject) jo.get("data") ;
                String message = (String) data.get("message");
                result+= status+"\n"+message+"\n Bạn phải nhập đúng cú pháp Country;State;City";
            }

        } catch (HttpStatusException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace( );
        } catch (ParseException e) {
            e.printStackTrace( );
        }
        return result;
    }

    private int check(String line){
        if(line.contains(";")){
            String [] b = line.split(";");
            return b.length;
        }
        return 0;
    }

    private String[] splitString(String line){
        String[] arr = line.split(";");
        return arr;
    }


    public void execute(){
        content = new ArrayList<String>();
        try {
            socket = new DatagramSocket(port);
            dpreceive = new DatagramPacket(new byte[size],size);
            while(true){
                out = "";
                socket.receive(dpreceive);
                in = new String(dpreceive.getData(), 0, dpreceive.getLength());
                int c;
                String aqi;
                if (in.toLowerCase().equals("bye")){
                    out = "Close socket !";
                    dpsend = new DatagramPacket(out.getBytes()
                            ,out.getBytes().length
                            ,dpreceive.getAddress()
                            ,dpreceive.getPort());
                    socket.send(dpsend);
                    closeServer();
                    break;
                }else
                    c = check(in);
                switch (c){
                    case 0:
                        if(in.toLowerCase().equals("hello")){
                            content = getAllCountries();
                            for (String t : content) {
                                out += t + ", ";
                            }
                            out=out.trim();
                            out=out.substring(0,out.length()-1);
                            dpsend = new DatagramPacket(out.getBytes()
                                    ,out.getBytes().length
                                    ,dpreceive.getAddress()
                                    ,dpreceive.getPort());
                            socket.send(dpsend);
                            break;
                        }
                        else {
                            content = getStates(in);
                            for(String t : content){
                                out += t + ", ";
                            }
                            out=out.trim();
                            out=out.substring(0,out.length()-1);
                            dpsend = new DatagramPacket(out.getBytes()
                                    ,out.getBytes().length
                                    ,dpreceive.getAddress()
                                    , dpreceive.getPort());
                            socket.send(dpsend);
                            break;
                        }

                    case 2:
                        String[] temptarr = splitString(in);
                        country = temptarr[0];
                        state = temptarr[1];
                        content = getCities(country,state);
                        for(String t : content){
                            out += t + "\n";
                        }
                        dpsend = new DatagramPacket(out.getBytes()
                                ,out.getBytes().length
                                ,dpreceive.getAddress()
                                , dpreceive.getPort());
                        socket.send(dpsend);
                        break;

                    case 3:
                        String[] temparr = splitString(in);
                        country = temparr[0];
                        state = temparr[1];
                        city = temparr[2];
                        aqi = AQI(country,state,city);
                        dpsend = new DatagramPacket(aqi.getBytes()
                                ,aqi.getBytes().length
                                ,dpreceive.getAddress()
                                , dpreceive.getPort());
                        socket.send(dpsend);
                        break;
                }
            }

//            closeServer();
        } catch (SocketException e) {
            e.printStackTrace( );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
