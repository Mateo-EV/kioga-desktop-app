package ui.menu;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import controllers.AuthController;
import java.awt.Color;
import java.awt.Component;
import java.net.URL;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import models.Admin;
import raven.drawer.component.DrawerPanel;
import raven.drawer.component.SimpleDrawerBuilder;
import raven.drawer.component.footer.SimpleFooterData;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.header.SimpleHeaderStyle;
import raven.drawer.component.menu.MenuAction;
import raven.drawer.component.menu.MenuEvent;
import raven.drawer.component.menu.MenuValidation;
import raven.drawer.component.menu.SimpleMenuOption;
import raven.drawer.component.menu.SimpleMenuStyle;
import raven.drawer.component.menu.data.Item;
import raven.drawer.component.menu.data.MenuItem;
import raven.swing.AvatarIcon;
import views.AdminPage;
import views.BrandPage;
import views.CategoryPage;
import views.CustomerPage;
import views.DashboardPage;
import views.OrderPage;
import views.ProductPage;

public class DrawerBuilder extends SimpleDrawerBuilder {

    private Admin admin;
    private final ThemesChange themesChange;

    public void setAdmin(Admin admin) {
        this.admin = admin;
        SimpleHeaderData headerData = header.getSimpleHeaderData();
        headerData.setTitle(admin.getName());
        headerData.setDescription(admin.getEmail());
        try {
            URL url_image = new URL(admin.getImage());
            FlatSVGIcon svg = new FlatSVGIcon(url_image);
            AvatarIcon icon = new AvatarIcon(svg, 60, 60, 999);
            icon.setBorder(2);
            headerData.setIcon(icon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        header.setSimpleHeaderData(headerData);
        rebuildMenu();
    }

    public DrawerBuilder() {
        themesChange = new ThemesChange();
    }

    @Override
    public Component getFooter() {
        return themesChange;
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        AvatarIcon icon = new AvatarIcon(getClass().getResource(
            "/resources/image/profile.png"), 60, 60, 999);
        icon.setBorder(2);
        return new SimpleHeaderData()
            .setIcon(icon)
            .setTitle("said")
            .setDescription("mateo123@gmail.com")
            .setHeaderStyle(new SimpleHeaderStyle() {

                @Override
                public void styleTitle(JLabel label) {
                    label.putClientProperty(FlatClientProperties.STYLE, ""
                        + "[light]foreground:#FAFAFA");
                }

                @Override
                public void styleDescription(JLabel label) {
                    label.putClientProperty(FlatClientProperties.STYLE, ""
                        + "[light]foreground:#E1E1E1");
                }
            });
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData();
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {

        MenuItem items[] = new MenuItem[]{
            new Item.Label("PRINCIPAL"),
            new Item("Dashboard", "dashboard.svg"),
            new Item.Label("GESTIÓN"),
            new Item("Pedidos", "shopping-cart.svg"),
            new Item("Productos", "package.svg"),
            new Item("Categorías", "rows-3.svg"),
            new Item("Marcas", "square-menu.svg"),
            new Item("Clientes", "users.svg"),
            new Item.Label("OTROS"),
            new Item("Administradores", "lock.svg"),
            new Item("Logout", "logout.svg")
        };

        SimpleMenuOption simpleMenuOption = new SimpleMenuOption() {
            @Override
            public Icon buildMenuIcon(String path, float scale) {
                FlatSVGIcon icon = new FlatSVGIcon(path, scale);
                FlatSVGIcon.ColorFilter colorFilter = new FlatSVGIcon.ColorFilter();
                colorFilter.add(Color.decode("#969696"), Color.decode("#FAFAFA"),
                    Color.decode("#969696"));
                icon.setColorFilter(colorFilter);
                return icon;
            }
        };

        simpleMenuOption.setMenuValidation(new MenuValidation() {

            private boolean checkMenu(int[] index, int[] indexHide) {
                if (index.length == indexHide.length) {
                    for (int i = 0; i < index.length; i++) {
                        if (index[i] != indexHide[i]) {
                            return true;
                        }
                    }
                    return false;
                }
                return true;
            }

            @Override
            public boolean menuValidation(int[] index) {
                if (admin == null) {
                    return false;
                }
                if (true) {
                    // non user admin going to hide
                    boolean act
                        // `Email`->`Gropu Read`->`Read 3`
                        = checkMenu(index, new int[]{1, 2, 3})
                        // `Email`->`Gropu Read`->`Read 5`
                        && checkMenu(index, new int[]{1, 2, 5})
                        // `Email`->`Group Read`->`Group Item->`Item 4`
                        && checkMenu(index, new int[]{1, 2, 2, 3})
                        // `Advanced UI`->`Owl Carousel`
                        && checkMenu(index, new int[]{4, 1})
                        // `Special Pages`
                        && checkMenu(index, new int[]{8});
                    return act;
                }
                return true;
            }
        });

        simpleMenuOption.setMenuStyle(new SimpleMenuStyle() {
            @Override
            public void styleMenuItem(JButton menu, int[] index) {
                menu.putClientProperty(FlatClientProperties.STYLE, ""
                    + "[light]foreground:#FAFAFA;"
                    + "arc:10");
            }

            @Override
            public void styleMenu(JComponent component) {
                component.putClientProperty(FlatClientProperties.STYLE, ""
                    + "background:$Drawer.background");
            }

            @Override
            public void styleLabel(JLabel label) {
                label.putClientProperty(FlatClientProperties.STYLE, ""
                    + "[light]foreground:darken(#FAFAFA,15%);"
                    + "[dark]foreground:darken($Label.foreground,30%)");
            }
        });
        simpleMenuOption.addMenuEvent(new MenuEvent() {
            @Override
            public void selected(MenuAction action, int[] index) {
                if (index.length == 1) {
                    switch (index[0]) {
                        case 0:
                            FormManager.showForm(new DashboardPage(true));
                            break;
                        case 1:
                            FormManager.showForm(new OrderPage());
                            break;
                        case 2:
                            FormManager.showForm(new ProductPage());
                            break;
                        case 3:
                            FormManager.showForm(new CategoryPage());
                            break;
                        case 4:
                            FormManager.showForm(new BrandPage());
                            break;
                        case 5:
                            FormManager.showForm(new CustomerPage());
                            break;
                        case 6:
                            FormManager.showForm(new AdminPage());
                            break;
                        case 7:
                            AuthController.logout();
                            FormManager.logout();
                            break;
                    }
                }
            }
        });

        simpleMenuOption.setMenus(items)
            .setBaseIconPath("resources/menu")
            .setIconScale(0.45f);
        return simpleMenuOption;
    }

    @Override

    public void build(DrawerPanel drawerPanel) {
        drawerPanel.putClientProperty(FlatClientProperties.STYLE, ""
            + "background:$Drawer.background");
    }

    @Override
    public int getDrawerWidth() {
        return 270;
    }
}
