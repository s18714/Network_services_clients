/**
 * @author Kryzhanivskyi Denys S18714
 */

package zad1;

public class Main {

    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();

        System.out.println(weatherJson);
        System.out.println(rate1);
        System.out.println(rate2);

        ServiceGUI serviceGUI = new ServiceGUI();
    }
}