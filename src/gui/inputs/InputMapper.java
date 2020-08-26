package gui.inputs;

import exceptions.InvalidFileException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static gui.inputs.NESInputs.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * This class handle the mapping between User Input and the NES
 */
public class InputMapper {

    private static final float DEAD_ZONE_RADIUS = .4f;

    private final long window;

    private final Map<NESInputs, Integer> mappedControlsJoystick;
    private final Map<NESInputs, Integer> mappedControlsKeyboard;
    private Map<Integer, String> keyNames;
    private Map<Integer, String> buttonNames;

    /**
     * Create a new InputMapper and load the config file
     *
     * @param window the OpenGL window ID used to get the events from
     */
    public InputMapper(long window) {
        this.window = window;
        mappedControlsJoystick = new HashMap<>();
        mappedControlsKeyboard = new HashMap<>();
        initKeyNames();
        loadConfig();
    }
    
    private void initKeyNames() {
        keyNames = new HashMap<>();
        buttonNames = new HashMap<>();

        keyNames.put(GLFW_KEY_UNKNOWN, "???");
        keyNames.put(GLFW_KEY_SPACE, "Space");
        keyNames.put(GLFW_KEY_APOSTROPHE, "'");
        keyNames.put(GLFW_KEY_COMMA, ",");
        keyNames.put(GLFW_KEY_MINUS, "-");
        keyNames.put(GLFW_KEY_PERIOD, "Period");
        keyNames.put(GLFW_KEY_SLASH, "/");
        keyNames.put(GLFW_KEY_0, "0");
        keyNames.put(GLFW_KEY_1, "1");
        keyNames.put(GLFW_KEY_2, "2");
        keyNames.put(GLFW_KEY_3, "3");
        keyNames.put(GLFW_KEY_4, "4");
        keyNames.put(GLFW_KEY_5, "5");
        keyNames.put(GLFW_KEY_6, "6");
        keyNames.put(GLFW_KEY_7, "7");
        keyNames.put(GLFW_KEY_8, "8");
        keyNames.put(GLFW_KEY_9, "9");
        keyNames.put(GLFW_KEY_SEMICOLON, ";");
        keyNames.put(GLFW_KEY_EQUAL, "=");
        keyNames.put(GLFW_KEY_A, "A");
        keyNames.put(GLFW_KEY_B, "B");
        keyNames.put(GLFW_KEY_C, "C");
        keyNames.put(GLFW_KEY_D, "D");
        keyNames.put(GLFW_KEY_E, "E");
        keyNames.put(GLFW_KEY_F, "F");
        keyNames.put(GLFW_KEY_G, "G");
        keyNames.put(GLFW_KEY_H, "H");
        keyNames.put(GLFW_KEY_I, "I");
        keyNames.put(GLFW_KEY_J, "J");
        keyNames.put(GLFW_KEY_K, "K");
        keyNames.put(GLFW_KEY_L, "L");
        keyNames.put(GLFW_KEY_M, "M");
        keyNames.put(GLFW_KEY_N, "N");
        keyNames.put(GLFW_KEY_O, "O");
        keyNames.put(GLFW_KEY_P, "P");
        keyNames.put(GLFW_KEY_Q, "Q");
        keyNames.put(GLFW_KEY_R, "R");
        keyNames.put(GLFW_KEY_S, "S");
        keyNames.put(GLFW_KEY_T, "T");
        keyNames.put(GLFW_KEY_U, "U");
        keyNames.put(GLFW_KEY_V, "V");
        keyNames.put(GLFW_KEY_W, "W");
        keyNames.put(GLFW_KEY_X, "X");
        keyNames.put(GLFW_KEY_Y, "Y");
        keyNames.put(GLFW_KEY_Z, "Z");
        keyNames.put(GLFW_KEY_LEFT_BRACKET, "[");
        keyNames.put(GLFW_KEY_BACKSLASH, "\\");
        keyNames.put(GLFW_KEY_RIGHT_BRACKET, "]");
        keyNames.put(GLFW_KEY_GRAVE_ACCENT, "???");
        keyNames.put(GLFW_KEY_WORLD_1, "???");
        keyNames.put(GLFW_KEY_WORLD_2, "???");
        keyNames.put(GLFW_KEY_ESCAPE, "Escape");
        keyNames.put(GLFW_KEY_ENTER, "Enter");
        keyNames.put(GLFW_KEY_TAB, "Tab");
        keyNames.put(GLFW_KEY_BACKSPACE, "Backspace");
        keyNames.put(GLFW_KEY_INSERT, "Insert");
        keyNames.put(GLFW_KEY_DELETE, "Delete");
        keyNames.put(GLFW_KEY_RIGHT, "Right");
        keyNames.put(GLFW_KEY_LEFT, "Left");
        keyNames.put(GLFW_KEY_DOWN, "Down");
        keyNames.put(GLFW_KEY_UP, "Up");
        keyNames.put(GLFW_KEY_PAGE_UP, "Page Up");
        keyNames.put(GLFW_KEY_PAGE_DOWN, "Page Down");
        keyNames.put(GLFW_KEY_HOME, "Home");
        keyNames.put(GLFW_KEY_END, "End");
        keyNames.put(GLFW_KEY_CAPS_LOCK, "Caps Lock");
        keyNames.put(GLFW_KEY_SCROLL_LOCK, "Scroll Lock");
        keyNames.put(GLFW_KEY_NUM_LOCK, "Num Lock");
        keyNames.put(GLFW_KEY_PRINT_SCREEN, "Print Screen");
        keyNames.put(GLFW_KEY_PAUSE, "Pause");
        keyNames.put(GLFW_KEY_F1, "F1");
        keyNames.put(GLFW_KEY_F2, "F2");
        keyNames.put(GLFW_KEY_F3, "F3");
        keyNames.put(GLFW_KEY_F4, "F4");
        keyNames.put(GLFW_KEY_F5, "F5");
        keyNames.put(GLFW_KEY_F6, "F6");
        keyNames.put(GLFW_KEY_F7, "F7");
        keyNames.put(GLFW_KEY_F8, "F8");
        keyNames.put(GLFW_KEY_F9, "F9");
        keyNames.put(GLFW_KEY_F10, "F10");
        keyNames.put(GLFW_KEY_F11, "F11");
        keyNames.put(GLFW_KEY_F12, "F12");
        keyNames.put(GLFW_KEY_F13, "F13");
        keyNames.put(GLFW_KEY_F14, "F14");
        keyNames.put(GLFW_KEY_F15, "F15");
        keyNames.put(GLFW_KEY_F16, "F16");
        keyNames.put(GLFW_KEY_F17, "F17");
        keyNames.put(GLFW_KEY_F18, "F18");
        keyNames.put(GLFW_KEY_F19, "F19");
        keyNames.put(GLFW_KEY_F20, "F20");
        keyNames.put(GLFW_KEY_F21, "F21");
        keyNames.put(GLFW_KEY_F22, "F22");
        keyNames.put(GLFW_KEY_F23, "F23");
        keyNames.put(GLFW_KEY_F24, "F24");
        keyNames.put(GLFW_KEY_F25, "F25");
        keyNames.put(GLFW_KEY_KP_0, "0 (KP)");
        keyNames.put(GLFW_KEY_KP_1, "1 (KP)");
        keyNames.put(GLFW_KEY_KP_2, "2 (KP)");
        keyNames.put(GLFW_KEY_KP_3, "3 (KP)");
        keyNames.put(GLFW_KEY_KP_4, "4 (KP)");
        keyNames.put(GLFW_KEY_KP_5, "5 (KP)");
        keyNames.put(GLFW_KEY_KP_6, "6 (KP)");
        keyNames.put(GLFW_KEY_KP_7, "7 (KP)");
        keyNames.put(GLFW_KEY_KP_8, "8 (KP)");
        keyNames.put(GLFW_KEY_KP_9, "9 (KP)");
        keyNames.put(GLFW_KEY_KP_DECIMAL, ". (KP)");
        keyNames.put(GLFW_KEY_KP_DIVIDE, "/ (KP)");
        keyNames.put(GLFW_KEY_KP_MULTIPLY, "* (KP)");
        keyNames.put(GLFW_KEY_KP_SUBTRACT, "- (KP)");
        keyNames.put(GLFW_KEY_KP_ADD, "+ (KP)");
        keyNames.put(GLFW_KEY_KP_ENTER, "Enter (KP)");
        keyNames.put(GLFW_KEY_KP_EQUAL, "= (KP)");
        keyNames.put(GLFW_KEY_LEFT_SHIFT, "Left Shift");
        keyNames.put(GLFW_KEY_LEFT_CONTROL, "Left Control");
        keyNames.put(GLFW_KEY_LEFT_ALT, "Left Alt");
        keyNames.put(GLFW_KEY_LEFT_SUPER, "Left Super");
        keyNames.put(GLFW_KEY_RIGHT_SHIFT, "Right Shift");
        keyNames.put(GLFW_KEY_RIGHT_CONTROL, "Right Control");
        keyNames.put(GLFW_KEY_RIGHT_ALT, "Right Alt");
        keyNames.put(GLFW_KEY_RIGHT_SUPER, "Right Super");
        keyNames.put(GLFW_KEY_MENU, "Menu");

        buttonNames.put(GLFW_GAMEPAD_BUTTON_A, "A");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_B, "B");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_X, "X");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_Y, "Y");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER, "LB");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER, "RB");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_BACK, "Back");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_START, "Start");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_GUIDE, "Guide");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_LEFT_THUMB, "Left Thumb");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_DPAD_UP - 1, "DPad Up");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT - 1, "DPad Right");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_DPAD_DOWN - 1, "DPad Down");
        buttonNames.put(GLFW_GAMEPAD_BUTTON_DPAD_LEFT - 1, "DPad Left");
    }

    /**
     * Return whether or not an Input is pressed or not
     * account for Keyboard and Gamepads
     *
     * @param input  the input to test
     * @param player the player ID
     * @return is the input pressed by the specified player
     */
    public boolean isPressed(NESInputs input, int player) {
        boolean pressed = false;
        //If a controller is connected we test for its inputs
        //otherwise we only test for the keyboard
        if (glfwJoystickPresent(player % 2 == 1 ? GLFW_JOYSTICK_1 : GLFW_JOYSTICK_2)) {
            ByteBuffer buttons = glfwGetJoystickButtons(player % 2 == 1 ? GLFW_JOYSTICK_1 : GLFW_JOYSTICK_2);
            FloatBuffer axes = glfwGetJoystickAxes(player % 2 == 1 ? GLFW_JOYSTICK_1 : GLFW_JOYSTICK_2);
            if (buttons != null && buttons.capacity() > mappedControlsJoystick.get(input)) {
                if (axes != null) {
                    switch (input) {
                        case CONTROLLER_1_UP, CONTROLLER_2_UP -> pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_Y && axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) < -DEAD_ZONE_RADIUS;
                        case CONTROLLER_1_DOWN, CONTROLLER_2_DOWN -> pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_Y && axes.get(GLFW_GAMEPAD_AXIS_LEFT_Y) > DEAD_ZONE_RADIUS;
                        case CONTROLLER_1_RIGHT, CONTROLLER_2_RIGHT -> pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_X && axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) > DEAD_ZONE_RADIUS;
                        case CONTROLLER_1_LEFT, CONTROLLER_2_LEFT -> pressed = axes.capacity() > GLFW_GAMEPAD_AXIS_LEFT_X && axes.get(GLFW_GAMEPAD_AXIS_LEFT_X) < -DEAD_ZONE_RADIUS;
                    }
                }
                pressed = pressed || buttons.get(mappedControlsJoystick.get(input)) == GLFW_PRESS;
            }
        }
        return pressed || glfwGetKey(window, mappedControlsKeyboard.get(input)) == GLFW_PRESS;

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
            Element controls = (Element) xml.getElementsByTagName("controls").item(0);
            if (controls == null)
                throw new InvalidFileException("input_mapping.xml file corrupted (controls node not found)");
            Element keyboard = (Element) controls.getElementsByTagName("keyboard").item(0);
            if (keyboard == null)
                throw new InvalidFileException("input_mapping.xml file corrupted (keyboard node not found)");
            NodeList keys = keyboard.getElementsByTagName("key");
            Element joystick = (Element) controls.getElementsByTagName("joystick").item(0);
            if (joystick == null)
                throw new InvalidFileException("input_mapping.xml file corrupted (joystick node not found)");
            NodeList buttons = joystick.getElementsByTagName("button");

            for (int i = 0; i < keys.getLength(); i++) {
                Element e = (Element) keys.item(i);
                String id = e.getAttribute("id");
                String keycode = e.getTextContent();
                mappedControlsKeyboard.put(NESInputs.valueOf(id), Integer.valueOf(keycode));
            }
            for (int i = 0; i < buttons.getLength(); i++) {
                Element e = (Element) buttons.item(i);
                String id = e.getAttribute("id");
                String keycode = e.getTextContent();
                mappedControlsJoystick.put(NESInputs.valueOf(id),  Integer.valueOf(keycode));
            }

        } catch (Exception e) {
            loadDefaultControls();
        }
    }

    /**
     * Save the input mapping to an external file
     */
    private void saveConfig() {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootNode = document.createElement("controls");
            Element keyboardNode = document.createElement("keyboard");

            for (Map.Entry<NESInputs, Integer> entry : mappedControlsKeyboard.entrySet()) {
                Element input = document.createElement("key");
                input.setAttribute("id", entry.getKey().name());
                input.setTextContent(String.valueOf(entry.getValue()));
                keyboardNode.appendChild(input);
            }
            rootNode.appendChild(keyboardNode);

            Element buttonNode = document.createElement("joystick");
            for (Map.Entry<NESInputs, Integer> entry : mappedControlsJoystick.entrySet()) {
                Element input = document.createElement("button");
                input.setAttribute("id", entry.getKey().name());
                input.setTextContent(String.valueOf(entry.getValue()));
                buttonNode.appendChild(input);
            }
            rootNode.appendChild(buttonNode);
            document.appendChild(rootNode);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("input_mapping.xml"));
            transformer.transform(domSource, streamResult);

        } catch (TransformerException | ParserConfigurationException pce) {
            pce.printStackTrace();
        }
    }

    /**
     * Return the name of the key mapped to an Input
     *
     * @param input the input to get the key of
     * @return the name of the key
     */
    public String getMappedKey(NESInputs input) {
        return keyNames.get(mappedControlsKeyboard.get(input));
    }

    /**
     * Get the currently pressed key
     *
     * @param window the window in which to capture the press
     * @return a KeyTuple containing the id and name of the pressed key, null if none
     */
    public KeyTuple getKeyPressed(long window) {
        for (Map.Entry<Integer, String> key : keyNames.entrySet()) {
            if (key.getKey() > 0 && glfwGetKey(window, key.getKey()) == GLFW_PRESS)
                return new KeyTuple(key.getKey(), key.getValue());
        }
        return null;
    }

    /**
     * Set a Controller Input to a specific key
     *
     * @param input the Input to set
     * @param key   the key that should trigger it
     */
    public void setMappedKey(NESInputs input, int key) {
        mappedControlsKeyboard.replace(input, key);
        saveConfig();
    }

    /**
     * Return the name of the gamepad button mapped to an Input
     *
     * @param input the input to get the button of
     * @return the name of the button
     */
    public String getMappedButton(NESInputs input) {
        return buttonNames.get(mappedControlsJoystick.get(input));
    }

    /**
     * Get the currently pressed gamepad button if a gamepad is connected
     *
     * @return a KeyTuple containing the id and name of the pressed button, null if none
     */
    public KeyTuple getButtonPressed() {
        if (glfwJoystickPresent(GLFW_JOYSTICK_1)) {
            ByteBuffer buttons = glfwGetJoystickButtons(GLFW_JOYSTICK_1);
            if (buttons != null) {
                for (Map.Entry<Integer, String> button : buttonNames.entrySet()) {
                    if (buttons.get(button.getKey()) == GLFW_PRESS)
                        return new KeyTuple(button.getKey(), button.getValue());
                }
            }
        }
        return null;
    }

    /**
     * Set a Controller Input to a specific gamepad button
     *
     * @param input  the Input to set
     * @param button the button that should trigger it
     */
    public void setMappedButton(NESInputs input, int button) {
        mappedControlsJoystick.replace(input, button);
        saveConfig();
    }
}
