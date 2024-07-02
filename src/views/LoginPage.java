package views;

import com.formdev.flatlaf.FlatClientProperties;
import controllers.AuthController;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import models.Admin;
import net.miginfocom.swing.MigLayout;
import ui.menu.FormManager;
import utils.ApiClient;
import raven.alerts.MessageAlerts;
import ui.components.LoadingButton;
import utils.TimeOut;

public class LoginPage extends JPanel {

    public LoginPage() {
        init();
    }

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private LoadingButton cmdLogin;

    private void init() {
        setLayout(new MigLayout("fill,insets 20", "[center]", "[center]"));
        txtEmail = new JTextField();
        txtPassword = new JPasswordField();
        cmdLogin = new LoadingButton("Login");

        JPanel panel = new JPanel(new MigLayout("wrap,fillx,insets 35 45 30 45",
            "fill,250:280"));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
            + "arc:20;"
            + "[light]background:darken(@background,3%);"
            + "[dark]background:lighten(@background,3%)");

        txtPassword.putClientProperty(FlatClientProperties.STYLE, ""
            + "showRevealButton:true");

        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            "Ingresa tu email");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            "Ingresa tu contraseña");

        JLabel lbTitle = new JLabel("KIOGA");
        JLabel description = new JLabel("Inicia sesión para acceder a tu cuenta");
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:bold +10");
        description.putClientProperty(FlatClientProperties.STYLE, ""
            + "[light]foreground:lighten(@foreground,30%);"
            + "[dark]foreground:darken(@foreground,30%)");

        panel.add(lbTitle);
        panel.add(description);
        panel.add(new JLabel("Email"), "gapy 8");
        panel.add(txtEmail);
        panel.add(new JLabel("Contraseña"), "gapy 8");
        panel.add(txtPassword);
        panel.add(cmdLogin, "gapy 10");
        add(panel);
//cmdLogin.startLoading();
        // event
        cmdLogin.addActionListener((e) -> {
            String email = txtEmail.getText().trim();
            String password = String.valueOf(txtPassword.getPassword()).trim();

            // Deshabilitar el botón y mostrar el spinner
            cmdLogin.startLoading();

            AuthController.login(
                email,
                password,
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse response) {
                    TimeOut.set(() -> {
                        MessageAlerts.getInstance().showMessage(
                            "Excelente",
                            response.getMessage(),
                            MessageAlerts.MessageType.SUCCESS
                        );
                    }, 750);
                    cmdLogin.stopLoading();
                    FormManager.login((Admin) response.getData());
                }

                @Override
                public void onError(ApiClient.ApiResponse response) {
                    MessageAlerts.getInstance().showMessage(
                        "Error",
                        response.getMessage(),
                        MessageAlerts.MessageType.ERROR
                    );
                    cmdLogin.stopLoading();
                    System.out.println(response.getMessage());
                }
            }
            );
        });
    }
}
