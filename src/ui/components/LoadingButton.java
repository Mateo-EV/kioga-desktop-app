package ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadingButton extends ActionButton  {
    private LoadingSpinner loadingSpinner;
    private JPanel contentPanel;
    private JLabel textLabel;
    
    public LoadingButton(String text) {
        super();
        init(text);
    }
    
    public LoadingButton(String text, int type) {
        super(type);
        init(text);
    }
    
    private void init(String text) {
        textLabel = new JLabel(text);
        textLabel.putClientProperty(FlatClientProperties.STYLE, "font:$semibold.font;");
        contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        contentPanel.setOpaque(false);

        loadingSpinner = new LoadingSpinner();
        loadingSpinner.setVisible(false);

        contentPanel.add(loadingSpinner);
        contentPanel.add(textLabel);
        setLayout(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);
    }

    public void startLoading() {
        loadingSpinner.setVisible(true);
        loadingSpinner.start();
        setEnabled(false);
    }

    public void stopLoading() {
        loadingSpinner.setVisible(false);
        loadingSpinner.stop();
        setEnabled(true);
    }
}
