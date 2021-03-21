/**
 * @author Kryzhanivskyi Denys S18714
 */

package zad1;

import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service {

    private String countryCurrency;
    private String countryCode;

    public Service(String country) {
        Locale.setDefault(Locale.ENGLISH);
        Map<String, String> map = new HashMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                if(locale.getDisplayCountry().equals(country))
                    countryCode = locale.getCountry().toLowerCase();

                Currency currency = Currency.getInstance(locale);
                map.putIfAbsent(locale.getDisplayCountry(), currency.getCurrencyCode());
            } catch (IllegalArgumentException ignored) {
            }
        }
        this.countryCurrency = map.get(country);
    }

    public String getWeather(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "," + countryCode
                + "&appid=5a4db7d723273927d687ef784a6d55dc";
        return readJsonFromUrl(url);
    }

    public Double getRateFor(String currency) {
        String url = "https://api.exchangeratesapi.io/latest?base=" + currency + "&symbols=" + countryCurrency;
        String json = readJsonFromUrl(url);

        Matcher matcher = Pattern.compile("\\d+\\.\\d+").matcher(json);

        if (matcher.find())
            return Double.parseDouble(matcher.group(0));

        return null;
    }

    public Double getNBPRate() {
        Double rate = countryCurrency.equals("PLN") ? 1.0 : null;
        if (rate != null) return rate;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document1 = builder.parse(new URL("http://www.nbp.pl/kursy/xml/a062z200330.xml").openStream());
            Document document2 = builder.parse(new URL("http://www.nbp.pl/kursy/xml/b012z200325.xml").openStream());

            NodeList nodeList1 = document1.getDocumentElement().getChildNodes();
            NodeList nodeList2 = document2.getDocumentElement().getChildNodes();

            NodeList[] lists = {nodeList1, nodeList2};

            for (NodeList list : lists) {
                for (int i = 0; i < list.getLength(); i++) {
                    if (list.item(i) instanceof Element && ((Element) list.item(i)).getTagName().equals("pozycja")) {
                        Element element = (Element) list.item(i);
                        if (element.getElementsByTagName("kod_waluty").item(0).getTextContent().equals(countryCurrency))
                            rate = new Double(element.getElementsByTagName("kurs_sredni").item(0)
                                    .getTextContent().replace(",", "."));
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return rate;
    }

    public Map<String, List<String>> getReadableWeather(String city) {
        Map<String, List<String>> finalMap = null;
        try {
            finalMap = new LinkedHashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.readValue(getWeather(city), Map.class);

            for (Map.Entry<String, Object> m : map.entrySet()) {
                map.replace(m.getKey(), map.get(m.getKey()).toString().replaceAll("[{\\[}\\]]", " "));

                String[] parts = map.get(m.getKey()).toString().split(",");
                List<String> list = new LinkedList<>(Arrays.asList(parts));
                finalMap.put(m.getKey(), list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return finalMap;
    }

    private String readJsonFromUrl(String urlString) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}