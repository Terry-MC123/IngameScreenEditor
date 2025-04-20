package top.terry_mc.inGame_screen_editor.json;

import java.util.Map;

public class JsonScreen {
    public String name;
    public Map<String, Widget> widgets;
    public static class Widget {
        public String message, type;
        public int x, y, width, height;
    }
}
