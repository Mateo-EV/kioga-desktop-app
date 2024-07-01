/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author intel
 */
public class ActionButton extends JButton {

    public static final int PRIMARY = 0;
    public static final int SECONDARY = 1;
    public static final int DESTRUCTIVE = 3;

    public ActionButton(String text) {
        super(text);
        init(PRIMARY);
    }

    public ActionButton(Icon icon) {
        super(icon);
        init(PRIMARY);
    }

    public ActionButton(Icon icon, int type) {
        super(icon);
        init(type);
    }

    public ActionButton(String text, int type) {
        super(text);
        init(type);
    }

    public ActionButton() {
        super();
        init(PRIMARY);
    }

    public ActionButton(int type) {
        super();
        init(type);
    }

    private void init(int type) {
        String bgColor;
        String disabledBgColor;
        String border = "0";
        String extra = "";

        switch (type) {
            case PRIMARY:
                bgColor = "background:@accentColor;";
                disabledBgColor = "disabledBackground:@accentColor;";
                extra = "[light]foreground:$Panel.background";
                break;
            case SECONDARY:
                bgColor = "[dark]background:lighten(@background,10%);"
                    + "[light]background:darken(@background,10%);";
                disabledBgColor = "[dark]disabledBackground:lighten(@background,10%);"
                    + "[light]disabledBackground:darken(@background,10%);";
                break;
            case DESTRUCTIVE:
                bgColor = "background:#DC2D2D;";
                disabledBgColor = "disabledBackground:#DC2D2D;";
                extra = "[light]foreground:$Panel.background";
                break;
            default:
                bgColor = "background:@accentColor;";
                disabledBgColor = "disabledBackground:@accentColor;";
        }

        putClientProperty(FlatClientProperties.STYLE,
            bgColor
            + disabledBgColor
            + "borderWidth:" + border + ";"
            + "focusWidth:0;"
            + "innerFocusWidth:0;"
            + "font:$semibold.font;"
            + extra
        );
    }
}
