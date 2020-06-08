package gui.inputs;

import exceptions.InvalidFileException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static gui.inputs.GamepadInputs.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * This class handle the mapping between User Input and the NES
 */
public class InputMapper {

    private static final float DEAD_ZONE_RADIUS = .4f;

    private long window;

    private Map<GamepadInputs, Integer> mappedControlsJoystick;
    private Map<GamepadInputs, Integer> mappedControlsKeyboard;
    private Map<String, Integer> buttonNames;

    /**
     * Create a new InputMapper and load the config file
     *
     * @param window the OpenGL window ID used to get the events from
     */
    public InputMapper(long window) {
        this.window = window;
        buttonNames = new HashMap<>();
        mappedControlsJoystick = new HashMap<>();
        mappedControlsKeyboard = new HashMap<>();
        initButtonNames();
        loadConfig();
    }

    /**
     * Return whether or not an Input is pressed or not
     * account for Keyboard and Gamepads
     *
     * @param input  the input to test
     * @param player the player ID
     * @return is the input pressed by the specified player
     */
    public boolean isPressed(GamepadInputs input, int player) {
        boolean pressed = false;
        //If a controller is connected we test for its inputs
        //otherwise we only test for the keyboard
        if (glfwJoystickPresent(player % 2 == 1 ? GLFW_JOYSTICK_1 : GLFW_JOYSTICK_2)) {
            ByteBuffer buttons = glfwGetJoystickButtons(player % 2 == 1 ? GLFW_JOYSTICK_1 : GLFW_JOYSTICK_2);
            FloatBuffer axes = glfwGetJoystickAxes(player % 2 == 1 ? GLFW_JOYSTICK_1 : GLFW_JOYSTICK_2);
            if (buttons != null && buttons.capacity() > mappedControlsJoystick.get(input)) {
                if (axes != null) {
                    switch (input) {
                        case CONTROLLER_1_UP:
                        case CONTROLLER_2_UP:
                            pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_Y && axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) < -DEAD_ZONE_RADIUS;
                            break;
                        case CONTROLLER_1_DOWN:
                        case CONTROLLER_2_DOWN:
                            pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_Y && axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) > DEAD_ZONE_RADIUS;
                            break;
                        case CONTROLLER_1_RIGHT:
                        case CONTROLLER_2_RIGHT:
                            pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_X && axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) > DEAD_ZONE_RADIUS;
                            break;
                        case CONTROLLER_1_LEFT:
                        case CONTROLLER_2_LEFT:
                            pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_X && axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) < -DEAD_ZONE_RADIUS;
                            break;
                    }
                }
                pressed = pressed || buttons.get(mappedControlsJoystick.get(input)) == GLFW_PRESS;
            }
        }
        return pressed || glfwGetKey(window, mappedControlsKeyboard.get(input)) == GLFW_PRESS;

    }

    /**
     * Populate the map to facilitate mapping between key names and key IDs
     */
    private void initButtonNames() {
        buttonNames.put("BUTTON_A", 0);
        buttonNames.put("BUTTON_B", 1);
        buttonNames.put("BUTTON_X", 2);
        buttonNames.put("BUTTON_Y", 3);
        buttonNames.put("BUTTON_LEFT_BUMPER", 4);
        buttonNames.put("BUTTON_RIGHT_BUMPER", 5);
        buttonNames.put("BUTTON_BACK", 6);
        buttonNames.put("BUTTON_START", 7);
        buttonNames.put("BUTTON_GUIDE", 8);
        buttonNames.put("BUTTON_DPAD_UP", 10);
        buttonNames.put("BUTTON_DPAD_RIGHT", 11);
        buttonNames.put("BUTTON_DPAD_DOWN", 12);
        buttonNames.put("BUTTON_DPAD_LEFT", 13);
        buttonNames.put("BUTTON_LAST", 14);
        buttonNames.put("BUTTON_CROSS", 15);
        buttonNames.put("BUTTON_CIRCLE", 16);
        buttonNames.put("BUTTON_SQUARE", 17);
        buttonNames.put("BUTTON_TRIANGLE", 18);
        buttonNames.put("KEY_0", 48);
        buttonNames.put("KEY_1", 49);
        buttonNames.put("KEY_2", 50);
        buttonNames.put("KEY_3", 51);
        buttonNames.put("KEY_4", 52);
        buttonNames.put("KEY_5", 53);
        buttonNames.put("KEY_6", 54);
        buttonNames.put("KEY_7", 55);
        buttonNames.put("KEY_8", 56);
        buttonNames.put("KEY_9", 57);
        buttonNames.put("KEY_SEMICOLON", 59);
        buttonNames.put("KEY_EQUAL", 61);
        buttonNames.put("KEY_A", 65);
        buttonNames.put("KEY_B", 66);
        buttonNames.put("KEY_C", 67);
        buttonNames.put("KEY_D", 68);
        buttonNames.put("KEY_E", 69);
        buttonNames.put("KEY_F", 70);
        buttonNames.put("KEY_G", 71);
        buttonNames.put("KEY_H", 72);
        buttonNames.put("KEY_I", 73);
        buttonNames.put("KEY_J", 74);
        buttonNames.put("KEY_K", 75);
        buttonNames.put("KEY_L", 76);
        buttonNames.put("KEY_M", 77);
        buttonNames.put("KEY_N", 78);
        buttonNames.put("KEY_O", 79);
        buttonNames.put("KEY_P", 80);
        buttonNames.put("KEY_Q", 81);
        buttonNames.put("KEY_R", 82);
        buttonNames.put("KEY_S", 83);
        buttonNames.put("KEY_T", 84);
        buttonNames.put("KEY_U", 85);
        buttonNames.put("KEY_V", 86);
        buttonNames.put("KEY_W", 87);
        buttonNames.put("KEY_X", 88);
        buttonNames.put("KEY_Y", 89);
        buttonNames.put("KEY_Z", 90);
        buttonNames.put("KEY_LEFT_BRACKET", 91);
        buttonNames.put("KEY_BACKSLASH", 92);
        buttonNames.put("KEY_RIGHT_BRACKET", 93);
        buttonNames.put("KEY_GRAVE_ACCENT", 96);
        buttonNames.put("KEY_WORLD_1", 161);
        buttonNames.put("KEY_WORLD_2", 162);
        buttonNames.put("KEY_ESCAPE", 256);
        buttonNames.put("KEY_ENTER", 257);
        buttonNames.put("KEY_TAB", 258);
        buttonNames.put("KEY_BACKSPACE", 259);
        buttonNames.put("KEY_INSERT", 260);
        buttonNames.put("KEY_DELETE", 261);
        buttonNames.put("KEY_RIGHT", 262);
        buttonNames.put("KEY_LEFT", 263);
        buttonNames.put("KEY_DOWN", 264);
        buttonNames.put("KEY_UP", 265);
        buttonNames.put("KEY_PAGE_UP", 266);
        buttonNames.put("KEY_PAGE_DOWN", 267);
        buttonNames.put("KEY_HOME", 268);
        buttonNames.put("KEY_END", 269);
        buttonNames.put("KEY_CAPS_LOCK", 280);
        buttonNames.put("KEY_SCROLL_LOCK", 281);
        buttonNames.put("KEY_NUM_LOCK", 282);
        buttonNames.put("KEY_PRINT_SCREEN", 283);
        buttonNames.put("KEY_PAUSE", 284);
        buttonNames.put("KEY_F1", 290);
        buttonNames.put("KEY_F2", 291);
        buttonNames.put("KEY_F3", 292);
        buttonNames.put("KEY_F4", 293);
        buttonNames.put("KEY_F5", 294);
        buttonNames.put("KEY_F6", 295);
        buttonNames.put("KEY_F7", 296);
        buttonNames.put("KEY_F8", 297);
        buttonNames.put("KEY_F9", 298);
        buttonNames.put("KEY_F10", 299);
        buttonNames.put("KEY_F11", 300);
        buttonNames.put("KEY_F12", 301);
        buttonNames.put("KEY_F13", 302);
        buttonNames.put("KEY_F14", 303);
        buttonNames.put("KEY_F15", 304);
        buttonNames.put("KEY_F16", 305);
        buttonNames.put("KEY_F17", 306);
        buttonNames.put("KEY_F18", 307);
        buttonNames.put("KEY_F19", 308);
        buttonNames.put("KEY_F20", 309);
        buttonNames.put("KEY_F21", 310);
        buttonNames.put("KEY_F22", 311);
        buttonNames.put("KEY_F23", 312);
        buttonNames.put("KEY_F24", 313);
        buttonNames.put("KEY_F25", 314);
        buttonNames.put("KEY_KP_0", 320);
        buttonNames.put("KEY_KP_1", 321);
        buttonNames.put("KEY_KP_2", 322);
        buttonNames.put("KEY_KP_3", 323);
        buttonNames.put("KEY_KP_4", 324);
        buttonNames.put("KEY_KP_5", 325);
        buttonNames.put("KEY_KP_6", 326);
        buttonNames.put("KEY_KP_7", 327);
        buttonNames.put("KEY_KP_8", 328);
        buttonNames.put("KEY_KP_9", 329);
        buttonNames.put("KEY_KP_DECIMAL", 330);
        buttonNames.put("KEY_KP_DIVIDE", 331);
        buttonNames.put("KEY_KP_MULTIPLY", 332);
        buttonNames.put("KEY_KP_SUBTRACT", 333);
        buttonNames.put("KEY_KP_ADD", 334);
        buttonNames.put("KEY_KP_ENTER", 335);
        buttonNames.put("KEY_KP_EQUAL", 336);
        buttonNames.put("KEY_LEFT_SHIFT", 340);
        buttonNames.put("KEY_LEFT_CONTROL", 341);
        buttonNames.put("KEY_LEFT_ALT", 342);
        buttonNames.put("KEY_LEFT_SUPER", 343);
        buttonNames.put("KEY_RIGHT_SHIFT", 344);
        buttonNames.put("KEY_RIGHT_CONTROL", 345);
        buttonNames.put("KEY_RIGHT_ALT", 346);
        buttonNames.put("KEY_RIGHT_SUPER", 347);
        buttonNames.put("KEY_MENU", 348);
        buttonNames.put("KEY_LAST", 348);
    }

    /**
     * Load the default mapping
     */
    private void loadDefaultControls() {
        mappedControlsJoystick.put(CONTROLLER_1_A, GLFW_GAMEPAD_BUTTON_A);
        mappedControlsJoystick.put(CONTROLLER_1_B, GLFW_GAMEPAD_BUTTON_B);
        mappedControlsJoystick.put(CONTROLLER_1_SELECT, GLFW_GAMEPAD_BUTTON_BACK);
        mappedControlsJoystick.put(CONTROLLER_1_START, GLFW_GAMEPAD_BUTTON_START);
        mappedControlsJoystick.put(CONTROLLER_1_UP, GLFW_GAMEPAD_BUTTON_DPAD_UP - 1);
        mappedControlsJoystick.put(CONTROLLER_1_DOWN, GLFW_GAMEPAD_BUTTON_DPAD_DOWN - 1);
        mappedControlsJoystick.put(CONTROLLER_1_LEFT, GLFW_GAMEPAD_BUTTON_DPAD_LEFT - 1);
        mappedControlsJoystick.put(CONTROLLER_1_RIGHT, GLFW_GAMEPAD_BUTTON_DPAD_RIGHT - 1);
        mappedControlsKeyboard.put(CONTROLLER_1_A, GLFW_KEY_I);
        mappedControlsKeyboard.put(CONTROLLER_1_B, GLFW_KEY_U);
        mappedControlsKeyboard.put(CONTROLLER_1_SELECT, GLFW_KEY_J);
        mappedControlsKeyboard.put(CONTROLLER_1_START, GLFW_KEY_K);
        mappedControlsKeyboard.put(CONTROLLER_1_UP, GLFW_KEY_UP);
        mappedControlsKeyboard.put(CONTROLLER_1_DOWN, GLFW_KEY_DOWN);
        mappedControlsKeyboard.put(CONTROLLER_1_LEFT, GLFW_KEY_LEFT);
        mappedControlsKeyboard.put(CONTROLLER_1_RIGHT, GLFW_KEY_RIGHT);

        mappedControlsJoystick.put(CONTROLLER_2_A, GLFW_GAMEPAD_BUTTON_A);
        mappedControlsJoystick.put(CONTROLLER_2_B, GLFW_GAMEPAD_BUTTON_B);
        mappedControlsJoystick.put(CONTROLLER_2_SELECT, GLFW_GAMEPAD_BUTTON_BACK);
        mappedControlsJoystick.put(CONTROLLER_2_START, GLFW_GAMEPAD_BUTTON_START);
        mappedControlsJoystick.put(CONTROLLER_2_UP, GLFW_GAMEPAD_BUTTON_DPAD_UP - 1);
        mappedControlsJoystick.put(CONTROLLER_2_DOWN, GLFW_GAMEPAD_BUTTON_DPAD_DOWN - 1);
        mappedControlsJoystick.put(CONTROLLER_2_LEFT, GLFW_GAMEPAD_BUTTON_DPAD_LEFT - 1);
        mappedControlsJoystick.put(CONTROLLER_2_RIGHT, GLFW_GAMEPAD_BUTTON_DPAD_RIGHT - 1);
        mappedControlsKeyboard.put(CONTROLLER_2_A, GLFW_KEY_Y);
        mappedControlsKeyboard.put(CONTROLLER_2_B, GLFW_KEY_T);
        mappedControlsKeyboard.put(CONTROLLER_2_SELECT, GLFW_KEY_G);
        mappedControlsKeyboard.put(CONTROLLER_2_START, GLFW_KEY_H);
        mappedControlsKeyboard.put(CONTROLLER_2_UP, GLFW_KEY_W);
        mappedControlsKeyboard.put(CONTROLLER_2_DOWN, GLFW_KEY_S);
        mappedControlsKeyboard.put(CONTROLLER_2_LEFT, GLFW_KEY_A);
        mappedControlsKeyboard.put(CONTROLLER_2_RIGHT, GLFW_KEY_D);
    }

    /**
     * Load the input mapping from an external file
     * Load the default mapping if an error occur
     */
    private void loadConfig() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File fileXML = new File("input_mapping.xml");
            Document xml;
            xml = builder.parse(fileXML);
            Element controls = (Element)xml.getElementsByTagName("controls").item(0);
            if (controls == null)
                throw new InvalidFileException("input_mapping.xml file corrupted (controls node not found)");
            Element keyboard = (Element)controls.getElementsByTagName("keyboard").item(0);
            if (keyboard == null)
                throw new InvalidFileException("input_mapping.xml file corrupted (keyboard node not found)");
            NodeList keys = keyboard.getElementsByTagName("key");
            Element joystick = (Element)controls.getElementsByTagName("joystick").item(0);
            if (joystick == null)
                throw new InvalidFileException("input_mapping.xml file corrupted (joystick node not found)");
            NodeList buttons = joystick.getElementsByTagName("button");

            for (int i = 0; i < keys.getLength(); i++) {
                Element e = (Element) keys.item(i);
                String id = e.getAttribute("id");
                String keycode = e.getTextContent();
                mappedControlsKeyboard.put(GamepadInputs.valueOf(id), buttonNames.get(keycode));
            }
            for (int i = 0; i < buttons.getLength(); i++) {
                Element e = (Element) buttons.item(i);
                String id = e.getAttribute("id");
                String keycode = e.getTextContent();
                mappedControlsJoystick.put(GamepadInputs.valueOf(id), buttonNames.get(keycode));
            }

        } catch (Exception e) {
            loadDefaultControls();
        }
    }
}
