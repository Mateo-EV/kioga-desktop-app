package ui.menu;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import controllers.AuthController;
import java.awt.Image;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import models.Admin;
import ui.components.MainForm;
import ui.components.SimpleForm;
import views.LoginPage;
import ui.swing.slider.PanelSlider;
import ui.swing.slider.SimpleTransition;
import utils.UndoRedo;
import views.DashboardPage;

public class FormManager {

    private static FormManager instance;
    private final JFrame frame;

    private final UndoRedo<SimpleForm> forms = new UndoRedo<>();

    private boolean menuShowing = true;
    private final PanelSlider panelSlider;
    private final MainForm mainForm;
    private final Menu menu;
    private final boolean undecorated;

    public static void install(JFrame frame, boolean undecorated) {
        instance = new FormManager(frame, undecorated);
    }

    private FormManager(JFrame frame, boolean undecorated) {
        this.frame = frame;
        panelSlider = new PanelSlider();
        mainForm = new MainForm(undecorated);
        menu = new Menu(new DrawerBuilder());
        this.undecorated = undecorated;
    }

    public static void showMenu() {
        instance.menuShowing = true;
        instance.panelSlider.addSlide(instance.menu, SimpleTransition.getShowMenuTransition(instance.menu.getDrawerBuilder().getDrawerWidth(), instance.undecorated));
    }
    
    public static void init() {
        Admin admin = AuthController.getSession();
        if (admin == null) {
            logout();
        } else {
            login(admin);
        }
        showForm(new DashboardPage());
    }

    public static void showForm(SimpleForm component) {
        if (isNewFormAble()) {
            if(instance.forms.getCurrent() != null && instance.forms.getCurrent().getClass().getSimpleName().equals(component.getClass().getSimpleName())){
                instance.menuShowing = false;
                Image oldImage = instance.panelSlider.createOldImage();
                instance.panelSlider.addSlide(instance.mainForm, SimpleTransition.getSwitchFormTransition(oldImage, instance.menu.getDrawerBuilder().getDrawerWidth()));
                return;
            };
            
            instance.forms.add(component);
            if (instance.menuShowing) {
                instance.menuShowing = false;
                Image oldImage = instance.panelSlider.createOldImage();
                instance.mainForm.setForm(component);
                instance.panelSlider.addSlide(instance.mainForm, SimpleTransition.getSwitchFormTransition(oldImage, instance.menu.getDrawerBuilder().getDrawerWidth()));
            } else {
                instance.mainForm.showForm(component);
            }
            instance.forms.getCurrent().formInitAndOpen();
        }
    }

    public static void logout() {
        FlatAnimatedLafChange.showSnapshot();
        instance.frame.getContentPane().removeAll();
        instance.frame.getContentPane().add(new LoginPage());
        instance.frame.repaint();
        instance.frame.revalidate();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void login(Admin admin) {
        FlatAnimatedLafChange.showSnapshot();
        
        instance.frame.getContentPane().removeAll();
        instance.frame.getContentPane().add(instance.panelSlider);
        // set new user and rebuild menu for user role
        ((DrawerBuilder) instance.menu.getDrawerBuilder()).setAdmin(admin);
        instance.frame.repaint();
        instance.frame.revalidate();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void hideMenu() {
        instance.menuShowing = false;
        instance.panelSlider.addSlide(instance.mainForm, SimpleTransition.getHideMenuTransition(instance.menu.getDrawerBuilder().getDrawerWidth(), instance.undecorated));
    }

    public static void undo() {
        if (isNewFormAble()) {
            if (!instance.menuShowing && instance.forms.isUndoAble()) {
                instance.mainForm.showForm(instance.forms.undo(), SimpleTransition.getDefaultTransition(true));
                instance.forms.getCurrent().formOpen();
            }
        }
    }

    public static void redo() {
        if (isNewFormAble()) {
            if (!instance.menuShowing && instance.forms.isRedoAble()) {
                instance.mainForm.showForm(instance.forms.redo());
                instance.forms.getCurrent().formOpen();
            }
        }
    }

    public static void refresh() {
        if (!instance.menuShowing) {
            instance.forms.getCurrent().formRefresh();
        }
    }

    public static UndoRedo<SimpleForm> getForms() {
        return instance.forms;
    }

    public static boolean isNewFormAble() {
        return instance.forms.getCurrent() == null || instance.forms.getCurrent().formClose();
    }

    public static void updateTempFormUI() {
        for (SimpleForm f : instance.forms) {
            SwingUtilities.updateComponentTreeUI(f);
        }
    }
}
