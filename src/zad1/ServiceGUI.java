package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceGUI extends JFrame {

    private JFXPanel jfxPanel = new JFXPanel();

    public ServiceGUI() throws HeadlessException {
        List<String> values = receiveValues();
        Service service = new Service(values.get(0));
        JPanel mainPanel = new JPanel(new BorderLayout());
        jfxPanel.setLayout(new BorderLayout());
        JLabel label1 = new JLabel(String.valueOf(service.getReadableWeather(values.get(1)))),
                label2 = new JLabel(String.valueOf(service.getRateFor(values.get(2))), JLabel.LEFT),
                label3 = new JLabel(String.valueOf(service.getNBPRate()), JLabel.RIGHT);

        mainPanel.add(label1, BorderLayout.NORTH);
        mainPanel.add(label2, BorderLayout.SOUTH);
        mainPanel.add(label3, BorderLayout.SOUTH);
        mainPanel.add(jfxPanel, BorderLayout.CENTER);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width - 300, 768);
        this.setLocationRelativeTo(null);
        this.setTitle("Service");
        this.setResizable(false);
        this.setVisible(true);
        this.add(mainPanel);

        loadWiki("https://en.wikipedia.org/wiki/" + values.get(1));
    }

    private List<String> receiveValues() {
        List<String> values = new ArrayList<>();
        JPanel panel = new JPanel(new BorderLayout());

        JPanel labels = new JPanel(new GridLayout(0, 1));
        labels.add(new JLabel("Country", JLabel.RIGHT));
        labels.add(new JLabel("City", JLabel.RIGHT));
        labels.add(new JLabel("Currency", JLabel.RIGHT));
        panel.add(labels, BorderLayout.WEST);

        JPanel textFields = new JPanel(new GridLayout(0, 1));
        JTextField country = new JTextField();
        JTextField city = new JTextField();
        JTextField currency = new JTextField();
        textFields.add(country);
        textFields.add(city);
        textFields.add(currency);
        panel.add(textFields, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Enter values:", JOptionPane.INFORMATION_MESSAGE);
        values.add(country.getText());
        values.add(city.getText());
        values.add(currency.getText());

        return values;
    }

    private void loadWiki(String url) {
        Platform.runLater(() -> {
            WebView webView = new WebView();
            WebEngine engine = webView.getEngine();
            Scene scene = new Scene(webView);
            jfxPanel.setScene(scene);
            engine.load(url);
        });
    }
}